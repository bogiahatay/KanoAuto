package net.iblankdigital.auto.ext

import android.os.Handler

fun Any.delay(time: Long, runnable: Runnable) {
    Handler().postDelayed(runnable, time)
}

