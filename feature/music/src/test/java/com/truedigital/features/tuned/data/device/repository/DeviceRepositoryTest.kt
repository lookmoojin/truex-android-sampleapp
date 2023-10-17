package com.truedigital.features.tuned.data.device.repository

import android.content.Context
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.utils.networkconnection.ConnectivityStateHolder
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.put
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class DeviceRepositoryTest {
    private lateinit var deviceRepository: DeviceRepository
    private val context: Context = mock()
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface = mock()

    private val mockUniqueId = "uniqueId"
    private val mockToken = "token"
    private fun mockInitSharedPrefValue() {
        whenever(sharedPreferences.get<String>(DeviceRepositoryImpl.UNIQUE_ID_KEY)).doReturn(
            mockUniqueId
        )
        whenever(sharedPreferences.get<String>(DeviceRepositoryImpl.TOKEN_KEY)).doReturn(mockToken)

        deviceRepository = DeviceRepositoryImpl(context, sharedPreferences)
    }

    private fun mockInitSharedPrefNull() {
        whenever(sharedPreferences.get<String>(DeviceRepositoryImpl.UNIQUE_ID_KEY)).doReturn(null)
        whenever(sharedPreferences.get<String>(DeviceRepositoryImpl.TOKEN_KEY)).doReturn(null)
        mockkStatic(UUID::class)
        every { UUID.randomUUID().toString() } returns "key"

        deviceRepository = DeviceRepositoryImpl(context, sharedPreferences)
    }

    @Test
    fun testGetUniqueId_returnUniqueId() {
        mockInitSharedPrefValue()
        assertEquals(mockUniqueId, deviceRepository.getUniqueId())
    }

    @Test
    fun testGetToken_returnToken() {
        mockInitSharedPrefValue()
        assertEquals(mockToken, deviceRepository.getToken())
    }

    @Test
    fun testIsNetworkConnected_isConnectedTrue_returnTrue() {
        mockInitSharedPrefValue()
        mockkObject(ConnectivityStateHolder)
        every { ConnectivityStateHolder.isConnected } returns true

        assertEquals(true, deviceRepository.isNetworkConnected())
    }

    @Test
    fun testIsWifiConnected_notFoundWifi_returnFalse() {
        mockInitSharedPrefValue()
        mockkObject(ConnectivityStateHolder)
        every { ConnectivityStateHolder.networkStats } returns listOf()

        assertEquals(false, deviceRepository.isWifiConnected())
    }

    @Test
    fun testGetLikesCount_returnLikeCount() {
        mockInitSharedPrefNull()
        val mockLikeCount = 10
        val result = 11
        whenever(sharedPreferences.get(DeviceRepositoryImpl.LIKES_COUNT_KEY, 0)).doReturn(
            mockLikeCount
        )

        assertEquals(result, deviceRepository.getLikesCount())
        verify(sharedPreferences, times(1)).put(DeviceRepositoryImpl.LIKES_COUNT_KEY, result)
    }

    @Test
    fun testIsArtistHintShown_returnSharedPrefValue() {
        mockInitSharedPrefNull()

        whenever(
            sharedPreferences.get(
                DeviceRepositoryImpl.ARTIST_HINT_STATUS_KEY,
                false
            )
        ).doReturn(true)

        assertEquals(true, deviceRepository.isArtistHintShown())
    }

    @Test
    fun testSetArtistHintStatus_verifySharedPref() {
        mockInitSharedPrefNull()

        deviceRepository.setArtistHintStatus(true)

        verify(sharedPreferences, times(1)).put(DeviceRepositoryImpl.ARTIST_HINT_STATUS_KEY, true)
    }
}
