package com.truedigital.features.truecloudv3.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrueCloudV3ContactData(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val tel: MutableList<PhoneNumber> = mutableListOf()
) : Parcelable

@Parcelize
data class PhoneNumber(
    val type: String = "",
    val number: String = ""
) : Parcelable
