package com.truedigital.features.music.data.playlist.model

import com.google.gson.annotations.SerializedName
import com.truedigital.features.music.data.trending.model.response.playlist.Translation

data class CreateNewPlaylistRequest(
    @SerializedName("name") val name: List<Translation> = listOf()
)
