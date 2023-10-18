package com.truedigital.features.music.extensions

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting

fun JsonElement.toSettingModel(): Setting? {
    return try {
        Gson().fromJson(this, Setting::class.java)
    } catch (jsonParseException: JsonParseException) {
        null
    }
}
