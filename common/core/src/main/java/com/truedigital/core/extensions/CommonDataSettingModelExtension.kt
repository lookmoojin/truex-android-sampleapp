package com.truedigital.core.extensions

import com.google.gson.Gson
import com.truedigital.core.data.CommonDataSettingModel

fun CommonDataSettingModel.toJson(): String {
    return Gson().toJson(this)
}
