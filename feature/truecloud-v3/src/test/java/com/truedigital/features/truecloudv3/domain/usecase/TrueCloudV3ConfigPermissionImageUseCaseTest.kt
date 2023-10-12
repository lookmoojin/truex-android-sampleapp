package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal interface TrueCloudV3ConfigPermissionImageUseCaseTestCase {
    fun `test execute() case success`()
    fun `test execute() case node config is null`()
    fun `test execute() case node button and image is not string`()
}

internal class TrueCloudV3ConfigPermissionImageUseCaseTest :
    TrueCloudV3ConfigPermissionImageUseCaseTestCase {

    private lateinit var useCase: TrueCloudV3ConfigPermissionImageUseCase

    private val initialAppConfigRepository: InitialAppConfigRepository = mockk()
    private val localizationRepository: LocalizationRepository = mockk()

    @BeforeEach
    fun setUp() {
        useCase = TrueCloudV3ConfigPermissionImageUseCaseImpl(
            initialAppConfigRepository,
            localizationRepository
        )
    }

    @Test
    override fun `test execute() case success`() = runTest {
        // arrange
        val mockObject = mapOf(
            "button" to "red-button",
            "image_storage" to "https://www.mock.com/permission-image-url.jpg"
        )
        val mockLanguage = mapOf("en" to mockObject)
        val mockConfig = mapOf("android" to mockLanguage)

        every {
            localizationRepository.getAppCountryCode()
        } returns LocalizationRepository.Localization.TH.countryCode
        every {
            localizationRepository.getAppLanguageCode()
        } returns LocalizationRepository.Localization.EN.languageCode
        coEvery {
            initialAppConfigRepository.getConfigByKey(any(), any())
        } coAnswers {
            mockConfig
        }

        // act
        val execute = useCase.execute(NodePermission.STORAGE)

        // assert
        execute.collectSafe {
            assertEquals("red-button", it.first)
            assertEquals("https://www.mock.com/permission-image-url.jpg", it.second)
        }
    }

    @Test
    override fun `test execute() case node config is null`() = runTest {
        // arrange
        every {
            localizationRepository.getAppCountryCode()
        } returns LocalizationRepository.Localization.TH.countryCode
        every {
            localizationRepository.getAppLanguageCode()
        } returns LocalizationRepository.Localization.EN.languageCode
        coEvery {
            initialAppConfigRepository.getConfigByKey(any(), any())
        } coAnswers {
            null
        }

        // act
        val execute = useCase.execute(NodePermission.STORAGE)

        // assert
        execute.collectSafe {
            assertEquals("", it.first)
            assertEquals("", it.second)
        }
    }

    @Test
    override fun `test execute() case node button and image is not string`() = runTest {
        // arrange
        val mockObject = mapOf(
            "button" to 1,
            "image_storage" to 2
        )
        val mockLanguage = mapOf("en" to mockObject)
        val mockConfig = mapOf("android" to mockLanguage)

        every {
            localizationRepository.getAppCountryCode()
        } returns LocalizationRepository.Localization.TH.countryCode
        every {
            localizationRepository.getAppLanguageCode()
        } returns LocalizationRepository.Localization.EN.languageCode
        coEvery {
            initialAppConfigRepository.getConfigByKey(any(), any())
        } coAnswers {
            mockConfig
        }

        // act
        val execute = useCase.execute(NodePermission.STORAGE)

        // assert
        execute.collectSafe {
            assertEquals("", it.first)
            assertEquals("", it.second)
        }
    }
}
