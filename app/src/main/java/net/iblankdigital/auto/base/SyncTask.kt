package net.iblankdigital.auto.base

import kotlinx.coroutines.*

@DelicateCoroutinesApi
abstract class SyncTask<I, P, O> {
    var job: Job? = null
    var result: O? = null

    //private var result: O
    open fun onPreExecute() {}

    open fun onPostExecute(result: O?) {}

    abstract fun doInBackground(vararg params: I): O

    fun execute(vararg input: I) {
        job = GlobalScope.launch(Dispatchers.Main) {
            onPreExecute()
            withContext(Dispatchers.IO) {
                result = doInBackground(*input)
            }
            onPostExecute(result)
        }
        return
    }

    open fun publishProgress(runnable: Runnable) {
        GlobalScope.launch(Dispatchers.Main) {
            runnable.run()
        }
    }

    fun cancel() {
        job?.cancel()
    }
}