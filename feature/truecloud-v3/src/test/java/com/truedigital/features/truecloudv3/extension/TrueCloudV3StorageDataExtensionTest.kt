package com.truedigital.features.truecloudv3.extension

import android.content.Context
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.injections.CoreComponent
import com.truedigital.core.injections.CoreSubComponent
import com.truedigital.core.utils.SharedPrefsUtils
import com.truedigital.features.truecloudv3.data.model.SharedObject
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TrueCloudV3StorageDataExtensionTest {

    val sharedPrefsUtils = mockk<SharedPrefsUtils>()
    val coreSubComponent = mockk<CoreSubComponent>()
    val localizationRepository = mockk<LocalizationRepository>()
    val localization = mockk<LocalizationRepository.Localization>()

    @BeforeEach
    fun setUp() {
        mockkObject(CoreComponent)
        every { CoreComponent.getInstance().getCoreSubComponent() } returns coreSubComponent
        every { coreSubComponent.getLocalizationRepository() } returns localizationRepository
        every { localizationRepository.getAppLocalization() } returns localization
        every { localizationRepository.getAppLocaleForEnTh() } returns Locale("th")
    }

    @Test
    fun convertTrueCloudV3Object() {
        // arrange
        val trueCloudV3StorageData = TrueCloudV3StorageData(
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
            createdAt = "2021-09-10T19:00:00.000Z",
            sharedObject = SharedObject(
                isPrivate = true
            )
        )
        val trueCloudV3Model = TrueCloudV3Model(
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
            createdAt = "2021-09-10T19:00:00.000Z",
            isPrivate = true
        )

        every { sharedPrefsUtils.get<String>(any()) } returns "test"
        // act
        val response = trueCloudV3StorageData.convertToTrueCloudV3Model()

        // assert
        assertEquals(trueCloudV3Model.id, response.id)
        assertEquals(trueCloudV3Model.parentObjectId, response.parentObjectId)
        assertEquals(trueCloudV3Model.objectType, response.objectType)
        assertEquals(trueCloudV3Model.name, response.name)
        assertEquals(trueCloudV3Model.size, response.size)
        assertEquals(trueCloudV3Model.mimeType, response.mimeType)
        assertEquals(trueCloudV3Model.category, response.category)
        assertEquals(trueCloudV3Model.coverImageKey, response.coverImageKey)
        assertEquals(trueCloudV3Model.coverImageSize, response.coverImageSize)
        assertEquals(trueCloudV3Model.updatedAt, response.updatedAt)
        assertNotEquals(trueCloudV3Model.createdAt, response.createdAt)
        assertEquals(trueCloudV3Model.isPrivate, response.isPrivate)
    }

    @Test
    fun convertTrueCloudV3ObjectList() {
        // arrange
        val trueCloudV3StorageData = TrueCloudV3StorageData(
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
            createdAt = "createdAt"
        )
        val trueCloudV3Model = TrueCloudV3Model(
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
            createdAt = "createdAt"
        )

        every { sharedPrefsUtils.get<String>(any()) } returns "test"

        // act
        val response = listOf(trueCloudV3StorageData).convertToListTrueCloudV3Model()

        // assert
        assertEquals(listOf(trueCloudV3Model).size, response.size)
    }

    @Test
    fun testConvertToFilesModel() {
        // arrange
        val context = mockk<Context>()
        val mockFile = mockk<File>()
        val trueCloudV3Model = TrueCloudV3Model(
            id = "id",
            parentObjectId = "parentObjectId",
            objectType = "objectType",
            name = "name",
            size = "100",
            mimeType = "mimeType",
            category = "category",
            coverImageKey = "coverImageKey",
            coverImageSize = "coverImageSize",
            updatedAt = "updatedAt",
            createdAt = "createdAt",
            isPrivate = true
        )
        every {
            sharedPrefsUtils.get<String>(any())
        } returns "getString"
        every { context.cacheDir } returns mockFile
        every { mockFile.absolutePath } returns "absolutePath"

        // act
        val response = trueCloudV3Model.convertToFilesModel(context)

        // assert
        assertEquals(trueCloudV3Model.id, response.id)
        assertEquals(trueCloudV3Model.isPrivate, response.isPrivate)
    }

    @Test
    fun testConvertToFolderModel() {
        // arrange
        val context = mockk<Context>()
        val trueCloudV3StorageData = TrueCloudV3StorageData(
            id = "id",
            parentObjectId = "parentObjectId",
            objectType = "objectType",
            name = "name",
            size = "101",
            mimeType = "mimeType",
            category = "category",
            coverImageKey = "coverImageKey",
            coverImageSize = "coverImageSize",
            updatedAt = "updatedAt",
            createdAt = "createdAt"
        )
        val trueCloudV3Model = TrueCloudV3Model(
            id = "id",
            parentObjectId = "parentObjectId",
            objectType = "objectType",
            name = "name",
            size = "100",
            mimeType = "mimeType",
            category = "category",
            coverImageKey = "coverImageKey",
            coverImageSize = "coverImageSize",
            updatedAt = "updatedAt",
            createdAt = "createdAt"
        )
        every {
            sharedPrefsUtils.get<String>(any())
        } returns "getString"

        // act
        val response = trueCloudV3Model.convertToFolderModel(context)

        // assert
        assertEquals(trueCloudV3Model.id, response.id)
    }

    @Test
    fun testConvertFolderToFileModel() {
        // arrange
        val context = mockk<Context>()
        val trueCloudV3Model = TrueCloudFilesModel.Folder(
            id = "id",
            parentObjectId = "parentObjectId",
            objectType = "objectType",
            name = "name",
            size = "1",
            mimeType = "mimeType",
            category = "category",
            coverImageKey = "coverImageKey",
            coverImageSize = "coverImageSize",
            updatedAt = "updatedAt",
            createdAt = "createdAt"
        )
        every {
            sharedPrefsUtils.get<String>(any())
        } returns "getString"
        // act
        val response = trueCloudV3Model.convertFolderToFileModel(context)

        // assert
        assertEquals(trueCloudV3Model.id, response.id)
    }

    @Test
    fun testConvertFileToFolderModel() {
        // arrange
        val context = mockk<Context>()
        val trueCloudV3Model = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
            objectType = "objectType",
            name = "name",
            size = "1",
            mimeType = "mimeType",
            category = "category",
            coverImageKey = "coverImageKey",
            coverImageSize = "coverImageSize",
            updatedAt = "updatedAt",
            createdAt = "createdAt"
        )
        every {
            sharedPrefsUtils.get<String>(any())
        } returns "getString"
        // act
        val response = trueCloudV3Model.convertFileToFolder(context)

        // assert
        assertEquals(trueCloudV3Model.id, response.id)
    }
}
