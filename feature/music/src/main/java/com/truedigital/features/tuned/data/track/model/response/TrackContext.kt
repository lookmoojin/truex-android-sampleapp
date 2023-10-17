package com.truedigital.features.tuned.data.track.model.response

import com.google.gson.annotations.SerializedName

data class TrackContext(@SerializedName("IsInCollection") var isFavourited: Boolean)
