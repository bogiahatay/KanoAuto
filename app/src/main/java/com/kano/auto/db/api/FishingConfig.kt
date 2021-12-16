package com.kano.auto.db.api

class FishingConfig {

    var id = ""
    var avtive = true
    var arrDetectFishing = arrayListOf<DetectFishing>()

    companion object {
        fun get(iOnApiRequest: IOnApiRequest) {
            val api = "https://playtogether.kanoteam.com/api/config.json"
            val param = mapOf<String, String>()
            BaseApi.request(api, param, iOnApiRequest)
        }
    }
}

class DetectFishing {

    var id = 0
    var title = ""

    var timeDetectSlow = 1000
    var timeDetectFast = 400

    var xKeo = 0F
    var yKeo = 0F

    var xThaMoi = 0F
    var yThaMoi = 0F

    var xClickKeo = 0F
    var yClickKeo = 0F

    var xBaoQuan = 0F
    var yBaoQuan = 0F

    var xBaoQuan2 = 0F
    var yBaoQuan2 = 0F

    var xTui = 0F
    var yTui = 0F

    var xCongCu = 0F
    var yCongCu = 0F

    var xCan1 = 0F
    var yCan1 = 0F

    var xCan2 = 0F
    var yCan2 = 0F

    var xCan3 = 0F
    var yCan3 = 0F

    var xCan4 = 0F
    var yCan4 = 0F

    var xCan5 = 0F
    var yCan5 = 0F

    var xCan6 = 0F
    var yCan6 = 0F

}