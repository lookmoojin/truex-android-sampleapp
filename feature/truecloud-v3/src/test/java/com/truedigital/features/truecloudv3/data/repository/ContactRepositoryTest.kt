package com.truedigital.features.truecloudv3.data.repository

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.utils.DataStoreUtil
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.ContactData
import com.truedigital.features.truecloudv3.data.model.GetContactResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3ContactData
import com.truedigital.features.truecloudv3.provider.ContactProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ContactRepositoryTest {
    private lateinit var contactRepository: ContactRepository
    private val contactProvider: ContactProvider = mockk()
    private val trueCloudV3Interface: TrueCloudV3Interface = mockk()
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val dataStoreUtil: DataStoreUtil = mockk()

    @BeforeEach
    fun setup() {
        contactRepository = ContactRepositoryImpl(
            contactProvider = contactProvider,
            trueCloudV3Interface = trueCloudV3Interface,
            userRepository = userRepository,
            dataStoreInterface = dataStoreUtil
        )
    }

    @Test
    fun `test getContact return success`() = runTest {
        // arrange
        val trueCloudV3ContactData = TrueCloudV3ContactData(
            firstName = "firstName"
        )
        val mockResponse = mutableListOf(trueCloudV3ContactData)
        coEvery {
            contactProvider.getContacts()
        } returns mockResponse

        // act
        val flow = contactRepository.getContact()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
        }
    }

    @Test
    fun `test getContact return empty`() = runTest {
        // arrange
        coEvery {
            contactProvider.getContacts()
        } returns mutableListOf()

        // act
        val flow = contactRepository.getContact()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(emptyList(), response)
        }
    }

    @Test
    fun `test getContactFromNetwork return success`() = runTest {
        // arrange
        val contact = ContactData(
            id = "test id success",
            userId = "userId",
            objectType = "objectType",
            parentObjectId = "parentObjectId",
            name = "name",
            mimeType = "mimeType",
            coverImageKey = "coverImageKey",
            coverImageSize = "coverImageSize",
            category = "category",
            size = "size",
            checksum = "checksum",
            isUploaded = false,
            deviceId = "deviceId",
            updatedAt = "updatedAt",
            createdAt = "createdAt",
            lastModified = "lastModified"
        )
        val contactResponse = GetContactResponse(
            data = contact
        )
        coEvery {
            trueCloudV3Interface.getContact(
                ssoid = any()
            )
        } returns Response.success(
            contactResponse
        )

        // act
        val flow = contactRepository.getContactFromNetwork()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(contactResponse, response)
            val expect = contactResponse.data
            assertEquals(expect?.id, response.data?.id)
            assertEquals(expect?.name, response.data?.name)
            assertEquals(expect?.category, response.data?.category)
            assertEquals(expect?.mimeType, response.data?.mimeType)
            assertEquals(expect?.checksum, response.data?.checksum)
            assertEquals(expect?.coverImageKey, response.data?.coverImageKey)
            assertEquals(expect?.coverImageSize, response.data?.coverImageSize)
            assertEquals(expect?.createdAt, response.data?.createdAt)
            assertEquals(expect?.deviceId, response.data?.deviceId)
            assertEquals(expect?.isUploaded, response.data?.isUploaded)
            assertEquals(expect?.lastModified, response.data?.lastModified)
            assertEquals(expect?.objectType, response.data?.objectType)
            assertEquals(expect?.updatedAt, response.data?.updatedAt)
            assertEquals(expect?.userId, response.data?.userId)
            assertEquals(expect?.parentObjectId, response.data?.parentObjectId)
            assertEquals(expect?.size, response.data?.size)
        }
    }

    @Test
    fun `test getContactFromNetworkV3 return error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())

        coEvery {
            trueCloudV3Interface.getContact(
                ssoid = any()
            )
        } returns Response.error(
            404,
            responseBody
        )

        // act
        val flow = contactRepository.getContactFromNetwork()

        // assert
        flow.catch { response ->
            assertNotNull(response)
        }.collect()
    }

    @Test
    fun `test getContactUpdateAt return success`() = runTest {
        // arrange
        val expectResponse = "UpdateAt"
        coEvery {
            dataStoreUtil.getSinglePreference(
                key = any(),
                ""
            )
        } returns expectResponse

        // act
        val updateAt = contactRepository.getUpdateAt()

        // assert
        assertEquals(expectResponse, updateAt)
    }

    @Test
    fun `test getContactUpdateAt return empty`() = runTest {
        // arrange
        val expectResponse = ""
        coEvery {
            dataStoreUtil.getSinglePreference(
                key = any(),
                ""
            )
        } returns expectResponse

        // act
        val updateAt = contactRepository.getUpdateAt()

        // assert
        assertEquals(expectResponse, updateAt)
    }

    @Test
    fun `test setUpdateAt`() = runTest {
        // arrange
        val expectResponse = ""
        coEvery {
            dataStoreUtil.putPreference(
                key = eq(stringPreferencesKey("KEY_TURE_CLOUD_V3_UPDATE_AT")),
                value = "1234"
            )
        } returns Unit

        // act
        contactRepository.setUpdateAt("1234")

        // assert
        coVerify {
            dataStoreUtil.putPreference(
                key = eq(stringPreferencesKey("KEY_TURE_CLOUD_V3_UPDATE_AT")),
                value = "1234"
            )
        }
    }
    @Test
    fun `test setContactSynced true`() = runTest {
        // arrange
        val expectResponse = ""
        coEvery {
            dataStoreUtil.putPreference(
                key = eq(booleanPreferencesKey("KEY_TURE_CLOUD_V3_SYNCED_STATUS")),
                value = true
            )
        } returns Unit
        // act
        contactRepository.setContactSynced(true)

        // assert
        coVerify {
            dataStoreUtil.putPreference(
                key = eq(booleanPreferencesKey("KEY_TURE_CLOUD_V3_SYNCED_STATUS")),
                value = true
            )
        }
    }

    @Test
    fun `test hasContactSynced return true`() = runTest {
        // arrange
        coEvery {
            dataStoreUtil.getSinglePreference(
                key = any(),
                false
            )
        } returns true

        // act
        val hasSync = contactRepository.hasContactSynced()

        // assert
        assertTrue(hasSync)
    }
    @Test
    fun `test hasContactSynced return false`() = runTest {
        // arrange
        coEvery {
            dataStoreUtil.getSinglePreference(
                key = any(),
                false
            )
        } returns false

        // act
        val hasSync = contactRepository.hasContactSynced()

        // assert
        assertFalse(hasSync)
    }
}
