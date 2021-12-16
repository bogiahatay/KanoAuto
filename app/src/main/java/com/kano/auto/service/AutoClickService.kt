package com.kano.auto.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.view.ViewConfiguration
import android.view.accessibility.AccessibilityEvent
import com.kano.auto.MainActivity
import com.kano.auto.bean.Event
import com.kano.auto.ext.logd
import com.kano.auto.ext.shortToast
import com.kano.auto.ext.shortToastLong
import com.kano.auto.utils.MLog


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
        path.moveTo(x.toFloat() - 3, y.toFloat() - 3)
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

        path.moveTo(widthDevice.toFloat() / 2, heightDevice.toFloat() / 4 * 3)
        path.lineTo(widthDevice.toFloat() / 2, heightDevice.toFloat() / 4 * 1)

        val gestureDescription = GestureDescription.Builder()
                .addStroke(StrokeDescription(path, 0, 1000.toLong()))
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