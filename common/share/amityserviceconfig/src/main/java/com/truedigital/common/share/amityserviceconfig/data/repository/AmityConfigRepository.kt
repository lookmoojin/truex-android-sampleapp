package com.truedigital.common.share.amityserviceconfig.data.repository

import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import javax.inject.Inject

interface AmityConfigRepository {
    fun isAmityConfigEnable(): Boolean
    suspend fun isAmityConfigEnableByCountry(countryCode: String): Boolean
}

class AmityConfigRepositoryImpl @Inject constructor(
    private val repo: InitialAppConfigRepository
) : AmityConfigRepository {
    companion object {
        const val AMITY_SERVICE_KEY = "amity_service"
        const val AMITY_SERVICE_ENABLE = "enable"
        const val AMITY_SERVICE_ANDROID = "android"
    }

    override fun isAmityConfigEnable(): Boolean {
        val config = repo.getConfigByKey(AMITY_SERVICE_KEY)
        return getEnable(config as? Map<*, *>)
    }

    override suspend fun isAmityConfigEnableByCountry(countryCode: String): Boolean {
        val config = repo.getConfigByKey(
            AMITY_SERVICE_KEY, countryCode
        )
        return getEnable(config as? Map<*, *>)
    }

    private fun getEnable(config: Map<*, *>?): Boolean {
        var isEnable = false
        config?.let { _config ->
            val enable = _config[AMITY_SERVICE_ENABLE] as? Map<*, *>
            isEnable = enable?.get(AMITY_SERVICE_ANDROID) as? Boolean ?: false
        }
        return isEnable
    }
}
