package com.truedigital.common.share.data.usecase

import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants
import com.truedigital.common.share.data.coredata.deeplink.usecase.GenerateDeeplinkFormatUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsInternalDeeplinkUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.ValidateDeeplinkUrlUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.ValidateDeeplinkUrlUseCaseImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface ValidateDeeplinkUrlUseCaseTestCase {
    fun `Given empty url when execute then should return empty string`()
    fun `Given internal deeplink url when execute then should return the given url`()
    fun `Given external web url when execute then should generate in-app browser url`()
}

class ValidateDeeplinkUrlUseCaseTest : ValidateDeeplinkUrlUseCaseTestCase {
    private val generateDeeplinkFormatUseCase: GenerateDeeplinkFormatUseCase = mockk()
    private val isInternalDeeplinkUseCase: IsInternalDeeplinkUseCase = mockk()

    private lateinit var validateDeeplinkUrlUseCase: ValidateDeeplinkUrlUseCase

    @BeforeEach
    fun setUp() {
        validateDeeplinkUrlUseCase = ValidateDeeplinkUrlUseCaseImpl(
            generateDeeplinkFormatUseCase,
            isInternalDeeplinkUseCase
        )
    }

    @Test
    override fun `Given empty url when execute then should return empty string`() {
        val url = validateDeeplinkUrlUseCase.execute("")

        assertTrue(url.isEmpty())
    }

    @Test
    override fun `Given internal deeplink url when execute then should return the given url`() {
        val mockOneLinkUrl = "https://ttid.co/UAnK/pij36yd3"
        every { isInternalDeeplinkUseCase.execute(mockOneLinkUrl) } returns true

        val url = validateDeeplinkUrlUseCase.execute(mockOneLinkUrl)

        assertEquals(mockOneLinkUrl, url)
    }

    @Test
    override fun `Given external web url when execute then should generate in-app browser url`() {
        val mockWebUrl = "https://www.google.com"
        val expectUrl = "https://home.trueid.net/inapp-browser?website=$mockWebUrl"
        every { isInternalDeeplinkUseCase.execute(mockWebUrl) } returns false
        every { generateDeeplinkFormatUseCase.execute(any(), any()) } returns expectUrl

        validateDeeplinkUrlUseCase.execute(mockWebUrl)

        val deeplinkContentType = slot<DeeplinkConstants.DeeplinkContentType>()
        verify(exactly = 1) {
            generateDeeplinkFormatUseCase.execute(
                capture(deeplinkContentType),
                any()
            )
        }
        assertEquals(
            DeeplinkConstants.DeeplinkContentType.INAPP_BROWSER,
            deeplinkContentType.captured
        )
    }
}
