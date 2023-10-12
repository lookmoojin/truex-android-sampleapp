package com.truedigital.features.truecloudv3.data.model

import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.features.truecloudv3.domain.model.AlphabetItemModel
import com.truedigital.features.truecloudv3.domain.model.ContactPhoneNumberModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.domain.model.DetailDialogModel
import com.truedigital.features.truecloudv3.domain.model.HeaderSelectionModel
import com.truedigital.features.truecloudv3.domain.usecase.NodePermission
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DataModelTest {

    @Test
    fun testAlphabetItem() {
        val alphabetItem = AlphabetItemModel()
        alphabetItem.alphabet = "A"
        alphabetItem.size = 12
        alphabetItem.isActive = true
        alphabetItem.position = 1
        alphabetItem.index = 1

        assert(alphabetItem.isActive)
        assertEquals(alphabetItem.index, 1)
        assertEquals(alphabetItem.position, 1)
        assertEquals(alphabetItem.size, 12)
        assertEquals(alphabetItem.alphabet, "A")
    }

    @Test
    fun testCompleteUploadRequest() {
        val request = CompleteUploadRequest(
            status = "status test",
            coverImageKey = "coverImageKey",
            coverImageSize = 123
        )

        assertEquals(request.status, "status test")
        assertEquals(request.coverImageSize, 123)
        assertEquals(request.coverImageKey, "coverImageKey")
    }

    @Test
    fun testCompleteUploadResponse() {
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

        trueCloudV3StorageData.id = "id"
        trueCloudV3StorageData.parentObjectId = "parentObjectId"
        trueCloudV3StorageData.objectType = "objectType"
        trueCloudV3StorageData.name = "name"
        trueCloudV3StorageData.size = "size"
        trueCloudV3StorageData.mimeType = "mimeType"
        trueCloudV3StorageData.category = "category"
        trueCloudV3StorageData.coverImageKey = "coverImageKey"
        trueCloudV3StorageData.coverImageSize = "coverImageSize"
        trueCloudV3StorageData.updatedAt = "updatedAt"
        trueCloudV3StorageData.createdAt = "createdAt"

        val errorResponse = ErrorResponse(
            code = "code",
            message = "message",
            cause = "cause"
        )
        val response = CompleteUploadResponse(
            data = trueCloudV3StorageData,
            error = errorResponse,
            code = 0,
            message = "message",
            platformModule = "platformModule",
            reportDashboard = "reportDashboard"
        )

        assertEquals(response.code, 0)
        assertEquals(response.message, "message")
        assertEquals(response.platformModule, "platformModule")
        assertEquals(response.reportDashboard, "reportDashboard")
        assertEquals(response.error, errorResponse)
        assertEquals(response.error?.cause, errorResponse.cause)
        assertEquals(response.error?.code, errorResponse.code)
        assertEquals(response.error?.message, errorResponse.message)
        assertEquals(response.data, trueCloudV3StorageData)
        assertEquals(response.data?.id, trueCloudV3StorageData.id)
        assertEquals(response.data?.size, trueCloudV3StorageData.size)
        assertEquals(response.data?.coverImageKey, trueCloudV3StorageData.coverImageKey)
        assertEquals(response.data?.coverImageSize, trueCloudV3StorageData.coverImageSize)
        assertEquals(response.data?.name, trueCloudV3StorageData.name)
        assertEquals(response.data?.updatedAt, trueCloudV3StorageData.updatedAt)
        assertEquals(response.data?.parentObjectId, trueCloudV3StorageData.parentObjectId)
        assertEquals(response.data?.objectType, trueCloudV3StorageData.objectType)
        assertEquals(response.data?.mimeType, trueCloudV3StorageData.mimeType)
        assertEquals(response.data?.createdAt, trueCloudV3StorageData.createdAt)
        assertEquals(response.data?.category, trueCloudV3StorageData.category)
    }

    @Test
    fun testInitialUploadResponse() {
        val secureTokenService = SecureTokenService(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
        )
        val s3Model = S3Model(
            bucket = "bucket",
            endpoint = "endpoint",
            secureTokenService = secureTokenService
        )

        val initialDataResponse = InitialDataResponse(
            id = "id",
            folderId = "folderId",
            mimeType = "mimeType",
            size = 1,
            s3 = s3Model
        )
        val errorResponse = ErrorResponse(
            code = "code",
            message = "message",
            cause = "cause"
        )
        val response = InitialUploadResponse(
            data = initialDataResponse,
            error = errorResponse,
            code = 0,
            message = "message",
            platformModule = "platformModule",
            reportDashboard = "reportDashboard"
        )

        assertEquals(response.code, 0)
        assertEquals(response.message, "message")
        assertEquals(response.platformModule, "platformModule")
        assertEquals(response.reportDashboard, "reportDashboard")
        assertEquals(response.error, errorResponse)
        assertEquals(response.error?.cause, errorResponse.cause)
        assertEquals(response.error?.code, errorResponse.code)
        assertEquals(response.error?.message, errorResponse.message)
        assertEquals(response.data, initialDataResponse)
    }

    @Test
    fun testGetContactResponse() {
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
        val errorResponse = ErrorResponse(
            code = "code",
            message = "message",
            cause = "cause"
        )
        val response = GetContactResponse(
            data = contactData,
            error = errorResponse,
            code = 0,
            message = "message",
            platformModule = "platformModule",
            reportDashboard = "reportDashboard"
        )

        assertEquals(response.code, 0)
        assertEquals(response.message, "message")
        assertEquals(response.platformModule, "platformModule")
        assertEquals(response.reportDashboard, "reportDashboard")
        assertEquals(response.error, errorResponse)
        assertEquals(response.error?.cause, errorResponse.cause)
        assertEquals(response.error?.code, errorResponse.code)
        assertEquals(response.error?.message, errorResponse.message)
        assertEquals(response.data, contactData)
        assertEquals(response.data?.id, contactData.id)
        assertEquals(response.data?.size, contactData.size)
        assertEquals(response.data?.coverImageKey, contactData.coverImageKey)
        assertEquals(response.data?.coverImageSize, contactData.coverImageSize)
        assertEquals(response.data?.name, contactData.name)
        assertEquals(response.data?.updatedAt, contactData.updatedAt)
        assertEquals(response.data?.parentObjectId, contactData.parentObjectId)
        assertEquals(response.data?.objectType, contactData.objectType)
        assertEquals(response.data?.mimeType, contactData.mimeType)
        assertEquals(response.data?.createdAt, contactData.createdAt)
        assertEquals(response.data?.category, contactData.category)
        assertEquals(response.data?.checksum, contactData.checksum)
        assertEquals(response.data?.isUploaded, contactData.isUploaded)
        assertEquals(response.data?.deviceId, contactData.deviceId)
        assertEquals(response.data?.lastModified, contactData.lastModified)
    }

    @Test
    fun testGetSTSResponse() {

        val errorResponse = ErrorResponse(
            code = "code",
            message = "message",
            cause = "cause"
        )
        val data = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )
        val response = GetSecureTokenServiceResponse(
            data = data,
            error = errorResponse,
            code = 0,
            message = "message",
            platformModule = "platformModule",
            reportDashboard = "reportDashboard"
        )

        assertEquals(response.code, 0)
        assertEquals(response.message, "message")
        assertEquals(response.platformModule, "platformModule")
        assertEquals(response.reportDashboard, "reportDashboard")
        assertEquals(response.error, errorResponse)
        assertEquals(response.error?.cause, errorResponse.cause)
        assertEquals(response.error?.code, errorResponse.code)
        assertEquals(response.error?.message, errorResponse.message)
        assertEquals(response.data, data)
        assertEquals(response.data?.accessToken, data.accessToken)
        assertEquals(response.data?.secretKey, data.secretKey)
        assertEquals(response.data?.sessionKey, data.sessionKey)
        assertEquals(response.data?.endpoint, data.endpoint)
        assertEquals(response.data?.expiresAt, data.expiresAt)
    }

    @Test
    fun testHeaderSelectionMode() {

        val headerSelectionModel = HeaderSelectionModel(
            key = "code",
            size = 1
        )

        assertEquals(headerSelectionModel.key, "code")
        assertEquals(headerSelectionModel.size, 1)
    }

    @Test
    fun testInitialDataResponse() {

        val secureTokenService = SecureTokenService(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
        )
        val s3Model = S3Model(
            bucket = "bucket",
            endpoint = "endpoint",
            secureTokenService = secureTokenService
        )

        val headerSelectionModel = InitialDataResponse(
            id = "id",
            folderId = "folderId",
            mimeType = "mimeType",
            size = 1,
            s3 = s3Model
        )

        assertEquals(headerSelectionModel.id, "id")
        assertEquals(headerSelectionModel.size, 1)
        assertEquals(headerSelectionModel.folderId, "folderId")
        assertEquals(headerSelectionModel.mimeType, "mimeType")
        assertEquals(headerSelectionModel.s3, s3Model)
        assertEquals(headerSelectionModel.s3?.bucket, s3Model.bucket)
        assertEquals(headerSelectionModel.s3?.endpoint, s3Model.endpoint)
        assertEquals(headerSelectionModel.s3?.secureTokenService, s3Model.secureTokenService)
        assertEquals(
            headerSelectionModel.s3?.secureTokenService?.accessToken,
            s3Model.secureTokenService?.accessToken
        )
        assertEquals(
            headerSelectionModel.s3?.secureTokenService?.secretKey,
            s3Model.secureTokenService?.secretKey
        )
        assertEquals(
            headerSelectionModel.s3?.secureTokenService?.expiresAt,
            s3Model.secureTokenService?.expiresAt
        )
        assertEquals(
            headerSelectionModel.s3?.secureTokenService?.sessionKey,
            s3Model.secureTokenService?.sessionKey
        )
    }

    @Test
    fun testInitialDownloadResponse() {

        val errorResponse = ErrorResponse(
            code = "code",
            message = "message",
            cause = "cause"
        )
        val secureTokenService = SecureTokenService(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
        )
        val s3Model = S3Model(
            bucket = "bucket",
            endpoint = "endpoint",
            secureTokenService = secureTokenService
        )

        val data = InitialDataResponse(
            id = "id",
            folderId = "folderId",
            mimeType = "mimeType",
            size = 1,
            s3 = s3Model
        )
        val response = InitialDownloadResponse(
            data = data,
            error = errorResponse,
            code = 0,
            message = "message",
            platformModule = "platformModule",
            reportDashboard = "reportDashboard"
        )

        assertEquals(response.code, 0)
        assertEquals(response.message, "message")
        assertEquals(response.platformModule, "platformModule")
        assertEquals(response.reportDashboard, "reportDashboard")
        assertEquals(response.error, errorResponse)
        assertEquals(response.error?.cause, errorResponse.cause)
        assertEquals(response.error?.code, errorResponse.code)
        assertEquals(response.error?.message, errorResponse.message)
        assertEquals(response.data, data)
    }

    @Test
    fun testInitUploadRequest() {

        val response = InitUploadRequest(
            parentObjectId = "parentObjectId",
            name = "name",
            mimeType = "mimeType",
            size = 1,
        )

        assertEquals(response.parentObjectId, "parentObjectId")
        assertEquals(response.name, "name")
        assertEquals(response.mimeType, "mimeType")
        assertEquals(response.size, 1)
    }

    @Test
    fun testListObjectResponse() {

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
        val dataListStorageResponse = DataListStorageResponse(
            uploaded = mutableListOf(trueCloudV3StorageData),
            storage = null
        )
        val errorResponse = ErrorResponse(
            code = "code",
            message = "message",
            cause = "cause"
        )
        val response = ListStorageResponse(
            data = dataListStorageResponse,
            error = errorResponse,
            code = 0,
            message = "message",
            platformModule = "platformModule",
            reportDashboard = "reportDashboard"
        )

        assertEquals(response.code, 0)
        assertEquals(response.message, "message")
        assertEquals(response.platformModule, "platformModule")
        assertEquals(response.reportDashboard, "reportDashboard")
        assertEquals(response.error, errorResponse)
        assertEquals(response.error?.cause, errorResponse.cause)
        assertEquals(response.error?.code, errorResponse.code)
        assertEquals(response.error?.message, errorResponse.message)
        assertEquals(response.data, dataListStorageResponse)
    }

    @Test
    fun testTrueCloudV3ObjectgetFileModel() {

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
        val response = trueCloudV3StorageData.getFilesModel()

        assertEquals(trueCloudV3StorageData.id, response.id)
        assertEquals(trueCloudV3StorageData.parentObjectId, response.parentObjectId)
        assertEquals(trueCloudV3StorageData.objectType, response.objectType)
        assertEquals(trueCloudV3StorageData.name, response.name)
        assertEquals(trueCloudV3StorageData.size, response.size)
        assertEquals(trueCloudV3StorageData.mimeType, response.mimeType)
        assertEquals(trueCloudV3StorageData.category, response.category)
        assertEquals(trueCloudV3StorageData.coverImageKey, response.coverImageKey)
        assertEquals(trueCloudV3StorageData.coverImageSize, response.coverImageSize)
        assertEquals(trueCloudV3StorageData.updatedAt, response.updatedAt)
        assertEquals(trueCloudV3StorageData.createdAt, response.createdAt)
    }

    @Test
    fun testTrueCloudV3ObjectgetFolderModel() {

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
        val response = trueCloudV3StorageData.getFolderModel()

        assertEquals(trueCloudV3StorageData.id, response.id)
        assertEquals(trueCloudV3StorageData.parentObjectId, response.parentObjectId)
        assertEquals(trueCloudV3StorageData.objectType, response.objectType)
        assertEquals(trueCloudV3StorageData.name, response.name)
        assertEquals(trueCloudV3StorageData.size, response.size)
        assertEquals(trueCloudV3StorageData.mimeType, response.mimeType)
        assertEquals(trueCloudV3StorageData.category, response.category)
        assertEquals(trueCloudV3StorageData.coverImageKey, response.coverImageKey)
        assertEquals(trueCloudV3StorageData.coverImageSize, response.coverImageSize)
        assertEquals(trueCloudV3StorageData.updatedAt, response.updatedAt)
        assertEquals(trueCloudV3StorageData.createdAt, response.createdAt)
    }

    @Test
    fun testContactData() {
        val phoneNumber = PhoneNumber(
            type = "type",
            number = "number"
        )
        val trueCloudV3Object = TrueCloudV3ContactData(
            firstName = "firstName",
            lastName = "lastName",
            email = "email",
            tel = mutableListOf(phoneNumber)
        )
        assertEquals(trueCloudV3Object.firstName, "firstName")
        assertEquals(trueCloudV3Object.lastName, "lastName")
        assertEquals(trueCloudV3Object.email, "email")
        assertEquals(trueCloudV3Object.tel, mutableListOf(phoneNumber))
        assertEquals(trueCloudV3Object.tel.first().type, phoneNumber.type)
        assertEquals(trueCloudV3Object.tel.first().number, phoneNumber.number)
    }

    @Test
    fun testContactDataModel() {
        val phoneNumber = ContactPhoneNumberModel(
            type = "type",
            number = "number"
        )
        val phoneNumber2 = ContactPhoneNumberModel(
            type = "type",
            number = "number"
        )
        val phoneNumber3 = ContactPhoneNumberModel(
            type = "type3",
            number = "number3"
        )
        val trueCloudV3Object3 = ContactTrueCloudModel(
            firstName = "firstName",
            lastName = "lastName",
            email = "email",
            tel = mutableListOf(phoneNumber3)
        )
        val trueCloudV3Object2 = ContactTrueCloudModel(
            firstName = "firstName",
            lastName = "lastName",
            email = "email",
            tel = mutableListOf(phoneNumber2)
        )
        val trueCloudV3Object = ContactTrueCloudModel(
            firstName = "firstName",
            lastName = "lastName",
            email = "email",
            tel = mutableListOf(phoneNumber)
        )
        assertEquals(trueCloudV3Object.firstName, "firstName")
        assertEquals(trueCloudV3Object.lastName, "lastName")
        assertEquals(trueCloudV3Object.email, "email")
        assertEquals(trueCloudV3Object.tel, mutableListOf(phoneNumber))
        assertEquals(trueCloudV3Object.tel.first().type, phoneNumber.type)
        assertEquals(trueCloudV3Object.tel.first().number, phoneNumber.number)
        assert(trueCloudV3Object.areContentsTheSame(trueCloudV3Object2))
        assert(!trueCloudV3Object.areContentsTheSame(trueCloudV3Object3))
        assert(
            !trueCloudV3Object.areContentsTheSame(
                ContactTrueCloudModel(
                    firstName = "firstName",
                    lastName = "lastName2",
                    email = "email",
                    tel = mutableListOf(phoneNumber)
                )
            )
        )
        assert(
            !trueCloudV3Object.areContentsTheSame(
                ContactTrueCloudModel(
                    firstName = "firstName",
                    lastName = "lastName",
                    email = "email2",
                    tel = mutableListOf(phoneNumber)
                )
            )
        )
        assert(
            !trueCloudV3Object.areContentsTheSame(
                ContactTrueCloudModel(
                    firstName = "firstName",
                    lastName = "lastName",
                    email = "email",
                    tel = mutableListOf(
                        phoneNumber,
                        ContactPhoneNumberModel(
                            type = "type2",
                            number = "number2"
                        )
                    )
                )
            )
        )
        assert(
            !trueCloudV3Object.areContentsTheSame(
                HeaderSelectionModel(
                    key = "key",
                    size = 1
                )
            )
        )
        assert(trueCloudV3Object.checkSameList(trueCloudV3Object2.tel, trueCloudV3Object.tel))
        assert(!trueCloudV3Object.checkSameList(trueCloudV3Object2.tel, trueCloudV3Object3.tel))
        assert(
            !trueCloudV3Object.checkSameList(
                trueCloudV3Object2.tel,
                mutableListOf(phoneNumber3, phoneNumber2)
            )
        )
    }

    @Test
    fun testContactDataModelCaseNotSame() {
        val phoneNumber = ContactPhoneNumberModel(
            type = "type",
            number = "number"
        )
        val phoneNumber2 = ContactPhoneNumberModel(
            type = "type2",
            number = "number2"
        )
        val trueCloudV3Object2 = ContactTrueCloudModel(
            firstName = "firstName2",
            lastName = "lastName2",
            email = "email2",
            tel = mutableListOf(phoneNumber2)
        )
        val trueCloudV3Object = ContactTrueCloudModel(
            firstName = "firstName",
            lastName = "lastName",
            email = "email",
            tel = mutableListOf(phoneNumber)
        )
        assertNotEquals(trueCloudV3Object.firstName, trueCloudV3Object2.firstName)
        assertNotEquals(trueCloudV3Object.lastName, trueCloudV3Object2.lastName)
        assertNotEquals(trueCloudV3Object.email, trueCloudV3Object2.email)
        assertNotEquals(trueCloudV3Object.tel, trueCloudV3Object2.tel)
        assertNotEquals(trueCloudV3Object.tel.first().type, trueCloudV3Object2.tel.first().type)
        assertNotEquals(trueCloudV3Object.tel.first().number, trueCloudV3Object2.tel.first().number)
        assert(!trueCloudV3Object.areContentsTheSame(trueCloudV3Object2))
        assert(!trueCloudV3Object.checkSameList(trueCloudV3Object2.tel, trueCloudV3Object.tel))
        assert(!trueCloudV3Object.areItemsTheSame(trueCloudV3Object2))
    }

    @Test
    fun testDetailDialogModel() {
        val nodePermission = NodePermission.STORAGE
        val detailDialogModel = DetailDialogModel(
            nodePermission = nodePermission,
            iconType = DialogIconType.CONTACT,
            title = "title",
            subTitle = "subTitle"
        )

        assertEquals(detailDialogModel.nodePermission, NodePermission.STORAGE)
        assertEquals(detailDialogModel.iconType, DialogIconType.CONTACT)
        assertEquals(detailDialogModel.title, "title")
        assertEquals(detailDialogModel.subTitle, "subTitle")
    }

    @Test
    fun testMigrationStatusRequest() {
        val detailDialogModel = MigrationStatusRequest()
        detailDialogModel.status = "status"

        assertEquals(detailDialogModel.status, "status")
    }

    @Test
    fun testRenameRequest() {
        val renameRequest = RenameRequest(
            name = "testname"
        )

        assertEquals("testname", renameRequest.name)
    }

    @Test
    fun testDataListObjectResponset() {
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
            createdAt = "createdAt",
            sharedObject = SharedObject(
                isPrivate = true
            )
        )
        val dataUsage = DataUsage(
            images = 0L,
            videos = 0L,
            audio = 0L,
            others = 0L,
            contacts = 0L,
            total = 0L,
            status = "",
            usagePercent = 0f
        )
        val renameRequest = DataListStorageResponse(
            uploaded = mutableListOf(trueCloudV3StorageData),
            storage = dataUsage
        )

        assertEquals(1, renameRequest.uploaded?.size)
        assertEquals(0L, dataUsage.images)
    }

    @Test
    fun testObjectRequest() {
        val objectRequest = ObjectsRequestModel(
            parentObjectId = "parent",
            objectIds = arrayListOf("1", "2")
        )
        assertEquals("parent", objectRequest.parentObjectId)
    }

    @Test
    fun testTrashObjectRequest() {
        val obj = TrashObjectRequestModel(
            objectIds = listOf("1", "2")
        )
        assertEquals("1", obj.objectIds[0])
    }
}
