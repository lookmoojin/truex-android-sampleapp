package com.truedigital.common.share.data.coredata.domain

import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

internal class GetEnablePrivilegeUseCaseTest {
    companion object {
        const val NODE_NAME_PRIVILEGE = "privilege"
        const val NODE_NAME_ENABLE = "enable"
        const val NODE_NAME_ANDROID = "android"
    }

    private lateinit var getEnablePrivilegeUseCase: GetEnablePrivilegeUseCase
    private val initialAppConfigRepository: InitialAppConfigRepository =
        Mockito.mock(InitialAppConfigRepository::class.java)
    private val localizationRepository: LocalizationRepository =
        Mockito.mock(LocalizationRepository::class.java)

    @BeforeEach
    fun setUp() {
        getEnablePrivilegeUseCase = GetEnablePrivilegeUseCaseImpl(
            initialAppConfigRepository,
            localizationRepository
        )
    }

    @Test
    fun `test execute getConfigByKey null return true`() = runTest {

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")

        whenever(initialAppConfigRepository.getConfigByKey(NODE_NAME_PRIVILEGE, "th"))
            .thenReturn(null)

        assertTrue(getEnablePrivilegeUseCase.execute())
    }

    @Test
    fun `test config enable node null return true`() = runTest {

        val mapResponse = mapOf(NODE_NAME_ENABLE to null)

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")

        whenever(initialAppConfigRepository.getConfigByKey(NODE_NAME_PRIVILEGE, "th"))
            .thenReturn(mapResponse)

        assertTrue(getEnablePrivilegeUseCase.execute())
    }

    @Test
    fun `test config android node null return true`() = runTest {

        val mapAndroidResponse = mapOf(NODE_NAME_ANDROID to null)

        val mapResponse = mapOf(NODE_NAME_ENABLE to mapAndroidResponse)

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")

        whenever(initialAppConfigRepository.getConfigByKey(NODE_NAME_PRIVILEGE, "th"))
            .thenReturn(mapResponse)

        assertTrue(getEnablePrivilegeUseCase.execute())
    }

    @Test
    fun `test config android node string return true`() = runTest {

        val mapAndroidResponse = mapOf(NODE_NAME_ANDROID to "true")

        val mapResponse = mapOf(NODE_NAME_ENABLE to mapAndroidResponse)

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")

        whenever(initialAppConfigRepository.getConfigByKey(NODE_NAME_PRIVILEGE, "th"))
            .thenReturn(mapResponse)

        assertTrue(getEnablePrivilegeUseCase.execute())
    }

    @Test
    fun `test config android node true return true`() = runTest {

        val mapAndroidResponse = mapOf(NODE_NAME_ANDROID to true)

        val mapResponse = mapOf(NODE_NAME_ENABLE to mapAndroidResponse)

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")

        whenever(initialAppConfigRepository.getConfigByKey(NODE_NAME_PRIVILEGE, "th"))
            .thenReturn(mapResponse)

        assertTrue(getEnablePrivilegeUseCase.execute())
    }

    @Test
    fun `test config android node false return false`() = runTest {

        val mapAndroidResponse = mapOf(NODE_NAME_ANDROID to false)

        val mapResponse = mapOf(NODE_NAME_ENABLE to mapAndroidResponse)

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")

        whenever(initialAppConfigRepository.getConfigByKey(NODE_NAME_PRIVILEGE, "th"))
            .thenReturn(mapResponse)

        assertFalse(getEnablePrivilegeUseCase.execute())
    }
}
