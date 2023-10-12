package com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails

import com.truedigital.common.share.datalegacy.data.repository.profile.ProfileRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ClearProfileCacheUseCaseTest {

    private lateinit var profileRepository: ProfileRepository
    private lateinit var clearProfileCacheUseCaseImpl: ClearProfileCacheUseCaseImpl

    @BeforeEach
    fun setUp() {
        profileRepository = mockk()
        clearProfileCacheUseCaseImpl = ClearProfileCacheUseCaseImpl(profileRepository)
    }

    @Test
    fun `execute should call clearCache on profileRepository`() = runTest {
        every { profileRepository.clearCache() } just runs

        clearProfileCacheUseCaseImpl.execute()

        verify { profileRepository.clearCache() }
    }
}
