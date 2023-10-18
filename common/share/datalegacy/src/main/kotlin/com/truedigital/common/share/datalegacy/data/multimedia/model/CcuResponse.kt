package com.truedigital.common.share.datalegacy.data.multimedia.model

import com.google.gson.annotations.SerializedName

class CcuResponse {
    @SerializedName("code")
    val code = 0

    @SerializedName("desc")
    val description: String? = null

    @SerializedName("response")
    var response: Response? = null

    val isSuccess: Boolean
        get() = code == 200

    val ccu: Map<String, Int>
        get() {
            val results: MutableMap<String, Int> = HashMap()
            response?.itemMap?.let { responseItem ->
                for ((key, value) in responseItem) {
                    if (value != null) {
                        results[key] = value.total
                    }
                }
            }
            return results
        }

    class Response {
        @SerializedName("item")
        var itemMap: Map<String, Stats?>? = null
    }

    class Stats {
        @SerializedName("total")
        var total = 0
    }
}
