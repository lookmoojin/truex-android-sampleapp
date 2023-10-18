package com.truedigital.features.truecloudv3.domain.usecase

import com.nhaarman.mockitokotlin2.any
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage.ERROR_GET_FIREBASE_CONFIG
import com.truedigital.features.truecloudv3.data.model.IntroLanguageModel
import com.truedigital.features.truecloudv3.data.repository.ConfigIntroImageRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal interface ConfigIntroImageUseCaseTestCase {
    fun `test execute case success`()
    fun `test execute case failed`()
}

internal class ConfigIntroImageUseCaseTest : ConfigIntroImageUseCaseTestCase {

    private lateinit var configIntroImageUseCase: ConfigIntroImageUseCase
    private val configIntroImageRepository: ConfigIntroImageRepository = mockk()
    private val localizationRepository: LocalizationRepository = mockk()

    @BeforeEach
    fun setUp() {
        configIntroImageUseCase = ConfigIntroImageUseCaseImpl(
            configIntroImageRepository,
            localizationRepository
        )
    }

    @Test
    override fun `test execute case success`() = runTest {
        // arrange
        val mockIsTablet = false
        val mockModel = IntroLanguageModel(
            en = "https://www.mock-url.com/mobile/image-en",
            my = "https://www.mock-url.com/mobile/image-en",
            th = "https://www.mock-url.com/mobile/image-th"
        )
        every {
            localizationRepository.getAppCountryCode()
        } returns LocalizationRepository.Localization.TH.countryCode

        every {
            localizationRepository.localize(any())
        } returns mockModel.th

        coEvery {
            configIntroImageRepository.getConfig(any(), any())
        } returns mockModel

        // act
        val execute = configIntroImageUseCase.execute(mockIsTablet)

        // assert
        execute.collectSafe { imageUrl ->
            assertEquals("https://www.mock-url.com/mobile/image-th", imageUrl)
        }
    }

    @Test
    override fun `test execute case failed`() = runTest {
        // arrange
        every {
            localizationRepository.getAppCountryCode()
        } returns LocalizationRepository.Localization.TH.countryCode
        coEvery {
            configIntroImageRepository.getConfig(any(), any())
        } returns null

        // act
        val execute = configIntroImageUseCase.execute(any())

        // assert
        execute.catch { exception ->
            assertEquals(ERROR_GET_FIREBASE_CONFIG, exception.message)
        }.collect {}
    }
}
