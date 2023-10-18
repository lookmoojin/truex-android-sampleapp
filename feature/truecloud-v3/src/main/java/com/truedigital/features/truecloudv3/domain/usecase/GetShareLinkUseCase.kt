package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.GetShareLinkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetShareLinkUseCase {
    fun execute(
        fileId: String
    ): Flow<String>
}

class GetShareLinkUseCaseImpl @Inject constructor(
    private val getShareLinkRepository: GetShareLinkRepository
) : GetShareLinkUseCase {
    override fun execute(fileId: String): Flow<String> {
        return getShareLinkRepository
            .getShareLink(fileId)
            .map { _shareResponse ->
                _shareResponse.data?.sharedUrl.orEmpty()
            }
    }
}
