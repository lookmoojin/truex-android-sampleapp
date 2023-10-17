package com.truedigital.features.tuned.data.station.model.response

import com.google.gson.annotations.SerializedName

data class StationContext(@SerializedName("IsInCollection") var isFavourited: Boolean)
