package com.truedigital.features.truecloudv3.data.model

import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.domain.model.HeaderType
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TrueCloudFileModelTest {
    @Test
    fun testSetTrueCloudFileModelUpload() {
        val trueCloudFileModel = TrueCloudFilesModel.Upload(
            id = 1234,
            path = "path",
            actionType = TaskActionType.UPLOAD,
            status = TaskStatusType.COMPLETE,
            objectId = "34se",
            size = "123",
            coverImageSize = 123,
            progress = 34,
            type = FileMimeType.VIDEO,
            name = "name"
        )
        trueCloudFileModel.size = "10"
        trueCloudFileModel.type = FileMimeType.VIDEO
        trueCloudFileModel.coverImageSize = 20
        trueCloudFileModel.progress = 35
        trueCloudFileModel.objectId = "34se"
        trueCloudFileModel.name = "name"
        trueCloudFileModel.updateAt = 123456
        trueCloudFileModel.status = TaskStatusType.COMPLETE

        assertEquals("10", trueCloudFileModel.size)
        assertEquals(FileMimeType.VIDEO, trueCloudFileModel.type)
        assertEquals(20, trueCloudFileModel.coverImageSize)
        assertEquals(35, trueCloudFileModel.progress)
        assertEquals("34se", trueCloudFileModel.objectId)
        assertEquals("name", trueCloudFileModel.name)
        assertEquals(1234, trueCloudFileModel.id)
        assertEquals("path", trueCloudFileModel.path)
        assertEquals(TaskActionType.UPLOAD, trueCloudFileModel.actionType)
        assertEquals(TaskStatusType.COMPLETE, trueCloudFileModel.status)
    }

    @Test
    fun testSetTrueCloudFileModelHeader() {
        val headerFileModel = TrueCloudFilesModel.Header(
            title = "file_title",
            headerType = HeaderType.FILE
        )
        val headerOTHERModel = TrueCloudFilesModel.Header(
            title = "other_title",
            headerType = HeaderType.OTHER
        )
        val headerUPLOADModel = TrueCloudFilesModel.Header(
            title = "upload_title",
            headerType = HeaderType.UPLOAD
        )

        assertEquals("file_title", headerFileModel.title)
        assertEquals("other_title", headerOTHERModel.title)
        assertEquals("upload_title", headerUPLOADModel.title)
        assertEquals(HeaderType.FILE, headerFileModel.headerType)
        assertEquals(HeaderType.OTHER, headerOTHERModel.headerType)
        assertEquals(HeaderType.UPLOAD, headerUPLOADModel.headerType)
    }

    @Test
    fun testSetTrueCloudFileModelFile() {
        val trueCloudFileModel = TrueCloudFilesModel.File(
            id = "1234",
            size = "123",
            coverImageSize = "123",
            parentObjectId = "34",
            mimeType = FileMimeType.VIDEO.mimeType,
            name = "name",
            category = "category",
            coverImageKey = "coverImageKey",
            updatedAt = "updatedAt",
            createdAt = "createdAt",
            selected = true,
        )
        trueCloudFileModel.size = "10"
        trueCloudFileModel.id = "1234"
        trueCloudFileModel.coverImageSize = "20"
        trueCloudFileModel.parentObjectId = "35"
        trueCloudFileModel.mimeType = FileMimeType.VIDEO.mimeType
        trueCloudFileModel.name = "name"
        trueCloudFileModel.category = "category"
        trueCloudFileModel.coverImageKey = "coverImageKey"
        trueCloudFileModel.updatedAt = "updatedAt"
        trueCloudFileModel.createdAt = "createdAt"
        trueCloudFileModel.selected = true
        trueCloudFileModel.objectType = "objectType"

        assertEquals("10", trueCloudFileModel.size)
        assertEquals(FileMimeType.VIDEO.mimeType, trueCloudFileModel.mimeType)
        assertEquals("20", trueCloudFileModel.coverImageSize)
        assertEquals("35", trueCloudFileModel.parentObjectId)
        assertEquals("1234", trueCloudFileModel.id)
        assertEquals("name", trueCloudFileModel.name)
        assertEquals("category", trueCloudFileModel.category)
        assertEquals("coverImageKey", trueCloudFileModel.coverImageKey)
        assertEquals("updatedAt", trueCloudFileModel.updatedAt)
        assertEquals("createdAt", trueCloudFileModel.createdAt)
        assertEquals(true, trueCloudFileModel.selected)
        assertEquals("objectType", trueCloudFileModel.objectType)
    }

    @Test
    fun testSetTrueCloudFileModelFolder() {
        val trueCloudFileModel = TrueCloudFilesModel.Folder(
            id = "1234",
            size = "123",
            coverImageSize = "123",
            parentObjectId = "34",
            mimeType = FileMimeType.VIDEO.mimeType,
            name = "name",
            category = "category",
            coverImageKey = "coverImageKey",
            updatedAt = "updatedAt",
            createdAt = "createdAt"
        )
        trueCloudFileModel.size = "10"
        trueCloudFileModel.id = "1234"
        trueCloudFileModel.coverImageSize = "20"
        trueCloudFileModel.parentObjectId = "35"
        trueCloudFileModel.mimeType = FileMimeType.VIDEO.mimeType
        trueCloudFileModel.name = "name"
        trueCloudFileModel.category = "category"
        trueCloudFileModel.coverImageKey = "coverImageKey"
        trueCloudFileModel.updatedAt = "updatedAt"
        trueCloudFileModel.createdAt = "createdAt"
        trueCloudFileModel.objectType = "objectType"

        assertEquals("10", trueCloudFileModel.size)
        assertEquals(FileMimeType.VIDEO.mimeType, trueCloudFileModel.mimeType)
        assertEquals("20", trueCloudFileModel.coverImageSize)
        assertEquals("35", trueCloudFileModel.parentObjectId)
        assertEquals("1234", trueCloudFileModel.id)
        assertEquals("name", trueCloudFileModel.name)
        assertEquals("category", trueCloudFileModel.category)
        assertEquals("coverImageKey", trueCloudFileModel.coverImageKey)
        assertEquals("updatedAt", trueCloudFileModel.updatedAt)
        assertEquals("createdAt", trueCloudFileModel.createdAt)
        assertEquals("objectType", trueCloudFileModel.objectType)
    }
}
