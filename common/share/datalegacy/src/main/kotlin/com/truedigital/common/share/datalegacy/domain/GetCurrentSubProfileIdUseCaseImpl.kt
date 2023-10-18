package com.truedigital.common.share.datalegacy.domain

import com.truedigital.common.share.datalegacy.data.TvsNowCacheSourceRepository
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetCurrentSubProfileUseCase
import javax.inject.Inject

const val SUB_PROFILE_ID_ME = "me"

interface GetCurrentSubProfileIdUseCase {
    fun execute(): String
}

class GetCurrentSubProfileIdUseCaseImpl @Inject constructor(
    private val getCurrentSubProfileUseCase: GetCurrentSubProfileUseCase,
    private val tvsNowCacheSourceRepository: TvsNowCacheSourceRepository
) : GetCurrentSubProfileIdUseCase {

    override fun execute(): String {
        return if (tvsNowCacheSourceRepository.isOpenFromTrueVision()) {
            getCurrentSubProfileUseCase.execute()?.id?.takeIf {
                it.isNotEmpty() && it.isNotBlank()
            } ?: SUB_PROFILE_ID_ME
        } else {
            SUB_PROFILE_ID_ME
        }
    }
}
