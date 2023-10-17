package com.truedigital.features.tuned.domain.facade.authentication

import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.device.repository.DeviceRepository
import com.truedigital.features.tuned.data.user.model.AssociatedDevice
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.presentation.main.facade.MusicAuthenticationFacade
import io.reactivex.Single
import javax.inject.Inject

class MusicAuthenticationFacadeImpl @Inject constructor(
    private val authenticationThirdPartyJWTUseCase: AuthenticationThirdPartyJWTUseCase,
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val musicUserRepository: MusicUserRepository,
    private val deviceRepository: DeviceRepository,
    private val authenticationTokenRepository: AuthenticationTokenRepository
) : MusicAuthenticationFacade {

    override fun getTrueUserId() = musicUserRepository.getTrueUserId()

    override fun loginJwt(trueUserId: Int, trueUserJwt: String): Single<Any> {
        val hasUser = musicUserRepository.exist()
        val device = deviceRepository.get()

        return if (hasUser) {
            getAuthenticatedUserUseCase.execute(device)
        } else {
            authenticationThirdPartyJWTUseCase.execute(device, trueUserJwt)
        }
            .flatMap { user ->
                val isExistUniqueId = checkIsExistDeviceUniqueId(device.uniqueId, user.devices)
                if (!isExistUniqueId) {
                    musicUserRepository.addDevice(device)
                        .flatMap { musicUserRepository.get(true) }
                } else {
                    Single.just(false)
                }
            }
            .map { Any() }
            .onErrorResumeNext {
                authenticationThirdPartyJWTUseCase.execute(deviceRepository.get(), trueUserJwt)
            }
    }

    private fun checkIsExistDeviceUniqueId(
        uniqueId: String,
        devices: List<AssociatedDevice>
    ): Boolean {
        val deviceList = devices.map { device ->
            device.uniqueId
        }.filter { _uniqueId ->
            _uniqueId == uniqueId
        }
        return deviceList.isNotEmpty()
    }

    override fun isLogout(): Boolean {
        val userId = musicUserRepository.getUserId()
        val userIdFromToken = authenticationTokenRepository.getUserIdFromToken()

        var isLogout = false

        if (userId == null || userIdFromToken == null || userId != userIdFromToken) {
            isLogout = true
        }
        return isLogout
    }

    override fun logout() {
        authenticationTokenRepository.clearToken()
        musicUserRepository.logout()
    }
}
