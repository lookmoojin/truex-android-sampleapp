package com.truedigital.common.share.datalegacy.domain.other.usecase

import com.truedigital.core.BuildConfig
import com.truedigital.core.manager.location.LocationManager
import com.truedigital.core.utils.EncryptUtil
import java.io.UnsupportedEncodingException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.inject.Inject

interface EncryptLocationUseCase {
    fun getEncryptLocation(): String
}

class EncryptLocationUseCaseImpl @Inject constructor(
    private val locationManager: LocationManager,
    private val encryptUtil: EncryptUtil
) : EncryptLocationUseCase {

    override fun getEncryptLocation(): String {
        if (locationManager.getLatitude() == "" || locationManager.getLongitude() == "") {
            return ""
        }

        val deviceLocation = "${locationManager.getLatitude()}, ${locationManager.getLongitude()}"
        var encryptDeviceLocation = ""

        try {
            encryptDeviceLocation = encryptUtil.encryptAES128Base64(
                deviceLocation,
                BuildConfig.GOOGLE_ANALYTIC_ENCRYPTION_KEY,
                BuildConfig.GOOGLE_ANALYTIC_ENCRYPTION_IV
            )
        } catch (e: InvalidKeyException) {
            return ""
        } catch (e: UnsupportedEncodingException) {
            return ""
        } catch (e: InvalidAlgorithmParameterException) {
            return ""
        } catch (e: BadPaddingException) {
            return ""
        } catch (e: IllegalBlockSizeException) {
            return ""
        }

        return encryptDeviceLocation
    }
}
