package com.truedigital.share.data.firestoreconfig.domainconfig.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.repository.DeviceRepository
import com.truedigital.share.data.firestoreconfig.domainconfig.repository.DomainRepository
import javax.inject.Inject

interface GetDomainServiceApiDataUseCase {
    fun execute()
}

class GetDomainServiceApiDataUseCaseImpl @Inject constructor(
    var deviceRepository: DeviceRepository,
    var domainRepository: DomainRepository,
    private val localizationRepository: LocalizationRepository,
) : GetDomainServiceApiDataUseCase {
    override fun execute() {
        domainRepository.loadApiConfig(
            versionName = getVersionWithoutPatch(deviceRepository.getAppVersion()),
            countryCode = localizationRepository.getAppCountryCode()
        )
    }

    private fun getVersionWithoutPatch(versionName: String): String {
        val list = versionName.split(".").toMutableList()

        // return something like 2.22 from string format : 2.22.0, 2.22.0.1
        return if (list.size >= 2) {
            list[0] + "." + list[1]
        } else {
            ""
        }
    }
}
