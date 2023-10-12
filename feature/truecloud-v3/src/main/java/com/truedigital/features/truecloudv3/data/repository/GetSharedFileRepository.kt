package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage.ERROR_GET_PRIVATE_SHARED_FILE
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage.ERROR_GET_SHARED_FILE_ACCESS_TOKEN
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3GetSharedFileInterface
import com.truedigital.features.truecloudv3.data.model.GetSharedFileResponseModel
import com.truedigital.features.truecloudv3.data.model.GetSharedObjectAccessTokenRequestModel
import com.truedigital.features.truecloudv3.data.model.SharedObjectAccessResponseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetSharedFileRepository {
    fun getPublicSharedObject(encryptedSharedObjectId: String): Flow<GetSharedFileResponseModel>
    fun getPrivateSharedObject(
        encryptedSharedObjectId: String,
        sharedObjectAccessToken: String
    ): Flow<GetSharedFileResponseModel>

    fun getSharedObjectAccessToken(
        getSharedObjectAccessTokenRequestModel: GetSharedObjectAccessTokenRequestModel
    ): Flow<SharedObjectAccessResponseModel?>
}

class GetSharedFileRepositoryImpl @Inject constructor(
    private val trueCloudV3GetSharedFileInterface: TrueCloudV3GetSharedFileInterface
) : GetSharedFileRepository {

    companion object {
        private const val OK_STATUS_CODE = 200
        private const val UNAUTHORIZED_STATUS_CODE = 401
    }

    override fun getPublicSharedObject(encryptedSharedObjectId: String): Flow<GetSharedFileResponseModel> {
        return flow {
            trueCloudV3GetSharedFileInterface.getPublicSharedFile(encryptedSharedObjectId).run {
                val responseBody = body()
                val statusCode = code()
                val responseMessage = message()
                if (statusCode != OK_STATUS_CODE) {
                    val getSharedFileResponseModel =
                        GetSharedFileResponseModel(
                            statusCode = statusCode,
                            statusMessage = responseMessage
                        )
                    emit(getSharedFileResponseModel)
                } else if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                }
            }
        }
    }

    override fun getPrivateSharedObject(
        encryptedSharedObjectId: String,
        sharedObjectAccessToken: String
    ): Flow<GetSharedFileResponseModel> {
        val shareFileAccessTokenHeader = "Bearer $sharedObjectAccessToken"
        return flow {
            trueCloudV3GetSharedFileInterface.getPrivateSharedFile(
                encryptedSharedObjectId,
                shareFileAccessTokenHeader
            ).run {
                val responseBody = body()
                if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                } else {
                    error(ERROR_GET_PRIVATE_SHARED_FILE)
                }
            }
        }
    }

    override fun getSharedObjectAccessToken(
        getSharedObjectAccessTokenRequestModel: GetSharedObjectAccessTokenRequestModel
    ): Flow<SharedObjectAccessResponseModel?> {
        return flow {
            trueCloudV3GetSharedFileInterface.getSharedObjectAccessToken(
                getSharedObjectAccessTokenRequestModel
            ).run {
                val responseBody = body()
                val statusCode = code()
                if (statusCode == UNAUTHORIZED_STATUS_CODE) {
                    emit(null)
                } else if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                } else {
                    error(ERROR_GET_SHARED_FILE_ACCESS_TOKEN)
                }
            }
        }
    }
}
