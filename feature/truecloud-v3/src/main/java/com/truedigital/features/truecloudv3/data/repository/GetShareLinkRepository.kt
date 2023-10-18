package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.ShareConfigRequest
import com.truedigital.features.truecloudv3.data.model.ShareConfigResponse
import com.truedigital.features.truecloudv3.data.model.ShareResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetShareLinkRepository {
    fun getShareLink(fileid: String): Flow<ShareResponse>
    fun getShareConfig(fileid: String): Flow<ShareConfigResponse>
    fun updateShareConfig(
        fileid: String,
        isPrivate: Boolean,
        password: String?,
        expireAt: String?,
        isNewPass: Boolean
    ): Flow<ShareConfigResponse>
}

class GetShareLinkRepositoryImpl @Inject constructor(
    private val trueCloudV3Interface: TrueCloudV3Interface,
    private val userRepository: UserRepository
) : GetShareLinkRepository {
    override fun getShareLink(fileid: String): Flow<ShareResponse> {
        return flow {
            trueCloudV3Interface.getShareLink(
                ssoid = userRepository.getSsoId(),
                fileid = fileid
            )
                .run {
                    val responseBody = body()
                    if (isSuccessful && responseBody != null) {
                        emit(responseBody)
                    } else {
                        error("${message()} (${code()})")
                    }
                }
        }
    }

    override fun getShareConfig(fileid: String): Flow<ShareConfigResponse> {
        return flow {
            trueCloudV3Interface.getShareConfig(
                ssoid = userRepository.getSsoId(),
                fileid = fileid
            )
                .run {
                    val responseBody = body()
                    if (isSuccessful && responseBody != null) {
                        emit(responseBody)
                    } else {
                        error("${message()} (${code()})")
                    }
                }
        }
    }

    override fun updateShareConfig(
        fileid: String,
        isPrivate: Boolean,
        password: String?,
        expireAt: String?,
        isNewPass: Boolean
    ): Flow<ShareConfigResponse> {
        return flow {
            val shareConfigRequest = ShareConfigRequest(
                isPrivate = isPrivate,
                password = password,
                expireAt = expireAt,
                isNewPassword = isNewPass
            )
            trueCloudV3Interface.updateShareConfig(
                ssoid = userRepository.getSsoId(),
                fileid = fileid,
                obj = shareConfigRequest
            )
                .run {
                    val responseBody = body()
                    if (isSuccessful && responseBody != null) {
                        emit(responseBody)
                    } else {
                        error("${message()} (${code()})")
                    }
                }
        }
    }
}
