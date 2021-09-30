package com.kano.auto.service.log

import android.os.Handler
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kano.auto.ext.getOptLong
import com.kano.auto.ext.getOptString
import com.kano.auto.ext.toJson
import com.kano.auto.service.WidgetService
import com.kano.auto.service.auto.AutoFishingPlayTogether
import com.kano.auto.service.auto.getDb
import com.kano.auto.service.auto.saveDb
import com.kano.auto.utils.DateTimeUtils
import java.util.*
import kotlin.random.Random


object LogAuto {

    lateinit var tvLog: TextView
    lateinit var widgetService: WidgetService

    var isRunning = false
    val handler = Handler()
    val function = {
        if (isRunning) {
            logTime()
            printLog()
            saveToDb()
            startLog()
        }
    }

    val keyStartAuto = "bắt đầu"
    val keyTimeAuto = "đã auto"
    val keyThaMoi = "thả mồi"
    val keyKeo = "kéo"
    val keyThanhCong = "thành công"

    var mapLog: LinkedHashMap<String, String> = linkedMapOf()


    fun init(widgetService: WidgetService, tvLog: TextView) {
        isRunning = true
        this.widgetService = widgetService
        this.tvLog = tvLog
        log(keyStartAuto, DateTimeUtils.formatLong(System.currentTimeMillis(), "HH:mm dd/MM/yyyy"))
        startLog()
    }


    private fun startLog() {
        handler.removeCallbacks(function)
        handler.postDelayed(function, 1000)
    }


    fun release() {
        isRunning = false
    }

    var countSaveDB = 0
    private fun saveToDb() {
        countSaveDB++
        if (countSaveDB % 5 == 0) {
            countSaveDB = 0
            val key = "LOG_AUTO"
            val json = getDb(key)
            var mapLogDb: LinkedHashMap<String, LinkedHashMap<String, String>> = linkedMapOf()
            if (json.isNotEmpty()) {
                val listType = object : TypeToken<LinkedHashMap<String, LinkedHashMap<String, String>>>() {}.type
                mapLogDb = Gson().fromJson(json, listType)
            }
            val keyDB = mapLog.getOptString(keyStartAuto, "")
            mapLogDb[keyDB] = mapLog
            saveDb(key, mapLogDb.toJson())
        }
    }

    fun getLogFull(): String {
        val key = "LOG_AUTO"
        val json = getDb(key)
        var mapLogDb: LinkedHashMap<String, LinkedHashMap<String, String>> = linkedMapOf()
        if (json.isNotEmpty()) {
            val listType = object : TypeToken<LinkedHashMap<String, LinkedHashMap<String, String>>>() {}.type
            mapLogDb = Gson().fromJson(json, listType)
        }
        var text = ""

        mapLogDb.forEach { it1 ->
            it1.value.forEach { it2 ->
                var value = it2.value
                if (it2.key == keyTimeAuto) {
                    value = (DateTimeUtils.getTimeMinus(value.toLong() / 1000))
                }
                text += (it2.key + ": " + value) + "\n"
            }
            text += "---------------" + "\n"
        }

        return text
    }

    private fun printLog() {
        var text = ""
        mapLog.forEach {
            var value = it.value
            if (it.key == keyTimeAuto) {
                value = (DateTimeUtils.getTimeMinus(value.toLong() / 1000))
            }
            text += (it.key + ": " + value) + "\n"
        }
        tvLog.text = text
    }

    private fun log(key: String, value: Any) {
        mapLog[key] = value.toString()
    }

    private fun logTime() {
        if (AutoFishingPlayTogether.isRunning) {
            val key = keyTimeAuto
            val value = (mapLog.getOptLong(key, 0) + 1000).toString()
            log(key, value)
        }
    }


    var mapBlock = hashMapOf<String, String>()
    private fun isBlock(key: String): Boolean {
        val lastTime = mapBlock.getOptLong(key, 0)
        val now = System.currentTimeMillis() / 1000
        if (now - lastTime > 3) {
            mapBlock[key] = now.toString()
            return false
        }
        return true
    }

    fun logThaMoi() {
        val key = keyThaMoi
        if (!isBlock(key)) {
            val value = mapLog.getOptLong(key, 0) + 1
            log(key, value)
        }
    }

    fun logKeo() {
        val key = keyKeo
        if (!isBlock(key)) {
            val value = mapLog.getOptLong(key, 0) + 1
            log(key, value)
        }
    }

    fun logFishingSuccess() {
        val key = keyThanhCong
        if (!isBlock(key)) {
            val value = mapLog.getOptLong(key, 0) + 1
            log(key, value)
        }
    }

}