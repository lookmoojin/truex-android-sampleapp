package com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.datalegacy.data.base.Success
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting
import com.truedigital.common.share.datalegacy.data.repository.profile.ProfileRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.CoverData
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.ProfileData
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.SocialConfigureTab
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.SocialSettings
import com.truedigital.common.share.datalegacy.domain.profile.model.SocialConfigureTabModel
import com.truedigital.common.share.datalegacy.domain.profile.model.SocialSettingsModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetProfileByUsernameUseCaseImplTest {
    private lateinit var getProfileByUsernameUseCase: GetProfileByUsernameUseCase
    private val profileRepository: ProfileRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()

    @BeforeEach
    fun setUp() {
        getProfileByUsernameUseCase = GetProfileByUsernameUseCaseImpl(
            profileRepository,
            localizationRepository
        )
    }

    @Test
    fun `test execute get profile by username success`() = runTest {
        val fieldMock = "social_configure_tab"
        val ssoIdMock = "ssoId"
        val avatarUrlMock = "avatarUrl"
        val displayNameMock = "displayName"
        val userNameMock = "userName"
        val bioMock = "bio"
        val languageMock = "language"
        val coverMock = CoverData()
        val iconBorderMock = "iconBorder"
        val socialConfigTabMock = listOf(
            SocialConfigureTabModel().apply {
                id = 1
                title = "tab_1"
                order = 1
                enable = true
            },
            SocialConfigureTabModel().apply {
                id = 2
                title = "tab_2"
                order = 2
                enable = true
            }
        )
        val socialSettingMock = SocialSettingsModel().apply {
            isTrueCardEnable = true
            isBadgeEnable = true
            isChatEnable = true
        }

        val response = Success(
            ProfileData().apply {
                ssoid = ssoIdMock
                avatar = avatarUrlMock
                displayName = displayNameMock
                userName = userNameMock
                bio = bioMock
                settings = Setting().apply { language = languageMock }
                cover = coverMock
                iconBorder = iconBorderMock
                socialConfigureTab = listOf(
                    SocialConfigureTab().apply {
                        id = 1
                        title = "tab_1"
                        order = 1
                        enable = true
                    },
                    SocialConfigureTab().apply {
                        id = 2
                        title = "tab_2"
                        order = 2
                        enable = true
                    }
                )
                socialSettings = SocialSettings().apply {
                    truecardDisplay = true
                    badgeDisplay = true
                    chatDisplay = true
                }
            }
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn("TH")
        whenever(profileRepository.getProfileByUsername(userNameMock, fieldMock, "th"))
            .thenReturn(flowOf(response))

        getProfileByUsernameUseCase.execute(userNameMock, fieldMock).collect { result ->
            assert(result is Success)
            val data = (result as? Success)?.data
            assertEquals(ssoIdMock, data?.ssoId)
            assertEquals(avatarUrlMock, data?.avatarUrl)
            assertEquals(displayNameMock, data?.displayName)
            assertEquals(userNameMock, data?.userName)
            assertEquals(bioMock, data?.bio)
            assertEquals(languageMock, data?.language)
            assertEquals(coverMock, data?.cover)
            assertEquals(iconBorderMock, data?.iconBorder)
            assertEquals(socialConfigTabMock, data?.socialConfigureTab)
            assertEquals(socialSettingMock, data?.socialSettings)
        }
    }
}
