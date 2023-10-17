package com.truedigital.features.music.domain.ads.usecase

import com.truedigital.features.music.data.ads.repository.CacheMusicPlayerAdsRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

interface ActionPreviousNextUseCaseTestCase {
    fun execute_isPreviousTrue_positionNull_doNothing()
    fun execute_isPreviousTrue_positionMoreThanSkip_doNothing()
    fun execute_isPreviousTrue_positionLessThanSkip_verifyAction()
    fun execute_isPreviousFalse_verifyAction()
}

class ActionPagingNextUseCaseTest : ActionPreviousNextUseCaseTestCase {

    lateinit var useCase: ActionPreviousNextUseCase

    @MockK
    lateinit var cacheMusicPlayerAdsRepository: CacheMusicPlayerAdsRepository

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        useCase = ActionPreviousNextUseCaseImpl(cacheMusicPlayerAdsRepository)
    }

    @Test
    override fun execute_isPreviousTrue_positionNull_doNothing() {
        useCase.execute(true)

        verify(exactly = 0) { cacheMusicPlayerAdsRepository.action() }
    }

    @Test
    override fun execute_isPreviousTrue_positionMoreThanSkip_doNothing() {
        useCase.execute(true, 6000L)

        verify(exactly = 0) { cacheMusicPlayerAdsRepository.action() }
    }

    @Test
    override fun execute_isPreviousTrue_positionLessThanSkip_verifyAction() {
        every { cacheMusicPlayerAdsRepository.action() } returns Unit

        useCase.execute(true, 1000L)

        verify { cacheMusicPlayerAdsRepository.action() }
    }

    @Test
    override fun execute_isPreviousFalse_verifyAction() {
        every { cacheMusicPlayerAdsRepository.action() } returns Unit

        useCase.execute(false)

        verify { cacheMusicPlayerAdsRepository.action() }
    }
}
