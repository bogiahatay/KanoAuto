package net.iblankdigital.auto.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.view.ViewConfiguration
import android.view.accessibility.AccessibilityEvent
import net.iblankdigital.auto.MainActivity
import net.iblankdigital.auto.bean.Event
import net.iblankdigital.auto.ext.logd
import net.iblankdigital.auto.ext.shortToastLong
import net.iblankdigital.auto.utils.MLog
import java.util.*


/**
 * Created on 2018/9/28.
 * By nesto
 */

var autoClickService: AutoClickService? = null

class AutoClickService : AccessibilityService() {

    val TAG = "AutoClickService"
    internal val events = mutableListOf<Event>()

    override fun onInterrupt() {
        // NO-OP
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // NO-OP
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        autoClickService = this
        shortToastLong("ServiceConnected")
        MainActivity.start()
    }

    fun click(x: Int, y: Int) {
        val tapTimeout = ViewConfiguration.getTapTimeout()
        click(x, y, tapTimeout)
    }

    @SuppressLint("NewApi")
    fun click(x: Int, y: Int, time: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return
        }

        MLog.e(TAG, "click : $x $y")

        val path = Path()
        path.moveTo(x.toFloat() - 2, y.toFloat() - 2)
        val gestureDescription = GestureDescription.Builder()
                .addStroke(StrokeDescription(path, 0, time.toLong()))
                .build()

        dispatchGesture(gestureDescription, null, null)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        "AutoClickService onUnbind".logd()
        autoClickService = null
        return super.onUnbind(intent)
    }


    override fun onDestroy() {
        "AutoClickService onDestroy".logd()
        autoClickService = null
        super.onDestroy()
    }

    fun scroll() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return
        }

        MLog.e(TAG, "scroll to bottom")

        val path = Path()
        val ranX1 = kotlin.random.Random.nextDouble(0.2, 0.8).toFloat()
        val ranX2 = kotlin.random.Random.nextDouble(0.2, 0.8).toFloat()

        val ranY1 = kotlin.random.Random.nextDouble(0.7, 0.9).toFloat()
        val ranY2 = kotlin.random.Random.nextDouble(0.1, 0.3).toFloat()

        path.moveTo(widthDevice.toFloat() * ranX1, heightDevice.toFloat() * ranY1)
        path.lineTo(widthDevice.toFloat() * ranX2, heightDevice.toFloat() * ranY2)

        val time = kotlin.random.Random.nextInt(800, 1500);


        val gestureDescription = GestureDescription.Builder()
                .addStroke(StrokeDescription(path, 0, time.toLong()))
                .build()

        dispatchGesture(gestureDescription, null, null)
    }

    fun back() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return
        }

        MLog.e(TAG, "back")

        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun hideKeyboard() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return
        }

        MLog.e(TAG, "hide Keyboard")

        performGlobalAction(SHOW_MODE_HIDDEN)

    }
}