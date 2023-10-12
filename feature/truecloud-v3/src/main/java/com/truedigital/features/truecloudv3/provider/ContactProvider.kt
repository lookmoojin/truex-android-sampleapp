package com.truedigital.features.truecloudv3.provider

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import androidx.annotation.VisibleForTesting
import com.truedigital.features.truecloudv3.data.model.PhoneNumber
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3ContactData
import javax.inject.Inject

interface ContactProvider {
    fun getContacts(): MutableList<TrueCloudV3ContactData>
}

class ContactProviderImpl @Inject constructor(
    private val context: Context,
    private val contactsUriProvider: ContactsUriProvider
) : ContactProvider {

    companion object {
        private const val EMPTY_PHONE_NUMBER = 0
    }

    override fun getContacts(): MutableList<TrueCloudV3ContactData> {
        val contacts = mutableListOf<TrueCloudV3ContactData>()
        context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ")ASC"
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val contactName =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumbers = mutableListOf<PhoneNumber>()
                if (Integer.parseInt(
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    ) > EMPTY_PHONE_NUMBER
                ) {
                    phoneNumbers.addAll(getContactsPhone(cursor))
                }
                val contactData = TrueCloudV3ContactData(
                    firstName = contactName,
                    tel = phoneNumbers
                )
                if (!contactName.isNullOrEmpty()) contacts.add(contactData)
            }
            cursor.close()
        }
        return contacts
    }

    @VisibleForTesting
    fun getContactsPhone(cursor: Cursor): MutableList<PhoneNumber> {
        val phoneNumbers = mutableListOf<PhoneNumber>()
        contactsUriProvider.getContactUri()?.let {
            context.contentResolver.query(
                it,
                null,
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                arrayOf(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))),
                null
            )?.use { phoneCursor ->
                while (phoneCursor.moveToNext()) {
                    val contactNumber = phoneCursor.getString(
                        phoneCursor.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        )
                    )
                    val phoneType = phoneCursor.getInt(
                        phoneCursor.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.TYPE
                        )
                    )
                    val customLabel = phoneCursor.getString(
                        phoneCursor.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.LABEL
                        )
                    )
                    val phoneLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                        context.resources,
                        phoneType,
                        customLabel
                    ) as String
                    phoneNumbers.add(
                        PhoneNumber(type = phoneLabel, number = contactNumber)
                    )
                }
            }
        }
        return phoneNumbers
    }
}
