package com.truedigital.common.share.amityserviceconfig.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.amityserviceconfig.data.repository.AmityConfigRepository
import com.truedigital.common.share.amityserviceconfig.data.repository.AmityConfigRepositoryImpl
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AmityConfigRepositoryImplTest {

    private val initialAppConfigRepository: InitialAppConfigRepository = mock()
    private lateinit var amityConfigRepository: AmityConfigRepository

    @BeforeEach
    fun setUp() {
        amityConfigRepository = AmityConfigRepositoryImpl(initialAppConfigRepository)
    }

    @Test
    fun `test get firestore set android true`() {
        val childMap = mapOf(
            "android" to true,
            "ios" to true,
        )
        val response = mapOf(
            "enable" to childMap
        )

        doReturn(response).whenever(initialAppConfigRepository).getConfigByKey("amity_service")

        val result = amityConfigRepository.isAmityConfigEnable()

        verify(initialAppConfigRepository, times(1)).getConfigByKey("amity_service")
        assert(result)
    }

    @Test
    fun `test get firestore set android false`() {
        val childMap = mapOf(
            "android" to false,
            "ios" to true,
        )
        val response = mapOf(
            "enable" to childMap
        )

        doReturn(response).whenever(initialAppConfigRepository).getConfigByKey("amity_service")
        whenever(initialAppConfigRepository.getConfigByKey("amity_service")).thenReturn(null)

        val result = amityConfigRepository.isAmityConfigEnable()
        verify(initialAppConfigRepository, times(1)).getConfigByKey("amity_service")

        assert(!result)
        assertEquals(result, false)
    }

    @Test
    fun `test check AmityConfigEnableByCountry set android true`() = runTest {
        val childMap = mapOf(
            "android" to true,
            "ios" to true,
        )
        val response = mapOf(
            "enable" to childMap
        )

        doReturn(response).whenever(initialAppConfigRepository)
            .getConfigByKey("amity_service", "th")

        val result = amityConfigRepository.isAmityConfigEnableByCountry(
            countryCode = "th"
        )

        verify(initialAppConfigRepository, times(1)).getConfigByKey("amity_service", "th")
        assert(result)
    }

    @Test
    fun `test AmityConfigEnableByCountry case config null`() = runTest {
        whenever(initialAppConfigRepository.getConfigByKey("amity_service")).thenReturn(null)

        val result = amityConfigRepository.isAmityConfigEnableByCountry(
            countryCode = "th"
        )
        verify(initialAppConfigRepository, times(1)).getConfigByKey("amity_service", "th")

        assertEquals(result, false)
    }

    @Test
    fun `test enable null return false`() = runTest {
        val mapResponse = mapOf("enable" to null)

        doReturn(mapResponse).whenever(initialAppConfigRepository)
            .getConfigByKey("amity_service")

        whenever(initialAppConfigRepository.getConfigByKey(any(), any())).thenReturn(mapResponse)

        assertFalse(amityConfigRepository.isAmityConfigEnable())
    }
}
