package com.truedigital.features.tuned.data.station.model

import com.google.gson.annotations.SerializedName

data class StationVote(@SerializedName("Vote") var vote: Vote, var success: Boolean)
