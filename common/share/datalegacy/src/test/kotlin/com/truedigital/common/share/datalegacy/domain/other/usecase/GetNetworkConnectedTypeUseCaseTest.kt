package com.truedigital.common.share.datalegacy.domain.other.usecase

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class GetNetworkConnectedTypeUseCaseTest {

    private val context: Context = mock()
    private val connectivityManager: ConnectivityManager = mock()

    private lateinit var getNetworkConnectedTypeUseCase: GetNetworkConnectedTypeUseCase

    @BeforeEach
    fun setUp() {
        whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(
            connectivityManager
        )
        getNetworkConnectedTypeUseCase = GetNetworkConnectedTypeUseCaseImpl(context)
    }

    /**
     * when sdk version M(23) or later
     */

    @Test
    @Disabled
    fun `test GetNetworkConnectedTypeUseCase with Cellular when sdk version M(23) or later`() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 23)
        // Mock the active network as Cellular
        val networkCapabilities = mock<NetworkCapabilities>()
        whenever(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(
            true
        )
        whenever(connectivityManager.getNetworkCapabilities(any())).thenReturn(networkCapabilities)

        // Execute the function and assert the result
        val result = getNetworkConnectedTypeUseCase.execute()
        assertEquals(GetNetworkConnectedTypeUseCaseImpl.CELLULAR, result)
    }

    @Test
    @Disabled
    fun `test GetNetworkConnectedTypeUseCase with WiFi when sdk version M(23) or later`() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 23)
        // Mock the active network as WiFi
        val networkCapabilities = mock<NetworkCapabilities>()
        whenever(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(
            true
        )
        whenever(connectivityManager.getNetworkCapabilities(any())).thenReturn(networkCapabilities)

        // Execute the function and assert the result
        val result = getNetworkConnectedTypeUseCase.execute()
        assertEquals(GetNetworkConnectedTypeUseCaseImpl.WIFI, result)
    }

    @Test
    @Disabled
    fun `test GetNetworkConnectedTypeUseCase with no active network connection when sdk version M(23) or later`() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 23)
        // Mock the active network as null
        whenever(connectivityManager.activeNetwork).thenReturn(null)

        // Execute the function and assert the result
        val result = getNetworkConnectedTypeUseCase.execute()
        assertEquals(GetNetworkConnectedTypeUseCaseImpl.NETWORK_ERROR, result)
    }

    @Test
    @Disabled
    fun `test GetNetworkConnectedTypeUseCase with other network when sdk version M(23) or later`() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 23)
        // Mock the active network as other network(VPN)
        val networkCapabilities = mock<NetworkCapabilities>()
        whenever(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)).thenReturn(
            true
        )
        whenever(connectivityManager.getNetworkCapabilities(any())).thenReturn(networkCapabilities)

        // Execute the function and assert the result
        val result = getNetworkConnectedTypeUseCase.execute()
        assertEquals(GetNetworkConnectedTypeUseCaseImpl.NETWORK_ERROR, result)
    }

    /**
     * When the SDK version is lower than M(23)
     */

    @Test
    @Disabled
    fun `test GetNetworkConnectedTypeUseCase with Cellular when sdk version lower than M(23)`() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 22)
        // Mock the active network as Cellular
        val networkInfo = mock<NetworkInfo>()
        whenever(networkInfo.type).thenReturn(ConnectivityManager.TYPE_MOBILE)
        whenever(connectivityManager.activeNetworkInfo).thenReturn(networkInfo)

        // Execute the function and assert the result
        val result = getNetworkConnectedTypeUseCase.execute()
        assertEquals(GetNetworkConnectedTypeUseCaseImpl.CELLULAR, result)
    }

    @Test
    @Disabled
    fun `test GetNetworkConnectedTypeUseCase with WiFi when sdk version lower than M(23)`() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 22)
        // Mock the active network as WiFi
        val networkInfo = mock<NetworkInfo>()
        whenever(networkInfo.type).thenReturn(ConnectivityManager.TYPE_WIFI)
        whenever(connectivityManager.activeNetworkInfo).thenReturn(networkInfo)

        // Execute the function and assert the result
        val result = getNetworkConnectedTypeUseCase.execute()
        assertEquals(GetNetworkConnectedTypeUseCaseImpl.WIFI, result)
    }

    @Test
    @Disabled
    fun `test GetNetworkConnectedTypeUseCase with no active network connection when sdk version lower than M(23)`() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 22)
        // Mock the active network as null
        whenever(connectivityManager.activeNetwork).thenReturn(null)

        // Execute the function and assert the result
        val result = getNetworkConnectedTypeUseCase.execute()
        assertEquals(GetNetworkConnectedTypeUseCaseImpl.NETWORK_ERROR, result)
    }

    @Test
    @Disabled
    fun `test GetNetworkConnectedTypeUseCase with other network when sdk version lower than M(23)`() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 22)
        // Mock the active network as other network(VPN)
        val networkInfo = mock<NetworkInfo>()
        whenever(networkInfo.type).thenReturn(ConnectivityManager.TYPE_VPN)
        whenever(connectivityManager.activeNetworkInfo).thenReturn(networkInfo)

        // Execute the function and assert the result
        val result = getNetworkConnectedTypeUseCase.execute()
        assertEquals(GetNetworkConnectedTypeUseCaseImpl.NETWORK_ERROR, result)
    }

    @Throws(Exception::class)
    private fun setFinalStatic(field: Field, newValue: Any?) {
        field.isAccessible = true
        val modifiersField: Field = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
        field.set(null, newValue)
    }
}
