package com.truedigital.common.share.componentv3.widget.truepoint.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.componentv3.widget.truepoint.data.TruePointWidgetConfigRepository
import com.truedigital.common.share.componentv3.widget.truepoint.data.model.TruePointConfigModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class GetTruePointTitleUseCaseTest {
    private val truePointWidgetConfigRepository: TruePointWidgetConfigRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()
    lateinit var getTruePointTitleUseCase: GetTruePointTitleUseCase

    @BeforeEach
    fun setUp() {
        getTruePointTitleUseCase = GetTruePointTitleUseCaseImpl(
            truePointWidgetConfigRepository,
            localizationRepository
        )
    }

    @Test
    fun `test GetTruePointTitleUseCase getTruePointConfig == TH`(): Unit = runTest {
        val mockTitleTh = "aaaa"
        val mockTruePointConfig = TruePointConfigModel(
            titleEn = "",
            titleTh = mockTitleTh
        )
        whenever(localizationRepository.getAppLocalizationForEnTh()).thenReturn(
            LocalizationRepository.Localization.TH
        )
        whenever(truePointWidgetConfigRepository.getTruePointConfig(localizationRepository.getAppCountryCode())).thenReturn(
            mockTruePointConfig
        )
        val result = getTruePointTitleUseCase.execute()
        assertNotEquals(result, "")
        assertEquals(result, mockTitleTh)
    }
    @Test
    fun `test GetTruePointTitleUseCase getTruePointConfig == EN`(): Unit = runTest {
        val mockTitleEN = "aaaa"
        val mockTruePointConfig = TruePointConfigModel(
            titleEn = mockTitleEN,
            titleTh = ""
        )
        whenever(localizationRepository.getAppLocalizationForEnTh()).thenReturn(
            LocalizationRepository.Localization.EN
        )
        whenever(
            truePointWidgetConfigRepository.getTruePointConfig(
                localizationRepository.getAppCountryCode()
            )
        ).thenReturn(
            mockTruePointConfig
        )
        val result = getTruePointTitleUseCase.execute()

        assertEquals(result, mockTitleEN)
        assertNotEquals(result, "")
    }
}
