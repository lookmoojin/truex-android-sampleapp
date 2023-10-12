package com.truedigital.common.share.datalegacy.domain.webview.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.datalegacy.domain.webview.usecase.GetSystemWebViewMinimumVersionUseCaseImpl.Companion.DEFAULT_VERSION
import com.truedigital.common.share.datalegacy.domain.webview.usecase.GetSystemWebViewMinimumVersionUseCaseImpl.Companion.NODE_NAME_MIN_VERSION
import com.truedigital.common.share.datalegacy.domain.webview.usecase.GetSystemWebViewMinimumVersionUseCaseImpl.Companion.NODE_NAME_SYSTEM_WEBVIEW
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetSystemWebViewMinimumVersionUseCaseTest {

    private val initialAppConfigRepository: InitialAppConfigRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()
    private lateinit var getSystemWebViewMinimumVersionUseCase: GetSystemWebViewMinimumVersionUseCase

    @BeforeEach
    fun setUp() {
        getSystemWebViewMinimumVersionUseCase = GetSystemWebViewMinimumVersionUseCaseImpl(
            initialAppConfigRepository = initialAppConfigRepository,
            localizationRepository = localizationRepository
        )
    }

    @Test
    fun testGetSystemWebViewMinimumVersion_Success_ThenReturnStringVersion() = runTest {
        val version = "1.2.3.4"
        val response = mapOf(NODE_NAME_MIN_VERSION to version)

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(initialAppConfigRepository.getConfigByKey(NODE_NAME_SYSTEM_WEBVIEW, "th")).thenReturn(
            response
        )

        val result = getSystemWebViewMinimumVersionUseCase.execute()
        result.collect {
            assertEquals(version, it)
        }
    }

    @Test
    fun testGetSystemWebViewMinimumVersion_FailGetNode_ThenReturnDefaultStringVersion() = runTest {
        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(initialAppConfigRepository.getConfigByKey(NODE_NAME_SYSTEM_WEBVIEW, "th")).thenReturn(
            null
        )

        val result = getSystemWebViewMinimumVersionUseCase.execute()
        result.collect {
            assertEquals(DEFAULT_VERSION, it)
        }
    }

    @Test
    fun testGetSystemWebViewMinimumVersion_FailGetMinVersion_ThenReturnDefaultStringVersion() = runTest {
        val response = mapOf(NODE_NAME_MIN_VERSION to null)

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(initialAppConfigRepository.getConfigByKey(NODE_NAME_SYSTEM_WEBVIEW, "th")).thenReturn(
            response
        )

        val result = getSystemWebViewMinimumVersionUseCase.execute()
        result.collect {
            assertEquals(DEFAULT_VERSION, it)
        }
    }

    @Test
    fun testGetSystemWebViewMinimumVersion_FailMissingType_ThenReturnDefaultStringVersion() = runTest {
        val response = mapOf(NODE_NAME_MIN_VERSION to 0.0)

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(initialAppConfigRepository.getConfigByKey(NODE_NAME_SYSTEM_WEBVIEW, "th")).thenReturn(
            response
        )

        val result = getSystemWebViewMinimumVersionUseCase.execute()
        result.collect {
            assertEquals(DEFAULT_VERSION, it)
        }
    }
}
