package net.iblankdigital.auto.ext

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

fun Any.toJson(): String {
    return Gson().toJson(this)
}

fun Any.toJsonPretty(): String {
    return GsonBuilder().setPrettyPrinting().create().toJson(this)
}


fun <T> String.toObject(classObj: Class<T>): T {
    return Gson().fromJson(this, classObj)
}


fun <T> String.toArray(): ArrayList<T> {
    val listType = object : TypeToken<ArrayList<T>>() {}.type
    return Gson().fromJson(this, listType)
}
