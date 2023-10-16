package com.truedigital.common.share.amityserviceconfig.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.amityserviceconfig.domain.repository.CommunityGetRegexRepository
import com.truedigital.common.share.amityserviceconfig.domain.usecase.CommunityGetRegexUseCase
import com.truedigital.common.share.amityserviceconfig.domain.usecase.CommunityGetRegexUseCaseImpl
import com.truedigital.core.data.device.repository.LocalizationRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommunityGetRegexUseCaseImplTest {
    private val communityGetRegexRepository: CommunityGetRegexRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()

    private lateinit var communityGetRegexUseCase: CommunityGetRegexUseCase

    @BeforeEach
    fun setUp() {
        communityGetRegexUseCase = CommunityGetRegexUseCaseImpl(
            communityGetRegexRepository = communityGetRegexRepository,
            localizationRepository = localizationRepository
        )
    }

    @Test
    fun `test execute getFeatureConfig is True`() = runTest {

        val mockExpectedResponse = mapOf(
            "android" to true,
            "ios" to true,
            "community_feature" to arrayOf(
                "livestream",
                "comment",
                "post"
            )
        )

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(communityGetRegexRepository.getFeatureConfigRegex("th"))
            .thenReturn(
                flowOf(mockExpectedResponse.toString())
            )

        communityGetRegexUseCase.execute().collect {
            assertEquals(mockExpectedResponse.toString(), it)
        }
    }
}
