package com.kano.auto.service.auto

import com.kano.auto.ext.cut
import com.kano.auto.ext.delay
import com.kano.auto.service.capture.ScreenshotManager
import com.kano.auto.service.widgetService
import com.kano.auto.ext.visibility
import com.kano.auto.ml.MLKit
import com.kano.auto.ml.MLKitText
import com.kano.auto.service.heightDevice
import com.kano.auto.utils.MLog

object AutoScreenShot : AutoBase() {



    fun toggle(): Boolean {
        isRunning = !isRunning
        startAuto()
        if (isRunning) {
            addDetectView()
        } else {
            removeDetectView()
        }

        return isRunning
    }


    fun addDetectView() {
        removeDetectView()
    }

    fun removeDetectView() {
        widgetService.removeDetectView(arrDetectView)
        arrDetectView.clear()
    }

    fun startAuto() {
        if (isRunning) {
            MLog.active = true;
            ScreenshotManager.getInstance().takeScreenshot { bitmap, timeCreate ->
                if (bitmap != null) {

//                    widgetService.imvPreview.visibility()
//                    widgetService.imvPreview.setImageBitmap(bitmap)
//                    MLKit.detectBitmap(bitmap)
//                    MLKitText.detectBitmap(bitmap)

                }
                delay(1000) {
                    startAuto()
                }
            }
        }
    }

    fun release() {
        isRunning = false
    }
}





