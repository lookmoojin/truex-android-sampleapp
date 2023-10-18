package com.truedigital.features.music.domain.ads.usecase

import com.truedigital.features.music.data.ads.repository.CacheMusicPlayerAdsRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ClearCacheMusicPlayerAdsUseCaseTest {

    lateinit var useCase: ClearCacheMusicPlayerAdsUseCase

    @MockK
    lateinit var cacheMusicPlayerAdsRepository: CacheMusicPlayerAdsRepository

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        useCase = ClearCacheMusicPlayerAdsUseCaseImpl(cacheMusicPlayerAdsRepository)
    }

    @Test
    fun execute_clearCache_verifyResetData() {
        every { cacheMusicPlayerAdsRepository.resetCountAds() } returns Unit
        every { cacheMusicPlayerAdsRepository.resetFirstTime() } returns Unit

        useCase.execute()

        verify { cacheMusicPlayerAdsRepository.resetCountAds() }
        verify { cacheMusicPlayerAdsRepository.resetFirstTime() }
    }
}
