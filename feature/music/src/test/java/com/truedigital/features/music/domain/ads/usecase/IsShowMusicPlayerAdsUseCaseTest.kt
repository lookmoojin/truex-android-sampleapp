package com.truedigital.features.music.domain.ads.usecase

import com.truedigital.features.music.data.ads.repository.CacheMusicPlayerAdsRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

interface IsShowMusicPlayerAdsUseCaseTestCase {
    fun execute_isFirstTimeTrue_returnTrue()
    fun execute_getActionTrue_countEquals_returnTrue()
    fun execute_getActionTrue_countNotEquals_returnFalse()
    fun execute_getActionFalse_returnFalse()
}

class IsShowMusicPlayerAdsUseCaseTest : IsShowMusicPlayerAdsUseCaseTestCase {

    lateinit var useCase: IsShowMusicPlayerAdsUseCase

    @MockK
    lateinit var cacheMusicPlayerAdsRepository: CacheMusicPlayerAdsRepository

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        useCase = IsShowMusicPlayerAdsUseCaseImpl(cacheMusicPlayerAdsRepository)
    }

    @Test
    override fun execute_isFirstTimeTrue_returnTrue() {
        every { cacheMusicPlayerAdsRepository.isFirstTime() } returns true
        every { cacheMusicPlayerAdsRepository.updateFirstTime() } returns Unit

        val result = useCase.execute()

        assertTrue { result }
        verify { cacheMusicPlayerAdsRepository.updateFirstTime() }
    }

    @Test
    override fun execute_getActionTrue_countEquals_returnTrue() {
        every { cacheMusicPlayerAdsRepository.isFirstTime() } returns false
        every { cacheMusicPlayerAdsRepository.getAction() } returns true
        every { cacheMusicPlayerAdsRepository.countAds() } returns Unit
        every { cacheMusicPlayerAdsRepository.getCountAds() } returns 3
        every { cacheMusicPlayerAdsRepository.resetAction() } returns Unit
        every { cacheMusicPlayerAdsRepository.resetCountAds() } returns Unit

        val result = useCase.execute()

        assertTrue { result }
        verify { cacheMusicPlayerAdsRepository.resetAction() }
        verify { cacheMusicPlayerAdsRepository.resetCountAds() }
    }

    @Test
    override fun execute_getActionTrue_countNotEquals_returnFalse() {
        every { cacheMusicPlayerAdsRepository.isFirstTime() } returns false
        every { cacheMusicPlayerAdsRepository.getAction() } returns true
        every { cacheMusicPlayerAdsRepository.countAds() } returns Unit
        every { cacheMusicPlayerAdsRepository.getCountAds() } returns 2
        every { cacheMusicPlayerAdsRepository.resetAction() } returns Unit

        val result = useCase.execute()

        assertFalse { result }
        verify { cacheMusicPlayerAdsRepository.resetAction() }
        verify(exactly = 0) { cacheMusicPlayerAdsRepository.resetCountAds() }
    }

    @Test
    override fun execute_getActionFalse_returnFalse() {
        every { cacheMusicPlayerAdsRepository.isFirstTime() } returns false
        every { cacheMusicPlayerAdsRepository.getAction() } returns false

        val result = useCase.execute()

        assertFalse { result }
    }
}
