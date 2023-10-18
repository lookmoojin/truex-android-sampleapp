package com.truedigital.features.truecloudv3.provider

import android.net.Uri
import android.provider.ContactsContract
import javax.inject.Inject

interface ContactsUriProvider {
    fun getContactUri(): Uri?
}

class ContactsUriProviderImpl @Inject constructor() : ContactsUriProvider {
    override fun getContactUri(): Uri? {
        return ContactsContract.CommonDataKinds.Phone.CONTENT_URI?.buildUpon()
            ?.appendQueryParameter(ContactsContract.REMOVE_DUPLICATE_ENTRIES, "1")
            ?.build()
    }
}
