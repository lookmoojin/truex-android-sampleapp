package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage.ERROR_COMPLETE_UPLOAD
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3UploadInterface
import com.truedigital.features.truecloudv3.data.model.CompleteUploadRequest
import com.truedigital.features.truecloudv3.data.model.CompleteUploadResponse
import com.truedigital.features.truecloudv3.data.model.ReplaceUploadRequest
import com.truedigital.features.truecloudv3.data.repository.UploadFileRepositoryImpl.Companion.KEY_COVER_IMAGE_SUFFIX
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface CompleteUploadRepository {
    fun callComplete(id: String, coverImageSize: Int?): Flow<CompleteUploadResponse>
    fun callReplaceComplete(id: String, coverImageSize: Int?): Flow<CompleteUploadResponse>
}

class CompleteUploadRepositoryImpl @Inject constructor(
    private val trueCloudV3UploadInterface: TrueCloudV3UploadInterface,
    private val userRepository: UserRepository
) : CompleteUploadRepository {

    companion object {
        private const val STATUS_SUCCESS = "SUCCESS"
        private const val ZERO = 0
    }

    override fun callComplete(id: String, coverImageSize: Int?): Flow<CompleteUploadResponse> {
        return flow {
            val request = CompleteUploadRequest(
                status = STATUS_SUCCESS,
                coverImageKey = getCoverKey(id, coverImageSize),
                coverImageSize = coverImageSize
            )
            trueCloudV3UploadInterface.completeUpload(
                ssoid = userRepository.getSsoId(),
                id = id,
                request = request
            )
                .run {
                    val responseBody = body()
                    if (isSuccessful && responseBody != null) {
                        emit(responseBody)
                    } else {
                        error(ERROR_COMPLETE_UPLOAD)
                    }
                }
        }
    }

    override fun callReplaceComplete(
        id: String,
        coverImageSize: Int?
    ): Flow<CompleteUploadResponse> {
        return flow {
            val request = ReplaceUploadRequest(
                status = STATUS_SUCCESS,
                coverImageKey = getCoverKey(id, coverImageSize),
                coverImageSize = coverImageSize
            )
            trueCloudV3UploadInterface.completeReplaceUpload(
                ssoid = userRepository.getSsoId(),
                id = id,
                request = request
            )
                .run {
                    val responseBody = body()
                    if (isSuccessful && responseBody != null) {
                        emit(responseBody)
                    } else {
                        error(ERROR_COMPLETE_UPLOAD)
                    }
                }
        }
    }

    private fun getCoverKey(id: String, coverImageSize: Int?): String? {
        return if (coverImageSize == ZERO) {
            id
        } else if (coverImageSize != null && coverImageSize > ZERO) {
            id + KEY_COVER_IMAGE_SUFFIX
        } else {
            null
        }
    }
}
