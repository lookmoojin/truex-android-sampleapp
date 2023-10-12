package com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails

import com.truedigital.common.share.datalegacy.data.base.Success
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting
import com.truedigital.common.share.datalegacy.data.repository.profile.ProfileRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.CoverData
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.ProfileData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GetNonCachedProfileUseCaseTest {

    private val profileRepository = mockk<ProfileRepository>()
    private val useCase = GetNonCachedProfileUseCaseImpl(profileRepository)

    @Test
    fun `execute should return profile model`() = runTest {
        // given
        val mockProfile = ProfileData().apply {
            ssoid = "1"
            avatar = "avatarUrl"
            displayName = "displayName"
            userName = "userName"
            bio = "bio"
            settings = Setting().apply {
                language = "en"
            }
            cover = CoverData().apply {
                small = "coverUrl"
            }
            iconBorder = "iconBorderUrl"
        }
        coEvery { profileRepository.getNonCachedProfile() } returns flowOf(Success(mockProfile))

        // when
        val result = useCase.execute().single()

        // then
        (result as Success).data.let {
            assertEquals(mockProfile.ssoid, it.ssoId)
            assertEquals(mockProfile.avatar, it.avatarUrl)
            assertEquals(mockProfile.displayName, it.displayName)
            assertEquals(mockProfile.userName, it.userName)
            assertEquals(mockProfile.bio, it.bio)
            assertEquals(mockProfile.settings?.language, it.language)
            assertEquals(mockProfile.cover?.small, it.cover?.small)
            assertEquals(mockProfile.iconBorder, it.iconBorder)
        }
    }
}
