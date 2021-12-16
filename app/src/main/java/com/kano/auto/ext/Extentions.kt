package com.kano.auto.ext

import android.content.Context
import android.util.Log
import android.view.View
import com.kano.auto.BuildConfig
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap


/**
 * Created on 2018/9/28.
 * By nesto
 */
private const val TAG = "AutoClickService"

fun Any.logd(tag: String = TAG) {
    if (!BuildConfig.DEBUG) return
    if (this is String) {
        Log.d(tag, this)
    } else {
        Log.d(tag, this.toString())
    }
}


fun Any.loge(tag: String = TAG) {
    if (!BuildConfig.DEBUG) return
    if (this is String) {
        Log.e(tag, this)
    } else {
        Log.e(tag, this.toString())
    }
}

fun Context.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

fun View.visibility() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Map<String, String>.getOptString(key: String, valueDefault: String): String {
    return if (containsKey(key)) {
        get(key)!!
    } else {
        valueDefault
    }
}

fun Map<String, String>.getOptLong(key: String, valueDefault: Long): Long {
    return if (containsKey(key)) {
        get(key)!!.toLong()
    } else {
        valueDefault
    }
}
typealias Action = () -> Unit
