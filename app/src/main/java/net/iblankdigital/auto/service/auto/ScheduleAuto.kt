package net.iblankdigital.auto.service.auto

import android.os.CountDownTimer
import android.os.Process
import net.iblankdigital.auto.ext.delay
import net.iblankdigital.auto.ext.gone
import net.iblankdigital.auto.service.widgetService
import net.iblankdigital.auto.utils.MethodUtils
import net.iblankdigital.auto.ext.visibility

object ScheduleAuto {

    var delayTimeEnd: CountDownTimer? = null
    var isActive = false

    fun timeEnd(time: Int) {
        if (time == 0) {
            stop()
            return
        }
        isActive = true
        val allTime = (time * 60 * 60 * 1000).toLong()
        delayTimeEnd?.cancel()
        widgetService.tvTimeEnd.visibility()
        delayTimeEnd = object : CountDownTimer(allTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (isActive) {
                    val seconds = millisUntilFinished % (60 * 1000) / 1000
                    val minus = millisUntilFinished / (60 * 1000)
                    widgetService.tvTimeEnd.text = "$minus:$seconds"
                }
            }

            override fun onFinish() {
                if (isActive) {

                    delay(2000) {
                        MethodUtils.gotoSettings(context)
                        delay(2000) {
                            widgetService.stopWidgetService()
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
        }.start()
    }

    private fun stop() {
        isActive = false
        widgetService.tvTimeEnd.text = ""
        widgetService.tvTimeEnd.gone()
        delayTimeEnd?.cancel()
    }

    fun release() {
        stop()
    }

}