package net.iblankdigital.auto.base

import kotlinx.coroutines.*
import java.lang.Runnable


open class ThreadTask(val runnable: Runnable) {

    var job: Job? = null

    fun cancel() {
        job?.cancel()
    }

    fun start() {
        job = GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                runnable.run()
            }
        }
    }
}