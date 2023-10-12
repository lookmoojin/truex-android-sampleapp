package com.truedigital.features.truecloudv3.provider

import com.google.gson.Gson
import javax.inject.Inject

interface GsonProvider {
    fun <T> getDataClass(rawData: String, clazz: Class<T>): T
}
class GsonProviderImpl @Inject constructor() : GsonProvider {
    override fun <T> getDataClass(rawData: String, clazz: Class<T>): T {
        return Gson().fromJson(rawData, clazz)
    }
}
