package net.iblankdigital.auto.service.auto

import net.iblankdigital.auto.ext.delay
import net.iblankdigital.auto.service.capture.ScreenshotManager
import net.iblankdigital.auto.service.widgetService
import net.iblankdigital.auto.utils.MLog

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





