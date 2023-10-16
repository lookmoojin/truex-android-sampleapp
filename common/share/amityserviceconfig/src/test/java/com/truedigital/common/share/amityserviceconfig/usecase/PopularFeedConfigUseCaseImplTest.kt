package com.truedigital.common.share.amityserviceconfig.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.amityserviceconfig.domain.repository.PopularFeedConfigRepository
import com.truedigital.common.share.amityserviceconfig.domain.usecase.PopularFeedConfigUseCase
import com.truedigital.common.share.amityserviceconfig.domain.usecase.PopularFeedConfigUseCaseImpl
import com.truedigital.core.data.device.repository.LocalizationRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PopularFeedConfigUseCaseImplTest {
    private val popularFeedConfigRepository: PopularFeedConfigRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()

    private lateinit var popularFeedConfigUseCase: PopularFeedConfigUseCase

    @BeforeEach
    fun setUp() {
        popularFeedConfigUseCase = PopularFeedConfigUseCaseImpl(
            popularFeedConfigRepository = popularFeedConfigRepository,
            localizationRepository = localizationRepository
        )
    }

    @Test
    fun `test execute getFeatureConfig is True`() = runTest {
        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(popularFeedConfigRepository.getFeatureConfigPopularFeed("th"))
            .thenReturn(
                flowOf(true)
            )

        popularFeedConfigUseCase.execute().collect {
            assert(it)
        }
    }
}
