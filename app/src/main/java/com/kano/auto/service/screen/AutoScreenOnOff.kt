package com.kano.auto.service.screen

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.PowerManager
import android.os.PowerManager.PARTIAL_WAKE_LOCK
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.kano.auto.ext.showToast
import com.kano.auto.service.auto.context


object AutoScreenOnOff {


    var pm: PowerManager? = null
    var mWakeLock: PowerManager.WakeLock? = null
    var isActiveOffScreen = false

    @SuppressLint("InvalidWakeLockTag")
    fun toggle() {

        isActiveOffScreen = !isActiveOffScreen;
        if (isActiveOffScreen) {
            active()
        } else {
            disable()
            showToast("Disable Auto Off Screen")
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    private fun active() {
        try {
            pm = context.getSystemService(Service.POWER_SERVICE) as PowerManager
            mWakeLock = pm?.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "tag")
            mWakeLock?.acquire(12 * 60 * 60 * 1000L)
            showToast("Active Auto Off Screen")
        } catch (e: Exception) {
            showToast("Error Auto Off Screen")
            e.printStackTrace()
        }

    }

    private fun disable() {
        if (mWakeLock != null && mWakeLock!!.isHeld) {
            mWakeLock?.release()
        }
    }

    fun release() {
        isActiveOffScreen = false
        disable()
    }

    fun setBrightness(value: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context)) {
                var brightness = value
                if (brightness < 0) {
                    brightness = 0
                }
                if (brightness > 255) {
                    brightness = 255;
                }
                val cResolver = context.contentResolver
                Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);

            } else {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.data = Uri.parse("package:" + context.packageName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }
}