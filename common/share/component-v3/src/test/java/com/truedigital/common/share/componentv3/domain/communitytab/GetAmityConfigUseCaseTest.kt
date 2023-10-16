package com.truedigital.common.share.componentv3.domain.communitytab

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.GetAmityConfigUseCase
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.GetAmityConfigUseCaseImpl
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetAmityConfigUseCaseTest {

    private lateinit var getAmityConfigUseCase: GetAmityConfigUseCase
    private val initialAppConfigRepository: InitialAppConfigRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()

    companion object {
        const val AMITY_SERVICE_KEY = "amity_service"
    }

    @BeforeEach
    fun setUp() {
        getAmityConfigUseCase = GetAmityConfigUseCaseImpl(
            initialAppConfigRepository = initialAppConfigRepository,
            localizationRepository = localizationRepository
        )
    }

    @Test
    fun `getAmityService Success`() = runTest {

        val enableNode = mapOf(
            "android" to true
        )
        val response = mapOf(
            "enable" to enableNode
        )

        whenever(initialAppConfigRepository.getConfigByKey(AMITY_SERVICE_KEY, "en")).thenReturn(
            response
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn("en")
        val result = getAmityConfigUseCase.execute()
        verify(initialAppConfigRepository, times(1)).getConfigByKey(AMITY_SERVICE_KEY, "en")
        assertEquals(true, result)
    }

    @Test
    fun `getAmityService has null`() = runTest {
        whenever(initialAppConfigRepository.getConfigByKey(AMITY_SERVICE_KEY, "en")).thenReturn(
            null
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn("en")
        val result = getAmityConfigUseCase.execute()
        verify(initialAppConfigRepository, times(1)).getConfigByKey(AMITY_SERVICE_KEY, "en")
        assertEquals(false, result)
    }
}
