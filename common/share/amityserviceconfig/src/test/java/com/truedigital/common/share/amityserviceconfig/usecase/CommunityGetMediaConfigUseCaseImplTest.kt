package com.truedigital.common.share.amityserviceconfig.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.amityserviceconfig.domain.repository.MediaConfigRepository
import com.truedigital.common.share.amityserviceconfig.domain.usecase.CommunityGetMediaConfigUseCase
import com.truedigital.common.share.amityserviceconfig.domain.usecase.CommunityGetMediaConfigUseCaseImpl
import com.truedigital.core.data.device.repository.LocalizationRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommunityGetMediaConfigUseCaseImplTest {
    private val mediaConfigRepository: MediaConfigRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()

    private lateinit var communityGetMediaConfigUseCase: CommunityGetMediaConfigUseCase

    @BeforeEach
    fun setUp() {
        communityGetMediaConfigUseCase = CommunityGetMediaConfigUseCaseImpl(
            mediaConfigRepository = mediaConfigRepository,
            localizationRepository = localizationRepository
        )
    }

    @Test
    fun testGetMediaConfig_whenAppCountryCodeIsTH_returnTrue() = runTest {
        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(mediaConfigRepository.getFeatureConfigMediaConfig("th"))
            .thenReturn(
                flowOf(true)
            )

        communityGetMediaConfigUseCase.execute().collect {
            assert(it)
        }
    }
}
