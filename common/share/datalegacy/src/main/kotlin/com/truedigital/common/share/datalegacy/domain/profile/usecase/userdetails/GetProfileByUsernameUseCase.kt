package com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails

import com.truedigital.common.share.datalegacy.data.base.Failure
import com.truedigital.common.share.datalegacy.data.base.Progress
import com.truedigital.common.share.datalegacy.data.base.ResultResponse
import com.truedigital.common.share.datalegacy.data.base.Success
import com.truedigital.common.share.datalegacy.data.repository.profile.ProfileRepository
import com.truedigital.common.share.datalegacy.domain.profile.model.ProfileModel
import com.truedigital.common.share.datalegacy.domain.profile.model.SocialConfigureTabModel
import com.truedigital.common.share.datalegacy.domain.profile.model.SocialSettingsModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetProfileByUsernameUseCase {
    fun execute(username: String, fields: String? = null): Flow<ResultResponse<ProfileModel>>
}

class GetProfileByUsernameUseCaseImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val localizationRepository: LocalizationRepository
) : GetProfileByUsernameUseCase {

    override fun execute(username: String, fields: String?): Flow<ResultResponse<ProfileModel>> {
        return profileRepository.getProfileByUsername(
            username,
            fields,
            localizationRepository.getAppCountryCode().lowercase()
        )
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
                            socialConfigureTab = result.data.socialConfigureTab?.map {
                                SocialConfigureTabModel(
                                    id = it.id ?: 0,
                                    title = it.title ?: "",
                                    order = it.order ?: 0,
                                    enable = it.enable ?: false
                                )
                            }
                            socialSettings = result.data.socialSettings?.let {
                                SocialSettingsModel(
                                    isTrueCardEnable = it.truecardDisplay ?: false,
                                    isBadgeEnable = it.badgeDisplay ?: false,
                                    isChatEnable = it.chatDisplay ?: false
                                )
                            }
                        }
                    )
                    is Failure -> result
                    is Progress -> result
                }
            }
    }
}
