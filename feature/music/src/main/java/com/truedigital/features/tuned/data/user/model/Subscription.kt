package com.truedigital.features.tuned.data.user.model

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.util.LocalisedString
import java.util.Date

data class Subscription(
    @SerializedName("IsEnabled") var isEnabled: Boolean,
    @SerializedName("SubscriptionId") var id: Int,
    @SerializedName("PackageId") var packageId: Int,
    @SerializedName("Key") var key: String,
    @SerializedName("StartDate") var startDate: Date,
    @SerializedName("EndDate") var endDate: Date,
    @SerializedName("IsAutoRenew") var isAutoRenew: Boolean,
    @SerializedName("Description") var description: List<LocalisedString>,
    @SerializedName("Name") var name: List<LocalisedString>,
    @SerializedName("Period") var period: Period,
    @SerializedName("RenewalCostId") var renewalCostId: Int?,
    @SerializedName("RenewalCostTitle") var renewalCostTitle: List<LocalisedString>
)
