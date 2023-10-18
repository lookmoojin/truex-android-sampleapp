package com.truedigital.core.domain.usecase

import com.newrelic.agent.android.NewRelic
import com.truedigital.core.data.repository.DeviceRepository
import javax.inject.Inject

interface IsBypassSSLUseCase {
    fun execute(buildDebug: Boolean = false): Boolean
}

class IsBypassSSLUseCaseImpl @Inject constructor(
    private val getAnimalUseCase: GetAnimalUseCase,
    private val deviceRepository: DeviceRepository
) : IsBypassSSLUseCase {
    override fun execute(buildDebug: Boolean): Boolean {
        val animal = getAnimalUseCase.execute()
        val device = deviceRepository.getAndroidId()
        val whale = animal.whale?.contains(device) ?: false
        val attributes = mapOf(
            "deviceId" to device
        )
        NewRelic.recordCustomEvent("ActiveDevice", attributes)
        return when {
            buildDebug -> true
            !animal.cat -> true
            else -> whale
        }
    }
}
