package com.truedigital.navigation.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.navigation.data.PersonaData
import com.truedigital.navigation.data.repository.GetInterContentRepository
import com.truedigital.navigation.domain.usecase.GetPersonaConfigUseCase
import com.truedigital.navigation.domain.usecase.GetPersonaConfigUseCaseImpl
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetPersonaConfigUseCaseTest {
    private lateinit var getPersonaConfigUseCase: GetPersonaConfigUseCase
    private val getInterContentRepository: GetInterContentRepository = mockk()
    private val localizationRepository: LocalizationRepository = mockk()

    @BeforeEach
    fun setup() {
        getPersonaConfigUseCase = GetPersonaConfigUseCaseImpl(
            getInterContentRepository = getInterContentRepository,
            localizationRepository = localizationRepository
        )
    }

    @Test
    fun testGetPersonaConfig_Data_Success_Should_return_Data() = runTest {
        every {
            localizationRepository.getAppCountryCode()
        } returns (
            LocalizationRepository.Localization.EN.countryCode
            )
        every {
            localizationRepository.getAppLanguageCode()
        } returns (
            LocalizationRepository.Localization.EN.languageCode
            )
        every {
            getInterContentRepository.getPersonaData(
                "EN", "en"
            )
        } returns (
            flowOf(PersonaData(url = "url", schemaId = "schemaId"))
            )
        getPersonaConfigUseCase.execute().collectSafe { personaData ->
            assertEquals("url", personaData.url)
            assertEquals("schemaId", personaData.schemaId)
        }
    }

    @Test
    fun testGetPersonaConfig_Data_Success_But_Empty_Should_return_EmptyData() = runTest {
        every {
            localizationRepository.getAppCountryCode()
        } returns (
            LocalizationRepository.Localization.EN.countryCode
            )
        every {
            localizationRepository.getAppLanguageCode()
        } returns (
            LocalizationRepository.Localization.EN.languageCode
            )
        every {
            getInterContentRepository.getPersonaData(
                "EN", "en"
            )
        } returns (
            flowOf(PersonaData(url = "", schemaId = ""))
            )
        getPersonaConfigUseCase.execute().collectSafe { personaData ->
            assertEquals("", personaData.url)
            assertEquals("", personaData.schemaId)
        }
    }

    @Test
    fun testGetPersonaConfig_Data_Success_But_Null_Should_return_EmptyData() = runTest {
        every {
            localizationRepository.getAppCountryCode()
        } returns (
            LocalizationRepository.Localization.EN.countryCode
            )
        every {
            localizationRepository.getAppLanguageCode()
        } returns (
            LocalizationRepository.Localization.EN.languageCode
            )
        every {
            getInterContentRepository.getPersonaData(
                "EN", "en"
            )
        } returns (
            flowOf(PersonaData(url = null, schemaId = null))
            )
        getPersonaConfigUseCase.execute().collectSafe { personaData ->
            assertEquals("", personaData.url)
            assertEquals("", personaData.schemaId)
        }
    }
}
