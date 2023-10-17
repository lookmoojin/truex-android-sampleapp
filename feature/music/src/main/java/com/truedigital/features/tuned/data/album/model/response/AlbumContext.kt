package com.truedigital.features.tuned.data.album.model.response

import com.google.gson.annotations.SerializedName

data class AlbumContext(@SerializedName("IsInCollection") var isFavourited: Boolean)
