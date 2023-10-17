package com.truedigital.features.tuned.data.ad.model

import com.google.gson.annotations.SerializedName

enum class AdSourceType {
    @SerializedName("None", alternate = ["NONE"])
    NONE,

    @SerializedName("Station", alternate = ["STATION"])
    STATION,

    @SerializedName("Album", alternate = ["ALBUM"])
    ALBUM,

    @SerializedName("Playlist", alternate = ["PLAYLIST"])
    PLAYLIST,
}
