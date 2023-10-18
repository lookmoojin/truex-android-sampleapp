package com.truedigital.features.truecloudv3.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ContactTrueCloudModel(
    val picture: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val tel: List<ContactPhoneNumberModel> = listOf()
) : Contact, Parcelable {

    fun areContentsTheSame(newItem: ContactTrueCloudModel): Boolean {
        return (
            picture.equals(newItem.picture) &&
                firstName.equals(newItem.firstName) &&
                lastName.equals(newItem.lastName) &&
                email.equals(newItem.email) &&
                tel.size == newItem.tel.size &&
                checkSameList(
                    tel,
                    newItem.tel
                )
            )
    }

    fun checkSameList(
        old: List<ContactPhoneNumberModel>,
        new: List<ContactPhoneNumberModel>
    ): Boolean {
        return old.size == new.size && old.toSet() == new.toSet()
    }

    override fun areItemsTheSame(newItem: ContactBaseModel): Boolean {
        return false
    }

    override fun areContentsTheSame(newItem: ContactBaseModel): Boolean {
        return false
    }
}

@Parcelize
data class ContactPhoneNumberModel(
    val type: String = "",
    val number: String = ""
) : Parcelable
