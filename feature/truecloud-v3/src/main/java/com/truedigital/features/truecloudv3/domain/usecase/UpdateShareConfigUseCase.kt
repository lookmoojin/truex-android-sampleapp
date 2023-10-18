package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.GetShareLinkRepository
import com.truedigital.features.truecloudv3.domain.model.ShareConfigModel
import com.truedigital.features.truecloudv3.domain.model.SharedFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface UpdateShareConfigUseCase {
    fun execute(
        fileid: String,
        isPrivate: Boolean,
        password: String?,
        expireAt: String?,
        isNewPass: Boolean
    ): Flow<ShareConfigModel>
}

class UpdateShareConfigUseCaseImpl @Inject constructor(
    private val getShareLinkRepository: GetShareLinkRepository
) : UpdateShareConfigUseCase {
    override fun execute(
        fileid: String,
        isPrivate: Boolean,
        password: String?,
        expireAt: String?,
        isNewPass: Boolean
    ): Flow<ShareConfigModel> {
        return getShareLinkRepository
            .updateShareConfig(
                fileid = fileid,
                isPrivate = isPrivate,
                password = password,
                expireAt = expireAt,
                isNewPass = isNewPass
            )
            .map { _shareResponse ->
                val id = _shareResponse.data?.id.orEmpty()
                val isPrivate = _shareResponse.data?.isPrivate ?: true
                val createdAt = _shareResponse.data?.createdAt.orEmpty()
                val updatedAt = _shareResponse.data?.updatedAt.orEmpty()
                val expireAt = _shareResponse.data?.expireAt.orEmpty()
                val password = _shareResponse.data?.password.orEmpty()

                ShareConfigModel(
                    sharedFile = SharedFile(
                        id = id,
                        isPrivate = isPrivate,
                        createdAt = createdAt,
                        expireAt = expireAt,
                        password = password,
                        updatedAt = updatedAt
                    )
                )
            }
    }
}
