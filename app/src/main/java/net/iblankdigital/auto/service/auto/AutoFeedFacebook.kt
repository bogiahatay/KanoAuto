package net.iblankdigital.auto.service.auto

import android.os.Handler
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import com.google.mlkit.vision.text.Text
import net.iblankdigital.auto.ext.delay
import net.iblankdigital.auto.ml.MLKitText
import net.iblankdigital.auto.service.autoClickService
import net.iblankdigital.auto.service.capture.ScreenshotManager
import net.iblankdigital.auto.utils.MLog
import net.iblankdigital.auto.utils.MethodUtils
import net.iblankdigital.auto.service.log.LogAuto
import net.iblankdigital.auto.service.statusBarHeight
import net.iblankdigital.auto.service.widgetService
import net.iblankdigital.auto.utils.StringUtils
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

object AutoFeedFacebook : AutoBase() {
    val TAG = "AutoFeedFacebook"

    var timeDetect: Long = 2000
    val packageName = "com.facebook.katana";

    fun toggle(): Boolean {
        isRunning = !isRunning

        if (isRunning) {
            addDetectView()
            startAuto()
        } else {
            removeDetectView()
        }
        return isRunning
    }

    fun onConfigurationChanged(isLand: Boolean) {
        if (isRunning) {
            addDetectView()
        }
    }

    fun addDetectView() {
        removeDetectView()
//        arrDetectView.addAll(widgetService.addViewDetect(x1, y1.toPixel(heightDevice), size1Width, size1Height))
//        arrDetectView.addAll(widgetService.addViewDetect(x2, y2.toPixel(heightDevice), size2Width, size2Height))
    }

    fun removeDetectView() {
        widgetService.removeDetectView(arrDetectView)
        arrDetectView.clear()
    }


    fun startAuto() {
        if (isRunning) {

            if (MethodUtils.isAppRunning(context, packageName)) {
                timeDetect = 2000
                LogAuto.log("Facebook", "onTopScreen")
                feed()
            } else {
                LogAuto.log("Facebook", "...")
                reStartApp()
                timeDetect = 5000
                retry()
            }
        }
    }

    private fun reStartApp() {
        try {
            MLog.e("ReStart Facebook")
            MethodUtils.killApp(context, packageName)
            Handler().postDelayed({
                //http://rnaura.com/blog-post/open-facebook-with-intent-in-android-2/
                MethodUtils.startApp(context, packageName)
            }, 2000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val x1 = statusBarHeight + 10
    val y1 = 30F
    val size1Width = 20
    val size1Height = 20

    val x2 = statusBarHeight + 10
    val y2 = 90F
    val size2Width = 20
    val size2Height = 20

    private fun feed() {
        ScreenshotManager.getInstance().takeScreenshot { bitmap, timeCreate ->
            if (bitmap != null) {
//                widgetService.imvPreview.visibility()
//                widgetService.imvPreview.setImageBitmap(bitmap)
                MLKitText.detectBitmap(bitmap, object : MLKitText.IOnTextRecognizer {
                    override fun onSuccess(arrTextBlock: List<Text.TextBlock>) {
                        if (isScreenHome(arrTextBlock)) {
                            LogAuto.log("HomeScreen", "true")
                        } else {
                            LogAuto.log("HomeScreen", "false")
                        }
                        if (isScreenComments(arrTextBlock)) {
                            timeDetect = 10000
                            delay(6000) {
                                LogAuto.log("Action", "Hide Keyboard")
                                autoClickService?.hideKeyboard()
                            }
                            delay(8000) {
                                LogAuto.log("Action", "Press Back")
                                autoClickService?.back()
                            }
                            LogAuto.log("Screen", "Comments");
                        } else {
                            timeDetect = 2000
                            LogAuto.log("Screen", "Home");
                            if (isViewComments(arrTextBlock)) {
                                timeDetect = 5000
                                LogAuto.log("Action", "View Comments");
                            } else {
                                if (isLike(arrTextBlock)) {
                                    LogAuto.log("Action", "Like");
                                } else {
                                    if (isScroll()) {
                                        LogAuto.log("Action", "Scroll");
                                    }
                                }
                            }

                        }
                        retry()
                    }

                    override fun onError(why: String) {
                        retry()
                    }
                })
            } else {
                retry()
                MLog.e("ScreenshotManager ERROR")
            }
        }
    }


    private var lastLike = 0L

    private fun isLike(arrTextBlock: List<Text.TextBlock>): Boolean {
        if (Calendar.getInstance().timeInMillis - lastLike < 30000) {
            return false
        }
        val str1 = "thich"
        val str2 = "binh luan"
        val str3 = "chia se"

        var blockLike: Text.TextBlock? = null
        var blockComments: Text.TextBlock? = null
        var blockShare: Text.TextBlock? = null

        if (Random.nextInt() % 2 == 0) {
            for (textBlock in arrTextBlock) {
                val text = StringUtils.removeAccent(textBlock.text).toLowerCase().trim()
                MLog.e(TAG, text)
                if (text.contains(str1)) {
                    blockLike = textBlock
                    MLog.e(TAG, text)
                }
                if (text.contains(str2)) {
                    blockComments = textBlock
                    MLog.e(TAG, text)
                }
                if (text.contains(str3)) {
                    blockShare = textBlock
                    MLog.e(TAG, text)
                }
            }
            if (blockLike != null && blockComments != null && blockShare != null) {
                val avg = (blockLike.boundingBox!!.top + blockComments.boundingBox!!.top + blockShare.boundingBox!!.top) / 3
                if (abs(blockLike.boundingBox!!.top - avg) < 30) {
                    LogAuto.log("Action", "Click Like")
                    lastLike = Calendar.getInstance().timeInMillis;
                    autoClickService?.click(blockLike.boundingBox!!.left + blockLike.boundingBox!!.width() / 2, blockLike.boundingBox!!.top + blockLike.boundingBox!!.height() / 2)
                    return true
                }
            }
        }
        return false
    }

    private var lastViewComments = 0L

    private fun isViewComments(arrTextBlock: List<Text.TextBlock>): Boolean {
        if (Calendar.getInstance().timeInMillis - lastViewComments < 30000) {
            return false
        }
        val str1 = "thich"
        val str2 = "binh luan"
        val str3 = "chia se"

        var blockLike: Text.TextBlock? = null
        var blockComments: Text.TextBlock? = null
        var blockShare: Text.TextBlock? = null

        if (Random.nextInt() % 5 == 0) {
            for (textBlock in arrTextBlock) {
                val text = StringUtils.removeAccent(textBlock.text).toLowerCase().trim()
                MLog.e(TAG, text)
                if (text.contains(str1)) {
                    blockLike = textBlock
                    MLog.e(TAG, text)
                }
                if (text.contains(str2)) {
                    blockComments = textBlock
                    MLog.e(TAG, text)
                }
                if (text.contains(str3)) {
                    blockShare = textBlock
                    MLog.e(TAG, text)
                }
            }
            if (blockLike != null && blockComments != null && blockShare != null) {
                val avg = (blockLike.boundingBox!!.top + blockComments.boundingBox!!.top + blockShare.boundingBox!!.top) / 3
                if (abs(blockLike.boundingBox!!.top - avg) < 30) {
                    LogAuto.log("Action", "Click ViewComments")
                    lastViewComments = Calendar.getInstance().timeInMillis
                    autoClickService?.click(blockComments.boundingBox!!.left + blockComments.boundingBox!!.width() / 2, blockComments.boundingBox!!.top + blockComments.boundingBox!!.height() / 2)
                    return true
                }
            }
        }
        return false
    }

    private fun isScroll(): Boolean {
        if (Random.nextBoolean()) {
            autoClickService?.scroll();
            return true
        }
        return false
    }

    private fun isScreenComments(arrTextBlock: List<Text.TextBlock>): Boolean {
        val str1 = "viet binh luan"

        val str2 = "phu hop nhat"
        val str3 = "moi nhat"
        val str4 = "tat ca binh luan"
        val str5 = "chua co binh luan nao"
        val str6 = "cu nhat"

        var blockTypeComment: Text.TextBlock? = null
        var blockComments: Text.TextBlock? = null

        for (textBlock in arrTextBlock) {
            val text = StringUtils.removeAccent(textBlock.text).toLowerCase()
            if (text.contains(str1)) {
                blockComments = textBlock
            }
            if (text.contains(str2)
                    || text.contains(str3)
                    || text.contains(str4)
                    || text.contains(str5)
                    || text.contains(str6)
            ) {
                blockTypeComment = textBlock
            }
        }
        if (blockComments != null && blockTypeComment != null) {
            return true
        }
        return false
    }

    private fun isScreenHome(arrText: List<Text.TextBlock>): Boolean {

        return false
    }

    private fun retry() {
        delay(timeDetect) {
            startAuto()
        }
    }

    fun release() {
        isRunning = false
        removeDetectView()
    }

    fun showSettings(btn: View) {

        val popup = PopupMenu(context, btn)
        val menu: Menu = popup.menu

        val configDB = getConfig()
        configDB.arrDetectFishing.forEach {
            menu.add(0, it.id, 0, it.title)
        }
        menu.add(1, 1, 1, "Opt 1")
        menu.add(2, 2, 2, "Opt 2")
        menu.add(3, 3, 3, "Opt 3")
        menu.add(4, 4, 4, "Opt 4")
        menu.add(5, 5, 5, "Opt 5")

        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == 1
                    || item.itemId == 2
                    || item.itemId == 3
                    || item.itemId == 4
                    || item.itemId == 5
                    || item.itemId == 6) {
            }
            true
        }
        popup.show()
    }
}





