package com.truedigital.features.truecloudv3.domain.model

import android.graphics.Bitmap

data class ContactTrueCloudDisplayModel(
    val picture: Bitmap? = null,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val tel: List<ContactPhoneNumberModel> = listOf()
)
