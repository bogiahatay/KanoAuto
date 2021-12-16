package com.kano.auto.service.auto

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Process
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import com.kano.auto.base.PushNotifySpec
import com.kano.auto.db.api.DetectFishing
import com.kano.auto.ext.*
import com.kano.auto.service.autoClickService
import com.kano.auto.service.capture.ScreenshotManager
import com.kano.auto.service.log.LogAuto
import com.kano.auto.service.widgetService
import com.kano.auto.utils.MLog
import com.kano.auto.utils.MethodUtils

object AutoFishingPlayTogether : AutoBase() {
    val TAG = "AutoFishing"
    lateinit var detect: DetectFishing

    fun toggle(): Boolean {
        isRunning = !isRunning
        //reset param
        countError = 0
        lastTimeSuccess = System.currentTimeMillis()
        lastTimeKeo = System.currentTimeMillis()
        if (isRunning) {
            addDetectView()
            startAuto()
        } else {
            removeDetectView()
        }
        return isRunning
    }


    fun showSettings(btn: View) {

        val popup = PopupMenu(context, btn)
        val menu: Menu = popup.menu

        val configDB = getConfig()
        configDB.arrDetectFishing.forEach {
            menu.add(0, it.id, 0, it.title)
        }
        menu.add(1, 1, 1, "Cần 1")
        menu.add(2, 2, 2, "Cần 2")
        menu.add(3, 3, 3, "Cần 3")
        menu.add(4, 4, 4, "Cần 4")
        menu.add(5, 5, 5, "Cần 5")
        menu.add(6, 6, 6, "Cần 6")

        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == 1
                    || item.itemId == 2
                    || item.itemId == 3
                    || item.itemId == 4
                    || item.itemId == 5
                    || item.itemId == 6) {
                saveDb("can_id", item.itemId)
            }
            if (item.itemId > 10) {
                saveDb("detect_id", item.itemId)
            }
            addDetectView()
            true
        }
        popup.show()
    }


    fun onConfigurationChanged(isLand: Boolean) {
        if (isRunning) {
            addDetectView()
        }
    }

    private fun addDetectView() {
        removeDetectView()

        try {
            val canId = getDb("can_id").toInt()
            val detectId = getDb("detect_id").toInt()
            MLog.e("detectId: $detectId")
            detect = getConfig().arrDetectFishing.find { it.id == detectId }!!

            when (canId) {
                1 -> {
                    xCan = detect.xCan1
                    yCan = detect.yCan1
                }
                2 -> {
                    xCan = detect.xCan2
                    yCan = detect.yCan2
                }
                3 -> {
                    xCan = detect.xCan3
                    yCan = detect.yCan3
                }
                4 -> {
                    xCan = detect.xCan4
                    yCan = detect.yCan4
                }
                5 -> {
                    xCan = detect.xCan5
                    yCan = detect.yCan5
                }
                6 -> {
                    xCan = detect.xCan6
                    yCan = detect.yCan6
                }
            }

            xKeo = detect.xKeo
            yKeo = detect.yKeo

            xThaMoi = detect.xThaMoi
            yThaMoi = detect.yThaMoi

            xClickKeo = detect.xClickKeo
            yClickKeo = detect.yClickKeo

            xBaoQuan = detect.xBaoQuan
            yBaoQuan = detect.yBaoQuan

            xBaoQuan2 = detect.xBaoQuan2

            xTui = detect.xTui
            yTui = detect.yTui

            xCongCu = detect.xCongCu
            yCongCu = detect.yCongCu

            arrDetectView.addAll(widgetService.addViewDetect(xKeo.toPixelW() + sizeKeoWidth / 2, yKeo.toPixelH() + sizeKeoHeight + 5, 1, 200))

            arrDetectView.addAll(widgetService.addViewDetect(xThaMoi.toPixelW(), yThaMoi.toPixelH(), sizeThaMoi, sizeThaMoi, "thả"))
            arrDetectView.addAll(widgetService.addViewDetect(xClickKeo.toPixelW(), yClickKeo.toPixelH(), sizeClickKeo, sizeClickKeo, "kéo"))
            arrDetectView.addAll(widgetService.addViewDetect(xKeo.toPixelW(), yKeo.toPixelH() + 5, sizeKeoWidth, sizeKeoHeight, "kéo"))
            arrDetectView.addAll(widgetService.addViewDetect(xBaoQuan.toPixelW(), yBaoQuan.toPixelH(), sizeBaoQuan, sizeBaoQuan, "bq1"))
            arrDetectView.addAll(widgetService.addViewDetect(xBaoQuan2.toPixelW(), yBaoQuan.toPixelH(), sizeBaoQuan, sizeBaoQuan, "bq2"))

            arrDetectView.addAll(widgetService.addViewDetect(xTui.toPixelW(), yTui.toPixelH(), sizeTui, sizeTui, "tui"))
            arrDetectView.addAll(widgetService.addViewDetect(xCongCu.toPixelW(), yCongCu.toPixelH(), sizeCongCu, sizeCongCu, "cc"))
            arrDetectView.addAll(widgetService.addViewDetect(xCan.toPixelW(), yCan.toPixelH(), sizeCan, sizeCan, "cần $canId"))

            arrDetectView.addAll(widgetService.addViewDetect(xOkSuaCan.toPixelW(), yOkSuaCan.toPixelH(), sizeOkSuaCan, sizeOkSuaCan, "sửa"))
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

    fun removeDetectView() {
        widgetService.removeDetectView(arrDetectView)
        arrDetectView.clear()
    }

    fun release() {
        isRunning = false
        removeDetectView()
    }

    fun startAuto() {
        if (isRunning) {
            fishing()
            check()
        }
    }

    var timeDetect = 1000
    var isInFishing = false
    var isInThaMoi = false
    var lastTimeSuccess = System.currentTimeMillis()
    var countError = 0

    private fun check() {
        val now = System.currentTimeMillis()
        if (now - lastTimeSuccess > 60 * 1000) {
            lastTimeSuccess = System.currentTimeMillis()
            ping()
            countError++
            if (countError > 3) {
                Thread {
                    PushNotifySpec.execute("Auto Fishing", "Error fishing $countError")
                }.start()
            }
            if (countError > 10) {
                delay(2000) {
                    MethodUtils.gotoSettings(context)
                    delay(2000) {
                        MethodUtils.killApp(context, AutoFeedFacebook.packageName)
                        delay(2000) {
                            Process.killProcess(Process.myPid());
                        }
                    }
                }
            }
        }
    }

    private fun fishing() {
        if (blockFishing) {
            retry(1000)
            return
        }
        ScreenshotManager.getInstance().takeScreenshot { bitmap, timeCreate ->
            timeDetect = detect.timeDetectSlow
            if (bitmap != null) {
                isInFishing = isInFishing(bitmap)
                if (isInFishing) {
                    if (isKeo(bitmap)) {
                        MLog.e(TAG, "kéo")
                        clickKeo()
                        LogAuto.logKeo()
                    } else {
                        if (isBaoQuan(bitmap)) {
                            lastTimeSuccess = System.currentTimeMillis()
                            countError = 0
                            LogAuto.logFishingSuccess()
                            MLog.e(TAG, "bảo quản")
                        } else {
                            timeDetect = detect.timeDetectFast
                            MLog.e(TAG, "đợi cá cắn")
                        }
                    }
                } else {
                    if (!isInThaMoi) {
                        isInThaMoi = true
                        MLog.e(TAG, "thả mồi")
                        thaMoi()
                        gc()
                        delay(4000) {
                            isInThaMoi = false
                            if (isInFishing) {
                                LogAuto.logThaMoi()
                            } else {
                                layCanCau()
                            }
                        }
                    }
                }
                bitmap.recycle()
            }
            retry(timeDetect - timeCreate)
        }
    }


    private fun gc() {
        System.gc()
        Runtime.getRuntime().gc()
    }

    var sizeThaMoi = 10
    var xThaMoi = 80F - 100F
    var yThaMoi = 62F - 100F

    private fun thaMoi() {
        autoClickService?.click(xThaMoi.toPixelW(), yThaMoi.toPixelH())
    }


    var blockFishing = false
    private fun layCanCau() {
        if (blockFishing) {
            return
        }
        MLog.e(TAG, "lấy cần câu")
        blockFishing = true
        moTui()

        delay(2000) {
            moCongCu()
            delay(2000) {
                suaCan()
                delay(8000) {
                    thaMoi()
                    delay(3000) {
                        blockFishing = false
                    }
                }
            }
        }


    }

    var sizeTui = 10
    var colorTui = Color.parseColor("#e44142")
    var xTui = -5F
    var yTui = 53F

    private fun moTui() {
        autoClickService?.click(xTui.toPixelW(), yTui.toPixelH())
    }

    var sizeCongCu = 10
    var xCongCu = 0F
    var yCongCu = 0F
    private fun moCongCu() {
        autoClickService?.click(xCongCu.toPixelW(), yCongCu.toPixelH())
    }

    var sizeCan = 10
    var xCan = 0F
    var yCan = 45.8F

    var sizeOkSuaCan = 10
    var xOkSuaCan = 50F
    var yOkSuaCan = (804F / (1080F / 100F)) - 100F

    private fun suaCan() {
        autoClickService?.click(xCan.toPixelW(), yCan.toPixelH())
        delay(2000) {
            autoClickService?.click(xOkSuaCan.toPixelW(), yOkSuaCan.toPixelH())
            delay(2000) {
                autoClickService?.click(xOkSuaCan.toPixelW(), yOkSuaCan.toPixelH())
                delay(2000) {
                    autoClickService?.click(xOkSuaCan.toPixelW(), yOkSuaCan.toPixelH())
                }
            }
        }
    }


    private fun isInFishing(bitmap: Bitmap): Boolean {
        return !bitmap.havePercentColor(xTui.toPixelW(), yTui.toPixelH(), sizeTui, sizeTui, colorTui, 50)
    }

    var sizeClickKeo = 10
    var xClickKeo = 88F - 100F
    var yClickKeo = 80F - 100F
    var dangKeo = false
    val timeKeo = 1000

    private fun clickKeo() {
        if (dangKeo) {
            return
        }
        dangKeo = true
        autoClickService?.click(xClickKeo.toPixelW(), yClickKeo.toPixelH(), timeKeo)

        delay(timeKeo.toLong()) {
            dangKeo = false
        }
    }


    var sizeBaoQuan = 10
    var colorBaoQuan = Color.parseColor("#41c5f3")
    var xBaoQuan = (1338F / (1920F / 100F)) - 100F
    var xBaoQuan2 = (1542F / (1920F / 100F)) - 100F
    var yBaoQuan = (880F / (1080F / 100F)) - 100F

    private fun isBaoQuan(bitmap: Bitmap): Boolean {
        if (bitmap.havePercentColor(xBaoQuan.toPixelW(), yBaoQuan.toPixelH(), sizeBaoQuan, sizeBaoQuan, colorBaoQuan, 90)) {
            clickBaoQuan()
            return true
        }
        if (bitmap.havePercentColor(xBaoQuan2.toPixelW(), yBaoQuan.toPixelH(), sizeBaoQuan, sizeBaoQuan, colorBaoQuan, 90)) {
            clickBaoQuan2()
            return true
        }
        return false
    }


    private fun clickBaoQuan() {
        autoClickService?.click(xBaoQuan.toPixelW(), yBaoQuan.toPixelH())
    }

    private fun clickBaoQuan2() {
        autoClickService?.click(xBaoQuan2.toPixelW(), yBaoQuan.toPixelH())
    }

    var sizeKeoWidth = 50
    var sizeKeoHeight = 1
    var xKeo = 49F
    var yKeo = 13F
    var lastTimeKeo = 0L

    private fun isKeo(bitmap: Bitmap): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastTimeKeo > 10000 && !bitmap.isSameOneColor(xKeo.toPixelW(), yKeo.toPixelH(), sizeKeoWidth, sizeKeoHeight)) {
            lastTimeKeo = now
            return true
        }
        return false
    }

    private fun retry(time: Long) {
        delay(if (time <= 100) 100 else time) {
            startAuto()
        }
    }

    private fun log(bm: Bitmap) {
        widgetService.imvPreviewCut.visibility = View.VISIBLE
        widgetService.imvPreviewCut.setImageBitmap(bm)
    }
}