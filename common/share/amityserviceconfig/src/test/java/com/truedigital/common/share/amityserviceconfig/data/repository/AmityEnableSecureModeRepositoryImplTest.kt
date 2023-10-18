package com.truedigital.common.share.amityserviceconfig.data.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.amityserviceconfig.domain.repository.AmityEnableSecureModeRepository
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AmityEnableSecureModeRepositoryImplTest {

    private val initialAppConfigRepository: InitialAppConfigRepository = mock()
    private lateinit var amityEnableSecureModeRepository: AmityEnableSecureModeRepository

    @BeforeEach
    fun setUp() {
        amityEnableSecureModeRepository = AmityEnableSecureModeRepositoryImpl(initialAppConfigRepository)
    }

    @Test
    fun `test get firestore AmityEnableSecureMode set android true`() {
        val childMap = mapOf(
            "android" to true,
            "ios" to true,
        )
        val response = mapOf(
            "enable_secure_mode" to childMap
        )

        doReturn(response).whenever(initialAppConfigRepository).getConfigByKey("amity_service")

        val result = amityEnableSecureModeRepository.isAmityEnableSecureMode()

        verify(initialAppConfigRepository, times(1)).getConfigByKey("amity_service")
        assert(result)
    }

    @Test
    fun `test get firestore AmityEnableSecureMode set android false`() {
        val childMap = mapOf(
            "android" to false,
            "ios" to true,
        )
        val response = mapOf(
            "enable_secure_mode" to childMap
        )

        doReturn(response).whenever(initialAppConfigRepository).getConfigByKey("amity_service")
        whenever(initialAppConfigRepository.getConfigByKey("amity_service")).thenReturn(null)

        val result = amityEnableSecureModeRepository.isAmityEnableSecureMode()
        verify(initialAppConfigRepository, times(1)).getConfigByKey("amity_service")

        assert(!result)
        assertEquals(result, false)
    }

    @Test
    fun `test check AmityEnableSecureModeEnableByCountry set android true`() = runTest {
        val childMap = mapOf(
            "android" to true,
            "ios" to true,
        )
        val response = mapOf(
            "enable_secure_mode" to childMap
        )

        doReturn(response).whenever(initialAppConfigRepository)
            .getConfigByKey("amity_service", "th")

        val result = amityEnableSecureModeRepository.isAmityEnableSecureModeByCountry(
            countryCode = "th"
        )

        verify(initialAppConfigRepository, times(1)).getConfigByKey("amity_service", "th")
        assert(result)
    }

    @Test
    fun `test AmityEnableSecureModeEnableByCountry case config null`() = runTest {
        whenever(initialAppConfigRepository.getConfigByKey("amity_service")).thenReturn(null)

        val result = amityEnableSecureModeRepository.isAmityEnableSecureModeByCountry(
            countryCode = "th"
        )
        verify(initialAppConfigRepository, times(1)).getConfigByKey("amity_service", "th")

        assertEquals(result, false)
    }

    @Test
    fun `test enable null return false`() = runTest {
        val mapResponse = mapOf("enable_secure_mode" to null)

        doReturn(mapResponse).whenever(initialAppConfigRepository)
            .getConfigByKey("amity_service")

        whenever(initialAppConfigRepository.getConfigByKey(any(), any())).thenReturn(mapResponse)

        assertFalse(amityEnableSecureModeRepository.isAmityEnableSecureMode())
    }
}
