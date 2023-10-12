package com.truedigital.common.share.datalegacy.extension

import com.google.gson.Gson
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting

fun Setting.toJson(): String {
    return Gson().toJson(this)
}
