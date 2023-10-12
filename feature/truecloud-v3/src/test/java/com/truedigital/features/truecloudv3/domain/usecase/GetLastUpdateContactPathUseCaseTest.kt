package com.truedigital.features.truecloudv3.domain.usecase

import android.content.Context
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.data.model.ContactData
import com.truedigital.features.truecloudv3.data.repository.ContactRepository
import com.truedigital.features.truecloudv3.domain.model.ContactDataModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class GetLastUpdateContactPathUseCaseTest {
    private lateinit var getLastUpdateContactPathUseCase: GetLastUpdateContactPathUseCase
    private val contextDataProvider: ContextDataProvider = mockk()
    private val contactRepository: ContactRepository = mockk()

    @BeforeEach
    fun setup() {
        getLastUpdateContactPathUseCase = GetLastUpdateContactPathUseCaseImpl(
            contactRepository = contactRepository,
            contextDataProvider = contextDataProvider
        )
    }

    @Test
    fun `test executeGetUpdateAt success`() = runTest {
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
        val expectResponse = "UpdateAt"
        val mockContext = mockk<Context>()
        val mockFile = mockk<File>()
        coEvery {
            contactRepository.getUpdateAt()
        } returns expectResponse
        coEvery {
            contactRepository.setUpdateAt(any())
        } returns Unit

        every { contextDataProvider.getDataContext() } returns mockContext
        every { mockContext.cacheDir } returns mockFile
        every { mockFile.exists() } returns true
        every { mockFile.isFile } returns true
        every { mockFile.path } returns "/path"
        every { mockFile.absolutePath } returns "/path"

        // act
        val response = getLastUpdateContactPathUseCase.execute(contactDataModel)

        // assert
        response.collectSafe {
            assertEquals("/path/true_cloud_cache/id", it.second)
        }
    }
}
