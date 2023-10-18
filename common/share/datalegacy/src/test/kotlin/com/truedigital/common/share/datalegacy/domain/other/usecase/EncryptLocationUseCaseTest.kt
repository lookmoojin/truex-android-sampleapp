package com.truedigital.common.share.datalegacy.domain.other.usecase

import com.truedigital.core.manager.location.LocationManager
import com.truedigital.core.utils.EncryptUtil
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.security.InvalidKeyException

internal class EncryptLocationUseCaseTest {

    private lateinit var locationManager: LocationManager
    private lateinit var encryptUtil: EncryptUtil
    private lateinit var useCase: EncryptLocationUseCaseImpl

    @BeforeEach
    fun setup() {
        locationManager = mockk()
        encryptUtil = mockk()
        useCase = EncryptLocationUseCaseImpl(locationManager, encryptUtil)
    }

    @Test
    fun `getEncryptLocation returns encrypted location`() {
        val latitude = "51.5074"
        val longitude = "0.1278"
        every { locationManager.getLatitude() } returns latitude
        every { locationManager.getLongitude() } returns longitude
        every {
            encryptUtil.encryptAES128Base64(
                "$latitude, $longitude",
                "hQeRhom34d9u3sTo",
                "sCwVxPGoahFn5QBL"
            )
        } returns "encrypted_location"

        val result = useCase.getEncryptLocation()

        assertEquals("encrypted_location", result)
    }

    @Test
    fun `getEncryptLocation returns empty string when latitude is blank`() {
        every { locationManager.getLatitude() } returns ""
        every { locationManager.getLongitude() } returns "0.1278"

        val result = useCase.getEncryptLocation()

        assertEquals("", result)
    }

    @Test
    fun `getEncryptLocation returns empty string when longitude is blank`() {
        every { locationManager.getLatitude() } returns "51.5074"
        every { locationManager.getLongitude() } returns ""

        val result = useCase.getEncryptLocation()

        assertEquals("", result)
    }

    @Test
    fun `getEncryptLocation returns empty string when encryption fails`() {
        every { locationManager.getLatitude() } returns "51.5074"
        every { locationManager.getLongitude() } returns "0.1278"
        every {
            encryptUtil.encryptAES128Base64(
                any(),
                any(),
                any()
            )
        } throws InvalidKeyException()
        // The same test could be repeated with other exceptions (UnsupportedEncodingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException)

        val result = useCase.getEncryptLocation()

        assertEquals("", result)
    }
}
