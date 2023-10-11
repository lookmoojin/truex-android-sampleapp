package com.truedigital.features.truecloudv3.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class DataStorageModel(
    var quota: Long = 0,
    var rootFolder: TrueCloudV3Model? = null,
    var dataUsage: DataUsageModel? = null,
    var migration: DataMigrationModel? = null
)
@Parcelize
data class DataUsageModel(
    var images: Long = 0,
    var videos: Long = 0,
    var audio: Long = 0,
    var others: Long = 0,
    var contacts: Long = 0,
    var total: Long = 0,
    var sortedObj: MutableList<Pair<String, Long?>>? = null
) : Parcelable

data class DataMigrationModel(
    var status: String = "",
    var estimatedTimeToComplete: String? = null,
    var failedDisplayTime: Int? = null
)
