package net.iblankdigital.auto

import android.app.Application
import android.os.Handler
import net.iblankdigital.auto.db.api.FishingConfig
import net.iblankdigital.auto.db.api.IOnApiRequest
import net.iblankdigital.auto.ml.MLKit
import net.iblankdigital.auto.ml.MLKitText
import net.iblankdigital.auto.service.auto.saveDb

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