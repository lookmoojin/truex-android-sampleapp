package com.truedigital.features.truecloudv3.provider

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.nhaarman.mockitokotlin2.anyOrNull
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.data.model.PhoneNumber
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.mockkStatic
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ContactProviderTest {
    lateinit var contactProvider: ContactProvider
    lateinit var contactProviderImpl: ContactProviderImpl
    private val context = mockk<Context>()
    private val contactsUriProvider = mockk<ContactsUriProvider>()
    private val uri: Uri = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        contactProvider = ContactProviderImpl(
            context = context,
            contactsUriProvider = contactsUriProvider
        )
        contactProviderImpl = ContactProviderImpl(
            context,
            contactsUriProvider = contactsUriProvider
        )
    }

    @Test
    fun `test case moveToNext false`() {
        // arrange
        val contextDataProviderWrapper: ContextDataProviderWrapper = mockk()
        val contextDataProvider = mockk<ContextDataProvider>()
        every { contextDataProviderWrapper.get() } returns contextDataProvider
        every { contextDataProvider.getDataContext() } returns context

        val contentResolver: ContentResolver = mockk()
        every { contentResolver.insert(any(), anyOrNull()) } returns mockkClass(Uri::class)
        every { context.contentResolver } returns contentResolver
        val mockCursor = mockk<Cursor>()
        every { mockCursor.moveToFirst() } returns true
        every { mockCursor.moveToNext() } returns false
        every { mockCursor.getColumnIndexOrThrow("data1") } returns 0
        every { mockCursor.getColumnIndexOrThrow("data2") } returns 1
        every { mockCursor.getColumnIndexOrThrow("data3") } returns 2
        every { mockCursor.getColumnIndexOrThrow("data5") } returns 3
        every { mockCursor.getString(0) } returns "displayName"
        every { mockCursor.getString(1) } returns "firstName"
        every { mockCursor.getString(2) } returns "lastName"
        every { mockCursor.getString(3) } returns "middleName"
        every { mockCursor.close() } just runs

        every {
            contentResolver.query(any(), any(), any(), any(), any())
        } returns mockCursor

        // act
        val response = contactProvider.getContacts()

        // assert
        assertEquals(mutableListOf(), response)
    }

    @Test
    fun `test case getContactsPhone success`() {
        // arrange
        val contextDataProviderWrapper: ContextDataProviderWrapper = mockk()
        val contextDataProvider = mockk<ContextDataProvider>()
        every { contextDataProviderWrapper.get() } returns contextDataProvider
        every { contextDataProvider.getDataContext() } returns context

        val contentResolver: ContentResolver = mockk()
        every { contentResolver.insert(any(), anyOrNull()) } returns mockkClass(Uri::class)
        every { context.contentResolver } returns contentResolver
        val mockCursor = mockk<Cursor>()

        every { mockCursor.moveToFirst() } returns true
        every { mockCursor.moveToNext() } returns true andThen false
        every { mockCursor.getColumnIndexOrThrow("_id") } returns 5
        every { mockCursor.getColumnIndexOrThrow("data1") } returns 1
        every { mockCursor.getColumnIndexOrThrow("data2") } returns 2
        every { mockCursor.getColumnIndexOrThrow("data3") } returns 3
        every { mockCursor.getString(5) } returns "id"
        every { mockCursor.getString(1) } returns "number"
        every { mockCursor.getInt(2) } returns 2
        every { mockCursor.getString(3) } returns "label"
        every { mockCursor.close() } just runs
        every { context.resources } returns mockk()
        mockkStatic(ContactsContract.CommonDataKinds.Phone::class)
        every {
            ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                any(),
                any(),
                any()
            )
        } returns "TypeLabel"

        every {
            contentResolver.query(any(), any(), any(), any(), any())
        } returns mockCursor
        every {
            contactsUriProvider.getContactUri()
        } returns uri

        // act
        val response = contactProviderImpl.getContactsPhone(mockCursor)

        // assert
        val expect = PhoneNumber(
            type = "TypeLabel",
            number = "number"
        )
        assertEquals(listOf(expect), response)
    }

    @Test
    fun `test case getContacts case true and false`() {
        val context = mockk<Context>()
        val cursor = mockk<Cursor>(relaxed = true)
        // mock the content resolver query method
        every { context.contentResolver.query(any(), any(), any(), any(), any()) } returns cursor

        // mock the cursor methods
        every { cursor.moveToNext() } returns true andThen false
        every { cursor.getString(any()) } returns "2"
        every { cursor.getString(1) } returns "1"
        every { cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME) } returns 0
        every { cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER) } returns 1
        every { cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID) } returns 2
        // create the contact provider
        val provider = ContactProviderImpl(context, contactsUriProvider)
        every {
            contactsUriProvider.getContactUri()
        } returns uri

        // get the contacts
        val contacts = provider.getContacts()

        // assert that the contacts list is not empty
        assert(contacts.isNotEmpty())
    }
}
