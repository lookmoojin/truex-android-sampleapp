package com.truedigital.common.share.data.coredata.domain.config.model

import java.io.Serializable

data class MaintenanceInfo(
    var aButtonEn: String? = null,
    var aButtonTh: String? = null,
    var bButtonEn: String? = null,
    var bButtonTh: String? = null,
    var description: HashMap<*, *>? = null,
    var descriptionEn: String? = null,
    var descriptionTh: String? = null,
    var hideBtnClose: String? = null,
    var image: HashMap<*, *>? = null,
    var imageEn: String? = null,
    var imageTh: String? = null,
    var isShow: String? = null,
    var title: HashMap<*, *>? = null,
    var titleEn: String? = null,
    var titleTh: String? = null,

    val maintenanceTime: MaintenanceTime = MaintenanceTime()
) : Serializable

data class MaintenanceTime(
    var currentDateTime: Long = 0,
    var endDate: String? = null,
    var startDate: String? = null
) : Serializable
