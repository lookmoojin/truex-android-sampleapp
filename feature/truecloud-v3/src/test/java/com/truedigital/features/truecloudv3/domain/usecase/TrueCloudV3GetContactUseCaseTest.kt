package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.model.ContactData
import com.truedigital.features.truecloudv3.data.model.ErrorResponse
import com.truedigital.features.truecloudv3.data.model.GetContactResponse
import com.truedigital.features.truecloudv3.data.model.PhoneNumber
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3ContactData
import com.truedigital.features.truecloudv3.data.repository.ContactRepository
import com.truedigital.features.truecloudv3.domain.model.ContactDataModel
import com.truedigital.features.truecloudv3.domain.model.ContactPhoneNumberModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3GetContactUseCaseTest {
    private val contactRepository: ContactRepository = mockk()
    private lateinit var getContactUseCase: GetContactUseCase
    private lateinit var checkContactUpdateUseCase: CheckContactUpdateUseCase

    @BeforeEach
    fun setup() {
        getContactUseCase = GetContactUseCaseImpl(
            contactRepository = contactRepository
        )
        checkContactUpdateUseCase = CheckContactUpdateUseCaseImpl(
            contactRepository = contactRepository
        )
    }

    @Test
    fun `test execute getContact success`() = runTest {
        // arrange
        val contactData = mutableListOf(TrueCloudV3ContactData())
        val contactTrueCloudModels = mutableListOf<ContactTrueCloudModel>()
        contactData.forEach {
            val phoneNumbers = mutableListOf<ContactPhoneNumberModel>()
            it.tel.forEach {
                phoneNumbers.add(
                    ContactPhoneNumberModel(
                        type = it.type,
                        number = it.number
                    )
                )
            }
            contactTrueCloudModels.add(
                ContactTrueCloudModel(
                    firstName = it.firstName,
                    lastName = it.lastName,
                    email = it.email,
                    tel = phoneNumbers
                )
            )
        }

        coEvery {
            contactRepository.getContact()
        } answers {
            flow {
                emit(contactData)
            }
        }

        // act
        val flow = getContactUseCase.execute()

        // assert
        flow.collectSafe { response ->
            assertEquals(contactTrueCloudModels.size, response.size)
        }
    }

    @Test
    fun `test executeCheckUpdateNetwork success`() = runTest {
        // arrange
        val contactData = ContactData(
            id = "id",
            parentObjectId = "parentObjectId",
            objectType = "objectType",
            name = "name",
            size = "size",
            mimeType = "mimeType",
            category = "category",
            coverImageKey = "coverImageKey",
            coverImageSize = "coverImageSize",
            updatedAt = "updatedAt",
            createdAt = "createdAt",
            checksum = "checksum",
            isUploaded = true,
            deviceId = "deviceId",
            lastModified = ""
        )
        val contactResponse = GetContactResponse(
            data = contactData,
            error = null

        )
        coEvery {
            contactRepository.getContactFromNetwork()
        } answers {
            flow {
                emit(contactResponse)
            }
        }
        val contactDataModel = ContactDataModel(
            id = contactData.id,
            userId = contactData.userId,
            objectType = contactData.objectType,
            parentObjectId = contactData.parentObjectId,
            name = contactData.name,
            mimeType = contactData.mimeType,
            coverImageKey = contactData.coverImageKey,
            coverImageSize = contactData.coverImageSize,
            category = contactData.category,
            size = contactData.size,
            checksum = contactData.checksum,
            isUploaded = contactData.isUploaded,
            deviceId = contactData.deviceId,
            updatedAt = contactData.updatedAt,
            createdAt = contactData.createdAt,
            lastModified = contactData.lastModified
        )

        // act
        val flow = checkContactUpdateUseCase.execute()

        // assert
        flow.collectSafe { response ->
            assertEquals(
                contactDataModel,
                response
            )
            assertEquals(response.id, contactDataModel.id)
            assertEquals(response.size, contactDataModel.size)
            assertEquals(response.coverImageKey, contactDataModel.coverImageKey)
            assertEquals(response.coverImageSize, contactDataModel.coverImageSize)
            assertEquals(response.name, contactDataModel.name)
            assertEquals(response.updatedAt, contactDataModel.updatedAt)
            assertEquals(response.parentObjectId, contactDataModel.parentObjectId)
            assertEquals(response.objectType, contactDataModel.objectType)
            assertEquals(response.mimeType, contactDataModel.mimeType)
            assertEquals(response.createdAt, contactDataModel.createdAt)
            assertEquals(response.category, contactDataModel.category)
            assertEquals(response.checksum, contactDataModel.checksum)
            assertEquals(response.isUploaded, contactDataModel.isUploaded)
            assertEquals(response.deviceId, contactDataModel.deviceId)
            assertEquals(response.lastModified, contactDataModel.lastModified)
        }
    }

    @Test
    fun `test executeCheckUpdateNetwork fail`() = runTest {
        // arrange
        val contactResponse = GetContactResponse(
            data = null,
            error = ErrorResponse(
                code = "ABC-123",
                message = "error message",
                cause = "unknown error"
            )

        )
        coEvery {
            contactRepository.getContactFromNetwork()
        } answers {
            flow {
                emit(contactResponse)
            }
        }

        // act
        val flow = checkContactUpdateUseCase.execute()

        // assert
        flow.catch { throwable ->
            assertEquals("Contact is not found", throwable.message)
        }.collect()
    }

    @Test
    fun `execute should return expected flow of contact true cloud models`() = runTest {
        // Arrange
        val expectedTrueCloudV3ContactData = mutableListOf(
            TrueCloudV3ContactData(
                firstName = "John",
                lastName = "Doe",
                email = "john.doe@example.com",
                tel = mutableListOf(
                    PhoneNumber(
                        type = "mobile",
                        number = "123-456-7890"
                    )
                )
            )
        )
        val expectedContactTrueCloudModels = mutableListOf(
            ContactTrueCloudModel(
                firstName = "John",
                lastName = "Doe",
                email = "john.doe@example.com",
                tel = mutableListOf(
                    ContactPhoneNumberModel(
                        type = "mobile",
                        number = "123-456-7890"
                    )
                )
            )
        )
        coEvery { contactRepository.getContact() } returns flow {
            emit(expectedTrueCloudV3ContactData)
        }

        // Act
        val result = getContactUseCase.execute().first()

        // Assert
        assertEquals(expectedContactTrueCloudModels.size, result.size)
        assertEquals(expectedContactTrueCloudModels.first().firstName, result.first().firstName)
        assertEquals(expectedContactTrueCloudModels.first().lastName, result.first().lastName)
        assertEquals(expectedContactTrueCloudModels.first().email, result.first().email)
    }
}
