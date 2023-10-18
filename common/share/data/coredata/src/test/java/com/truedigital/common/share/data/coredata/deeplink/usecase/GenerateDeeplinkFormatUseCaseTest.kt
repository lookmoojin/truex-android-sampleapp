package com.truedigital.common.share.data.coredata.deeplink.usecase

import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class GenerateDeeplinkFormatUseCaseTest {

    private lateinit var generateDeeplinkFormatUseCase: GenerateDeeplinkFormatUseCase

    @BeforeEach
    fun setUp() {
        generateDeeplinkFormatUseCase = GenerateDeeplinkFormatUseCaseImpl()
    }

    @Test
    fun `test gen deeplink movie detail`() {
        val aspectResult = "https://movie.trueid.net/movie/acbdefg"
        val cmsID = "acbdefg"
        val map = mapOf(
            DeeplinkConstants.DeeplinkKey.CMS_ID to cmsID
        )
        val deeplinkUrl = generateDeeplinkFormatUseCase.execute(DeeplinkConstants.DeeplinkContentType.WATCH_DETAIL, map)
        assertNotNull(deeplinkUrl)
        assertEquals(deeplinkUrl, aspectResult)
    }

    @Test
    fun `test gen deeplink series detail`() {
        val aspectResult = "https://movie.trueid.net/series/acbdefg"
        val cmsID = "acbdefg"
        val map = mapOf(
            DeeplinkConstants.DeeplinkKey.CMS_ID to cmsID
        )
        val deeplinkUrl = generateDeeplinkFormatUseCase.execute(DeeplinkConstants.DeeplinkContentType.WATCH_SERIES, map)
        assertNotNull(deeplinkUrl)
        assertEquals(deeplinkUrl, aspectResult)
    }

    @Test
    fun `test gen deeplink match info`() {
        val aspectResult = "https://trueid.net/sport/match/acbdefg"
        val matchID = "acbdefg"
        val map = mapOf(
            DeeplinkConstants.DeeplinkKey.MATCH_ID to matchID,
        )
        val deeplinkUrl = generateDeeplinkFormatUseCase.execute(DeeplinkConstants.DeeplinkContentType.SPORT_MATCH, map)
        assertNotNull(deeplinkUrl)
        assertEquals(deeplinkUrl, aspectResult)
    }
}
