package com.truedigital.common.share.amityserviceconfig.domain.repository

interface AmityEnableSecureModeRepository {
    fun isAmityEnableSecureMode(): Boolean
    suspend fun isAmityEnableSecureModeByCountry(countryCode: String): Boolean
}
