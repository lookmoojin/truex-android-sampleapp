package com.truedigital.features.truecloudv3.data.api

import com.google.gson.JsonObject
import com.truedigital.features.truecloudv3.data.model.CreateFolderRequest
import com.truedigital.features.truecloudv3.data.model.CreateFolderResponse
import com.truedigital.features.truecloudv3.data.model.DeleteStorageResponse
import com.truedigital.features.truecloudv3.data.model.GetContactResponse
import com.truedigital.features.truecloudv3.data.model.GetSecureTokenServiceResponse
import com.truedigital.features.truecloudv3.data.model.ListStorageResponse
import com.truedigital.features.truecloudv3.data.model.MigrateResponse
import com.truedigital.features.truecloudv3.data.model.MigrationStatusRequest
import com.truedigital.features.truecloudv3.data.model.ObjectInfoResponse
import com.truedigital.features.truecloudv3.data.model.ObjectsRequestModel
import com.truedigital.features.truecloudv3.data.model.RenameRequest
import com.truedigital.features.truecloudv3.data.model.RenameStorageResponse
import com.truedigital.features.truecloudv3.data.model.ShareConfigRequest
import com.truedigital.features.truecloudv3.data.model.ShareConfigResponse
import com.truedigital.features.truecloudv3.data.model.ShareResponse
import com.truedigital.features.truecloudv3.data.model.StorageResponse
import com.truedigital.features.truecloudv3.data.model.TrashObjectRequestModel
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TrueCloudV3Interface {

    @GET("v1/users/{ssoid}/storage")
    suspend fun storage(
        @Path("ssoid") ssoid: String
    ): Response<StorageResponse>

    @GET("v1/users/{ssoid}/folders/{rootFolderId}")
    suspend fun getFileList(
        @Path("ssoid") ssoid: String,
        @Path("rootFolderId") rootFolderId: String,
        @Query("order") order: String,
        @Query("skip") skip: Int,
        @Query("take") take: Int
    ): Response<ListStorageResponse>

    @POST("v1/users/{ssoid}/folder")
    suspend fun createFolder(
        @Path("ssoid") ssoid: String,
        @Body request: CreateFolderRequest
    ): Response<CreateFolderResponse>

    @DELETE("v1/users/{ssoid}/files/{objectId}")
    suspend fun deleteObject(
        @Path("ssoid") ssoid: String,
        @Path("objectId") objectId: String
    ): Response<DeleteStorageResponse>

    @PATCH("v1/users/{ssoid}/files/{objectId}/rename")
    suspend fun renameObject(
        @Path("ssoid") ssoid: String,
        @Path("objectId") fileId: String,
        @Body name: RenameRequest
    ): Response<RenameStorageResponse>

    @GET("v1/users/{ssoid}/files")
    suspend fun getCategoryFileList(
        @Path("ssoid") ssoid: String,
        @Query("category") category: String,
        @Query("order") order: String,
        @Query("skip") skip: Int,
        @Query("take") take: Int
    ): Response<ListStorageResponse>

    @POST("v1/users/{ssoid}/sts")
    suspend fun getSecureTokenService(
        @Path("ssoid") ssoid: String,
        @Body obj: JSONObject
    ): Response<GetSecureTokenServiceResponse>

    @GET("v1/users/{ssoid}/contact")
    suspend fun getContact(
        @Path("ssoid") ssoid: String
    ): Response<GetContactResponse>

    @POST("v1/users/{ssoid}/migration")
    suspend fun migrateData(
        @Path("ssoid") ssoid: String,
        @Body obj: JSONObject
    ): Response<MigrateResponse>

    @PATCH("v1/users/{ssoid}/migration")
    suspend fun updateMigrationStatus(
        @Path("ssoid") ssoid: String,
        @Body obj: MigrationStatusRequest
    ): Response<MigrateResponse>

    @POST("v1/users/{ssoid}/files/{fileid}/share")
    suspend fun getShareLink(
        @Path("ssoid") ssoid: String,
        @Path("fileid") fileid: String,
    ): Response<ShareResponse>

    @GET("v1/users/{ssoid}/files/{fileid}/share")
    suspend fun getShareConfig(
        @Path("ssoid") ssoid: String,
        @Path("fileid") fileid: String,
    ): Response<ShareConfigResponse>

    @PATCH("v1/users/{ssoid}/files/{fileid}/share")
    suspend fun updateShareConfig(
        @Path("ssoid") ssoid: String,
        @Path("fileid") fileid: String,
        @Body obj: ShareConfigRequest
    ): Response<ShareConfigResponse>

    @POST("v1/users/{ssoid}/objects/{type}")
    suspend fun moveObject(
        @Path("ssoid") ssoid: String,
        @Path("type") type: String,
        @Body obj: ObjectsRequestModel
    ): Response<JsonObject>

    @POST("v1/users/{ssoid}/objects/trash/move")
    suspend fun moveToTrash(
        @Path("ssoid") ssoid: String,
        @Body obj: TrashObjectRequestModel
    ): Response<JsonObject>

    @GET("v1/users/{ssoid}/objects/trash")
    suspend fun getTrashList(
        @Path("ssoid") ssoid: String
    ): Response<ListStorageResponse>

    @POST("v1/users/{ssoid}/objects/trash/restore")
    suspend fun restoreTrashData(
        @Path("ssoid") ssoid: String,
        @Body obj: TrashObjectRequestModel
    ): Response<JsonObject>

    @POST("v1/users/{ssoid}/objects/trash/delete")
    suspend fun deleteTrashData(
        @Path("ssoid") ssoid: String,
        @Body obj: TrashObjectRequestModel
    ): Response<JsonObject>

    @POST("v1/users/{ssoid}/objects/trash/empty")
    suspend fun emptyTrashData(
        @Path("ssoid") ssoid: String,
        @Body obj: TrashObjectRequestModel
    ): Response<JsonObject>

    @GET("v1/users/{ssoid}/objects/{fileid}")
    suspend fun getObjectInfo(
        @Path("ssoid") ssoid: String,
        @Path("fileid") fileid: String,
        @Query("presignUrl") presignUrl: Boolean = true,
    ): Response<ObjectInfoResponse>
}
