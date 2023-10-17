package com.truedigital.features.tuned.data.playlist.model.response

import com.google.gson.annotations.SerializedName

data class PlaylistContext(@SerializedName("IsInCollection") var isFavourited: Boolean)
