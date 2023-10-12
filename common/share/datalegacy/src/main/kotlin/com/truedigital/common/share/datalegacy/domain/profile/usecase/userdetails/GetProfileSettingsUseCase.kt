package com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails

import com.truedigital.common.share.datalegacy.data.base.Failure
import com.truedigital.common.share.datalegacy.data.base.ResultResponse
import com.truedigital.common.share.datalegacy.data.repository.profile.ProfileRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.ProfileRepositoryImpl.Companion.MESSAGE_GET_PROFILE_SETTINGS_FAILED
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.ProfileData
import com.truedigital.core.extensions.collectSafe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

interface GetProfileSettingsUseCase {
    fun execute(): Flow<ResultResponse<ProfileData>>
}

class GetProfileSettingsUseCaseImpl @Inject constructor(
    private val profileRepository: ProfileRepository
) : GetProfileSettingsUseCase {

    override fun execute() = flow<ResultResponse<ProfileData>> {
        runCatching {
            withTimeout(3000) {
                profileRepository.getProfile()
            }
        }.onSuccess { result ->
            result.collectSafe {
                emit(it)
            }
        }.onFailure {
            emit(Failure(Throwable(message = MESSAGE_GET_PROFILE_SETTINGS_FAILED)))
        }
    }
}
