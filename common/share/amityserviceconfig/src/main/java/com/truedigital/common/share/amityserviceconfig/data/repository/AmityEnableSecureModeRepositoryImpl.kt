package com.truedigital.common.share.amityserviceconfig.data.repository

import com.truedigital.common.share.amityserviceconfig.domain.repository.AmityEnableSecureModeRepository
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import javax.inject.Inject

class AmityEnableSecureModeRepositoryImpl @Inject constructor(
    private val repo: InitialAppConfigRepository
) : AmityEnableSecureModeRepository {
    companion object {
        const val AMITY_SERVICE_KEY = "amity_service"
        const val AMITY_SERVICE_ENABLE_SECURE_MODE = "enable_secure_mode"
        const val AMITY_SERVICE_ANDROID = "android"
    }

    override fun isAmityEnableSecureMode(): Boolean {
        val config = repo.getConfigByKey(AMITY_SERVICE_KEY)
        return getEnable(config as? Map<*, *>)
    }

    override suspend fun isAmityEnableSecureModeByCountry(countryCode: String): Boolean {
        val config = repo.getConfigByKey(
            AMITY_SERVICE_KEY, countryCode
        )
        return getEnable(config as? Map<*, *>)
    }

    private fun getEnable(config: Map<*, *>?): Boolean {
        var isEnable = false
        config?.let { _config ->
            val enable = _config[AMITY_SERVICE_ENABLE_SECURE_MODE] as? Map<*, *>
            isEnable = enable?.get(AMITY_SERVICE_ANDROID) as? Boolean ?: false
        }
        return isEnable
    }
}
