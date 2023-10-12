package com.truedigital.features.truecloudv3.domain.model

interface ContactBaseModel {
    fun areItemsTheSame(newItem: ContactBaseModel): Boolean
    fun areContentsTheSame(newItem: ContactBaseModel): Boolean
}
interface Contact : ContactBaseModel
