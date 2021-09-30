package com.kano.auto.ext

import android.os.Handler
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

fun Any.delay(time: Long, runnable: Runnable) {
    Handler().postDelayed(runnable, time)
}

