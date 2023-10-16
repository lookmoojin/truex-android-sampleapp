package com.truedigital.common.share.data.coredata.domain

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import javax.inject.Inject

interface GetEnablePrivilegeUseCase {
    suspend fun execute(): Boolean
}

class GetEnablePrivilegeUseCaseImpl @Inject constructor(
    private val initialAppConfigRepository: InitialAppConfigRepository,
    private val localizationRepository: LocalizationRepository
) : GetEnablePrivilegeUseCase {

    companion object {
        const val NODE_NAME_PRIVILEGE = "privilege"
        const val NODE_NAME_ENABLE = "enable"
        const val NODE_NAME_ANDROID = "android"
    }

    override suspend fun execute(): Boolean {
        val config = initialAppConfigRepository.getConfigByKey(
            NODE_NAME_PRIVILEGE,
            localizationRepository.getAppCountryCode()
        ) as? Map<*, *>
        val enable = config?.get(NODE_NAME_ENABLE) as? Map<*, *>
        return enable?.get(NODE_NAME_ANDROID) as? Boolean ?: true
    }
}
