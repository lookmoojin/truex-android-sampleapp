package com.truedigital.common.share.datalegacy.wrapper

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.provider.ContextDataProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ContextDataProviderWrapperTest {

    private lateinit var contextDataProvider: ContextDataProvider
    private val localizationRepository: LocalizationRepository = mockk()
    private lateinit var contextDataProviderWrapper: ContextDataProviderWrapperImpl

    private val appLanguageCode = "en"

    @BeforeEach
    fun setUp() {
        contextDataProvider = mockk(relaxed = true)

        every { localizationRepository.getAppLanguageCode() } returns appLanguageCode

        contextDataProviderWrapper =
            ContextDataProviderWrapperImpl(contextDataProvider, localizationRepository)
    }

    @Test
    fun `test get context data provider`() {
        // when
        val result = contextDataProviderWrapper.get()

        // then
        verify(exactly = 1) { contextDataProvider.updateContextLocale(appLanguageCode) }
        assertEquals(contextDataProvider, result)
    }
}
