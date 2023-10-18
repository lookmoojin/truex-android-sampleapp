package com.truedigital.features.tuned.data.user.model

import com.google.gson.annotations.SerializedName

data class Period(
    @SerializedName("PeriodId") var id: Int,
    @SerializedName("DateFrom") var dateFrom: String?,
    @SerializedName("DateTo") var dateTo: String?,
    @SerializedName("Status") var status: Int
)
