package com.truedigital.common.share.data.coredata.domain.model

data class CommunicatorInitialAppEnableConfigModel(
    val isEnable: Boolean,
    val image: String = "",
    val errorMessage1: String = "",
    val errorMessage2: String = "",
    val errorMessage3: String = ""
)
