package com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.datalegacy.data.base.Success
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting
import com.truedigital.common.share.datalegacy.data.repository.profile.ProfileRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.CoverData
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.ProfileData
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetProfileUseCaseImplTest {
    private lateinit var getProfileUseCase: GetProfileUseCase
    private val profileRepository: ProfileRepository = mock()

    @BeforeEach
    fun setUp() {
        getProfileUseCase = GetProfileUseCaseImpl(profileRepository)
    }

    @Test
    fun `test execute get profile success`() = runTest {
        val ssoIdMock = "ssoId"
        val avatarUrlMock = "avatarUrl"
        val displayNameMock = "displayName"
        val userNameMock = "userName"
        val bioMock = "bio"
        val languageMock = "language"
        val coverMock = CoverData()
        val profileIdMock = "profileId"
        val iconBorderMock = "iconBorder"
        val response = Success(
            ProfileData().apply {
                ssoid = ssoIdMock
                avatar = avatarUrlMock
                displayName = displayNameMock
                userName = userNameMock
                bio = bioMock
                settings = Setting().apply { language = languageMock }
                cover = coverMock
                id = profileIdMock
                iconBorder = iconBorderMock
            }
        )

        whenever(profileRepository.getProfile()).thenReturn(flowOf(response))
        getProfileUseCase.execute().collect { result ->
            assert(result is Success)
            val data = (result as? Success)?.data
            assertEquals(ssoIdMock, data?.ssoId)
            assertEquals(avatarUrlMock, data?.avatarUrl)
            assertEquals(displayNameMock, data?.displayName)
            assertEquals(userNameMock, data?.userName)
            assertEquals(bioMock, data?.bio)
            assertEquals(languageMock, data?.language)
            assertEquals(coverMock, data?.cover)
            assertEquals(profileIdMock, data?.profileId)
            assertEquals(iconBorderMock, data?.iconBorder)
        }
    }
}
