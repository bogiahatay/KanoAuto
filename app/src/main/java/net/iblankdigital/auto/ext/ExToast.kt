package net.iblankdigital.auto.ext

import android.os.Looper
import android.widget.Toast
import net.iblankdigital.auto.service.auto.context

/**
 * Created on 2017/1/6.
 * By nesto
 */
var toast: Toast? = null

fun showToast(text: String, length: Int = Toast.LENGTH_SHORT) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        toast = toast ?: Toast.makeText(context, text, length)
        toast?.let {
            it.setText(text)
            it.duration = length
            it.show()
        }
    } else {
        "show toast run in wrong thread".loge()
    }
}

fun shortToast(text: String) {
    showToast(text)
}

fun shortToastLong(text: String) {
    showToast(text, Toast.LENGTH_LONG)
}
