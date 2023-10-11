package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.GetShareLinkRepository
import com.truedigital.features.truecloudv3.domain.model.ShareConfigModel
import com.truedigital.features.truecloudv3.domain.model.SharedFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetShareConfigUseCase {
    fun execute(
        fileId: String
    ): Flow<ShareConfigModel>
}

class GetShareConfigUseCaseImpl @Inject constructor(
    private val getShareLinkRepository: GetShareLinkRepository
) : GetShareConfigUseCase {
    override fun execute(fileId: String): Flow<ShareConfigModel> {
        return getShareLinkRepository
            .getShareConfig(fileId)
            .map { _shareResponse ->
                val id = _shareResponse.data?.id.orEmpty()
                val isPrivate = _shareResponse.data?.isPrivate ?: false
                val createdAt = _shareResponse.data?.createdAt.orEmpty()
                val password = _shareResponse.data?.password.orEmpty()
                val expireAt = _shareResponse.data?.expireAt.orEmpty()
                val updatedAt = _shareResponse.data?.updatedAt.orEmpty()

                ShareConfigModel(
                    sharedFile = SharedFile(
                        id = id,
                        isPrivate = isPrivate,
                        createdAt = createdAt,
                        expireAt = expireAt,
                        updatedAt = updatedAt,
                        password = password
                    )
                )
            }
    }
}
