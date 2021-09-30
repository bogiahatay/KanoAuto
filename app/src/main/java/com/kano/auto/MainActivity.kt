package com.kano.auto

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity
import com.kano.auto.checker.Utils
import com.kano.auto.service.WidgetService
import com.kano.auto.service.auto.ping
import com.kano.auto.service.capture.ScreenshotManager
import com.kano.auto.utils.MLog
import com.kano.auto.ext.shortToast
import com.kano.auto.service.auto.context
import kotlinx.android.synthetic.main.activity_main.*


private const val PERMISSION_CODE = 110

open class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    val requestCapture = 6789
    var serviceIntent: Intent? = null
    lateinit var context: MainActivity


    companion object {
        var isRunning = false

        fun start() {
            if (isRunning) {
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this
        isRunning = true
        ping()
        button.setOnClickListener {

            if (checkPermission()) {
                serviceIntent = Intent(this@MainActivity, WidgetService::class.java)
                startService(serviceIntent)

                ScreenshotManager.getInstance().requestScreenshotPermission(this, requestCapture)

                Thread {
                    for (i in 0..10000000) {
                        SystemClock.sleep(5000)
                    }
                }.start()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        MLog.e(TAG, "onNewIntent")
    }

    private fun checkPermission(): Boolean {
        if (!checkPermissionAccess()) {
            shortToast("We need Accessibility Permission")
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Settings.canDrawOverlays(this)) {

        } else {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, PERMISSION_CODE)
            shortToast("We need System Alert Window Permission")
            return false
        }

//        if (!Utils.checkPermission(context, false)) {
//            shortToast("Vui lòng bật quyền truy cập dữ liệu sử dụng")
//            Utils.checkPermission(context, true)
//            return false
//        }
        return true
    }

    private fun checkPermissionAccess(): Boolean {
        val string = getString(R.string.accessibility_service_id)
        val manager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val list = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        for (id in list) {
            if (string == id.id) {
                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
//        moveTaskToBack(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCapture) {
            ScreenshotManager.getInstance().onActivityResult(resultCode, data)
        }
    }
}
