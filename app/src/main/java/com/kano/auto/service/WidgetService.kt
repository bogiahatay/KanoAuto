package com.kano.auto.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Process
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.kano.auto.MainActivity
import com.kano.auto.R
import com.kano.auto.ext.dp2px
import com.kano.auto.ext.gone
import com.kano.auto.ext.visibility
import com.kano.auto.service.auto.*
import com.kano.auto.service.capture.ScreenshotManager
import com.kano.auto.service.log.LogAuto
import com.kano.auto.service.screen.AutoScreenOnOff
import com.kano.auto.utils.MLog
import com.kano.auto.utils.MethodUtils
import com.kano.auto.utils.TouchAndDragListener


/**
 * Created on 2018/9/28.
 * By nesto
 */

var statusBarHeight = 0
var navigationBarHeight = 0
var widthDevice = 0
var heightDevice = 0
var orientation = -1
val smallDevice
    get() = Math.min(widthDevice, heightDevice)

lateinit var widgetService: WidgetService

class WidgetService : Service() {
    val TAG = "WidgetService"

    lateinit var manager: WindowManager
    lateinit var view: RelativeLayout
    lateinit var btnMove: TextView
    lateinit var btnStart: ImageView
    lateinit var btnSetting: ImageView
    lateinit var btnTypeAuto: ImageView
    lateinit var btnMore: ImageView
    lateinit var btnClose: ImageView
    lateinit var tvTimeEnd: TextView
    lateinit var vAction: View
    lateinit var tvLog: TextView
    lateinit var vFulLog: View
    lateinit var tvLogFull: TextView
    lateinit var btnLogFullClose: View

    lateinit var tvMessage: TextView
    lateinit var imvPreview: ImageView
    lateinit var imvPreviewCut: ImageView
    lateinit var imvPreviewCut2: ImageView

    lateinit var params: WindowManager.LayoutParams
    var startDragDistance: Int = 0

    var overlayParam = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
        WindowManager.LayoutParams.TYPE_PHONE
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        MLog.e(TAG, "onCreate")
        widgetService = this
        initScreenSize()

        startDragDistance = dp2px(10f)
        view = LayoutInflater.from(this).inflate(R.layout.widget, null) as RelativeLayout
        btnMove = view.findViewById(R.id.btnMove)
        btnStart = view.findViewById(R.id.btnStart)
        btnSetting = view.findViewById(R.id.btnSetting)
        btnTypeAuto = view.findViewById(R.id.btnTypeAuto)
        btnMore = view.findViewById(R.id.btnMore)
        btnClose = view.findViewById(R.id.btnClose)
        tvMessage = view.findViewById(R.id.tvMessage)
        vAction = view.findViewById(R.id.vAction)
        tvTimeEnd = view.findViewById(R.id.tvTimeEnd)
        tvLog = view.findViewById(R.id.tvLog)
        vFulLog = view.findViewById(R.id.vFulLog)
        tvLogFull = view.findViewById(R.id.tvFulLog)
        btnLogFullClose = view.findViewById(R.id.btnLogFullClose)
        btnLogFullClose.setOnClickListener {
            toggleLogFull()
        }

        imvPreview = view.findViewById(R.id.imvPreview)
        imvPreviewCut = view.findViewById(R.id.imvPreviewCut)
        imvPreviewCut2 = view.findViewById(R.id.imvPreviewCut2)

        params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayParam,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
        params.x = (smallDevice * 0.2F).toInt()
        params.y = (smallDevice * 0.2F).toInt()
        params.gravity = Gravity.TOP or Gravity.LEFT

        //getting windows services and adding the floating view to it
        manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        manager.addView(view, params)

        //adding an touchlistener to make drag movement of the floating widget
        btnMove.setOnTouchListener(TouchAndDragListener(params, startDragDistance,
                { toggleViewAction() },
                { manager.updateViewLayout(view, params) }))

        btnTypeAuto.setOnClickListener {
            showAutoType()
        }
        btnStart.setOnClickListener {
            startAuto()
        }

        btnSetting.setOnClickListener {
            showSetting()
        }
        btnMore.setOnClickListener {
            showMore()
        }
        btnClose.setOnClickListener {
            stopWidgetService()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel("kano", "kano", NotificationManager.IMPORTANCE_NONE)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
        }


        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification: Notification = NotificationCompat.Builder(this, "kano")
                .setContentTitle("KanoAuto")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent)
                .build()

        startForeground(1337, notification)

        LogAuto.init(this, tvLog)
    }

    fun showSetting() {

        if (TYPE_AUTO == 1) {
            AutoFeedFacebook.showSettings(btnSetting)
        }
        //AutoFishingPlayTogether
        if (TYPE_AUTO == 2) {
            AutoFishingPlayTogether.showSettings(btnSetting)
        }
    }

    fun showMore() {
        val popup = PopupMenu(context, btnMore)
        val menu: Menu = popup.menu

        val t1 = if (AutoScreenOnOff.isActiveOffScreen) {
            "Disable Auto Screen On Off"
        } else {
            "Active Auto Screen On Off"
        }
        menu.add(1, 11, 1, t1)
        menu.add(1, 22, 1, "Hẹn 1 Giờ Tắt")
        menu.add(1, 23, 1, "Hẹn 2 Giờ Tắt")
        menu.add(1, 24, 1, "Hẹn 3 Giờ Tắt")
        menu.add(1, 25, 1, "Hẹn 5 Giờ Tắt")
        menu.add(1, 26, 1, "Tắt Hẹn Giờ")
        menu.add(1, 36, 1, "Show Log")
        menu.add(1, 37, 1, "Disable Log")
        menu.add(1, 38, 1, "Giảm độ sáng")

        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == 11) {
                AutoScreenOnOff.toggle()
            }
            if (item.itemId == 22) {
                ScheduleAuto.timeEnd(1)
            }
            if (item.itemId == 23) {
                ScheduleAuto.timeEnd(2)
            }
            if (item.itemId == 24) {
                ScheduleAuto.timeEnd(3)
            }
            if (item.itemId == 25) {
                ScheduleAuto.timeEnd(3)
            }
            if (item.itemId == 26) {
                ScheduleAuto.timeEnd(0)
            }
            if (item.itemId == 36) {
                toggleLogFull()
            }
            if (item.itemId == 37) {
                LogAuto.isRunning = false
            }
            if (item.itemId == 38) {
                AutoScreenOnOff.setBrightness(0)
            }
            true
        }
        popup.show()
    }

    private fun toggleLogFull() {
        if (vFulLog.visibility == View.VISIBLE) {
            vFulLog.gone()
            tvLogFull.text = ""
        } else {
            vFulLog.visibility()
            tvLogFull.text = LogAuto.getLogFull()
        }
    }

    fun toggleViewAction() {
        if (vAction.visibility == View.GONE) {
            vAction.visibility = View.VISIBLE
            view.alpha = 1F
            MLog.active = true
        } else {
            vAction.visibility = View.GONE
            view.alpha = 0.5F
            MLog.active = false
        }
        imvPreview.gone()
        imvPreviewCut.gone()
        imvPreviewCut2.gone()
    }

    var TYPE_AUTO = 0

    fun showAutoType() {
        val popup = PopupMenu(context, btnTypeAuto)
        val menu: Menu = popup.menu
        menu.add(1, 1, 1, "Feed Facebook")
        menu.add(2, 2, 2, "Fishing PlayTogether")
        menu.add(3, 3, 3, "ScreenShot")

        popup.setOnMenuItemClickListener { item ->
            TYPE_AUTO = item.itemId
            true
        }
        popup.show()
    }

    fun startAuto() {
        //AutoLoginPlayTogether
        if (TYPE_AUTO == 1) {
            val status = AutoFeedFacebook.toggle()
            btnStart.setImageResource(if (status) R.drawable.ic_stop else R.drawable.ic_play)
            tvMessage.text = "Facebook"
        }
        //AutoFishingPlayTogether
        if (TYPE_AUTO == 2) {
            val status = AutoFishingPlayTogether.toggle()
            btnStart.setImageResource(if (status) R.drawable.ic_stop else R.drawable.ic_play)
            tvMessage.text = "Fishing"
        }
        //ScreenShot
        if (TYPE_AUTO == 3) {
            val status = AutoScreenShot.toggle()
            btnStart.setImageResource(if (status) R.drawable.ic_stop else R.drawable.ic_play)
            tvMessage.text = "ScreenShot"
        }
    }

    fun stopWidgetService() {
        stopSelf()
    }

    fun release() {
        AutoFeedFacebook.release()
        AutoFishingPlayTogether.release()
        AutoScreenShot.release()
        AutoScreenOnOff.release()
        LogAuto.release()

        ScheduleAuto.release()
        ScreenshotManager.getInstance().release()

        if (view.parent != null) {
            manager.removeView(view)
        }
        Handler().postDelayed({
            Process.killProcess(Process.myPid());
        }, 2000)
    }

    override fun onDestroy() {
        release()
        super.onDestroy()
        MLog.e(TAG, "onDestroy")
    }


    fun removeDetectView(view: View?) {
        if (view?.parent != null) {
            manager.removeView(view)
        }
    }

    fun removeDetectView(arrView: ArrayList<View>) {
        arrView.forEach { view ->
            manager.removeView(view)
        }
    }

    fun addViewDetect(xDetect: Int, yDetect: Int, width: Int, height: Int, tag: String = ""): ArrayList<View> {
        val view = View(context)
        view.setBackgroundResource(R.drawable.bg_fishing)
        view.isEnabled = false

        val params = WindowManager.LayoutParams(
                width,
                height,
                overlayParam,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)

        params.gravity = Gravity.TOP or Gravity.LEFT

        params.x = xDetect - 2
        params.y = yDetect - 2
        params.width = width + 4
        params.height = height + 4

        manager.addView(view, params)

        if (tag.isNotEmpty()) {
            val tagView = TextView(context)
            tagView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 6F);
            tagView.setTextColor(Color.RED)
            tagView.setLines(1)
            tagView.setBackgroundColor(Color.WHITE)
            tagView.text = tag

            val paramsTag = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    overlayParam,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT)


            paramsTag.gravity = Gravity.TOP or Gravity.LEFT

            paramsTag.x = xDetect - 2
            paramsTag.y = yDetect + height + 4

            manager.addView(tagView, paramsTag)
            return arrayListOf(view, tagView)
        }


        return arrayListOf(view)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        initScreenSize()
        MLog.e(TAG, "onConfigurationChanged : " + newConfig.orientation)

        AutoFeedFacebook.onConfigurationChanged(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        AutoFishingPlayTogether.onConfigurationChanged(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
    }


    fun initScreenSize() {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val size = Point()
        wm.defaultDisplay.getRealSize(size)

        statusBarHeight = MethodUtils.getStatusBarHeight(context)
        navigationBarHeight = MethodUtils.getNavigationBarHeight(context)

        widthDevice = size.x
        heightDevice = size.y

        orientation = wm.defaultDisplay.orientation
        MLog.e(TAG, when (orientation) {
            Surface.ROTATION_0 -> "android portrait screen"
            Surface.ROTATION_90 -> "android landscape screen"
            Surface.ROTATION_180 -> "android reverse portrait screen"
            else -> "android reverse landscape screen"
        })
    }


}