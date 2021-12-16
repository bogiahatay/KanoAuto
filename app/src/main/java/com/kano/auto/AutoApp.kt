package com.kano.auto

import android.app.Application
import android.os.Handler
import com.kano.auto.db.api.FishingConfig
import com.kano.auto.db.api.IOnApiRequest
import com.kano.auto.ml.MLKit
import com.kano.auto.ml.MLKitText
import com.kano.auto.service.auto.saveDb

class AutoApp : Application() {


    companion object {
        lateinit var instance: AutoApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initConfig()
        MLKit.init()
        MLKitText.init()
    }

    private fun initConfig() {
        FishingConfig.get(object : IOnApiRequest {
            override fun onApiSuccess(data: String) {
                saveDb("CONFIG", data)
            }

            override fun onApiError(why: String) {

            }
        })
        Handler().postDelayed({
            initConfig()
        }, 180 * 1000)
    }

}