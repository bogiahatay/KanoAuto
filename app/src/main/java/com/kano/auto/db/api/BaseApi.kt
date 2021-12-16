package com.kano.auto.db.api

import com.kano.auto.utils.MLog
import okhttp3.*
import java.io.IOException

object BaseApi {
    val TAG = "Api"
    fun request(api: String, param: Map<String, String>, iOnApiRequest: IOnApiRequest) {
        val client = OkHttpClient().newBuilder()
                .build()
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("page", "1")
                .build()
        val request = Request.Builder()
                .url(api)
                .method("POST", body)
                .build()
        MLog.e(TAG, "===> $api")
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val code: Int = response.code
                val time: Long = response.receivedResponseAtMillis - response.sentRequestAtMillis
                MLog.e(TAG, "<=== " + code + " " + time + "ms " + api)

                if (response.isSuccessful && response.body != null) {
                    iOnApiRequest.onApiSuccess(response.body!!.string())
                    return
                }
                iOnApiRequest.onApiError("error data")
            }

            override fun onFailure(call: Call, e: IOException) {
                iOnApiRequest.onApiError(e.message ?: "error api")
            }
        })

    }
}