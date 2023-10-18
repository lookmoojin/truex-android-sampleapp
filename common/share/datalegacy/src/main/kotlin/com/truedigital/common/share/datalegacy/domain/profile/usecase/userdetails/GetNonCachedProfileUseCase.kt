package com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails

import com.truedigital.common.share.datalegacy.data.base.Failure
import com.truedigital.common.share.datalegacy.data.base.Progress
import com.truedigital.common.share.datalegacy.data.base.ResultResponse
import com.truedigital.common.share.datalegacy.data.base.Success
import com.truedigital.common.share.datalegacy.data.repository.profile.ProfileRepository
import com.truedigital.common.share.datalegacy.domain.profile.model.ProfileModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetNonCachedProfileUseCase {
    fun execute(): Flow<ResultResponse<ProfileModel>>
}

class GetNonCachedProfileUseCaseImpl @Inject constructor(private val profileRepository: ProfileRepository) :
    GetNonCachedProfileUseCase {

    override fun execute(): Flow<ResultResponse<ProfileModel>> {
        return profileRepository.getNonCachedProfile()
            .map { result ->
                when (result) {
                    is Success -> Success(
                        ProfileModel().apply {
                            ssoId = result.data.ssoid.orEmpty()
                            avatarUrl = result.data.avatar.orEmpty()
                            displayName = result.data.displayName.orEmpty()
                            userName = result.data.userName.orEmpty()
                            bio = result.data.bio.orEmpty()
                            language = result.data.settings?.language.orEmpty()
                            cover = result.data.cover
                            iconBorder = result.data.iconBorder.orEmpty()
                        }
                    )
                    is Failure -> result
                    is Progress -> result
                }
            }
    }
}
