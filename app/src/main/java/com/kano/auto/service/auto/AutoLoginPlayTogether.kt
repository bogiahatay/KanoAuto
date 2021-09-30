package com.kano.auto.service.auto

import android.graphics.Color
import android.os.Handler
import android.view.View
import com.kano.auto.ext.compareColor
import com.kano.auto.ext.cut
import com.kano.auto.service.capture.ScreenshotManager
import com.kano.auto.utils.MLog
import com.kano.auto.utils.MethodUtils
import com.kano.auto.service.widthDevice
import com.kano.auto.service.heightDevice
import com.kano.auto.service.statusBarHeight
import com.kano.auto.service.widgetService

object AutoLoginPlayTogether : AutoBase() {
    val TAG = "AutoFishing"

    var timeDetect: Long = 6000
    val packageName = "com.haegin.playtogether";

    fun toggle(): Boolean {
        isRunning = !isRunning
        lastTimeRestart = System.currentTimeMillis()
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
        arrDetectView.addAll(widgetService.addViewDetect(x1, y1.toPixel(heightDevice), size1Width, size1Height))
        arrDetectView.addAll(widgetService.addViewDetect(x2, y2.toPixel(heightDevice), size2Width, size2Height))
    }

    fun removeDetectView() {
        widgetService.removeDetectView(arrDetectView)
        arrDetectView.clear()
    }


    fun startAuto() {
        if (isRunning) {
            if (MethodUtils.isAppRunning(context, packageName)) {
                checkFullSlot()
            } else {
                reStartApp()
                retry()
            }
        }
    }

    private fun reStartApp() {
        try {
            lastTimeRestart = System.currentTimeMillis()
            MLog.e("ReStart PlayTogether")
            MethodUtils.killApp(context, packageName)
            Handler().postDelayed({
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

    var lastTimeRestart = 0L
    private fun checkFullSlot() {
        val diff = System.currentTimeMillis() - lastTimeRestart
        if (diff > 1 * 60 * 1000) {
            ping()
        }

        ScreenshotManager.getInstance().takeScreenshot { bitmap, timeCreate ->
            if (bitmap != null) {

                val cut1 = bitmap.cut(x1, y1.toPixel(heightDevice), size1Width, size1Height)
                val cut2 = bitmap.cut(x2, y2.toPixel(heightDevice), size2Width, size2Height)

                val color = Color.parseColor("#fee300")

                widgetService.imvPreview.visibility = View.GONE
                widgetService.imvPreviewCut.visibility = View.VISIBLE
                widgetService.imvPreviewCut2.visibility = View.VISIBLE
                widgetService.imvPreviewCut.setImageBitmap(cut1)
                widgetService.imvPreviewCut2.setImageBitmap(cut2)

                if (cut1.compareColor(color) || cut2.compareColor(color)) {
                    reStartApp()
                    MLog.e("CompareBitmap FullSlot : true")
                } else {
                    MLog.e("CompareBitmap FullSlot : false")
                }
            } else {
                MLog.e("ScreenshotManager ERROR")
            }
            retry()
        }
    }

    private fun retry() {
        Handler().postDelayed({
            startAuto()
        }, timeDetect)
    }

    fun release() {
        isRunning = false
        removeDetectView()
    }
}





