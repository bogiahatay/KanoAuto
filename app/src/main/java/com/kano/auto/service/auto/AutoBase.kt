package com.kano.auto.service.auto

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.view.View
import android.view.WindowManager
import com.kano.auto.AutoApp
import com.kano.auto.R
import com.kano.auto.db.api.FishingConfig
import com.kano.auto.db.MySharedPreferences
import com.kano.auto.ext.toObject
import com.kano.auto.service.heightDevice
import com.kano.auto.service.widthDevice


val context: Context get() = AutoApp.instance

fun Float.toPixel(size: Int): Int {
    return (size.toFloat() / 100F * this).toInt()
}

fun Float.toPixelW(): Int {
    if (this < 0) {
        return toReversePixelW()
    }
    return (widthDevice.toFloat() / 100F * this).toInt()
}

fun Float.toPixelH(): Int {
    if (this < 0) {
        return toReversePixelH()
    }
    return (heightDevice.toFloat() / 100F * this).toInt()
}

fun Float.toReversePixelW(): Int {
    return (widthDevice.toFloat() / 100F * (100 + this)).toInt()
}

fun Float.toReversePixelH(): Int {
    return (heightDevice.toFloat() / 100F * (100 + this)).toInt()
}


fun ping() {
    val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    val volume = (maxVolume * 0.5F).toInt()
    audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)

    val mp: MediaPlayer = MediaPlayer.create(context, R.raw.error)
    mp.start()
}


fun saveDb(key: String, value: Any) {
    if (value is Int || value is Long || value is String || value is Float || value is Boolean) {
        MySharedPreferences.putStringValue(context, key, value.toString())
    }
}

fun getDb(key: String): String {
    return MySharedPreferences.getStringValue(context, key, "")
}

fun getConfig(): FishingConfig {
    val json = getDb("CONFIG")
    return json.toObject(FishingConfig::class.java)
}



abstract class AutoBase {
    var arrDetectView: ArrayList<View> = arrayListOf()
    var isRunning = false

}