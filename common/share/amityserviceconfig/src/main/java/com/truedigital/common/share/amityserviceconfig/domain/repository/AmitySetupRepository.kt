package com.truedigital.common.share.amityserviceconfig.domain.repository

interface AmitySetupRepository {
    fun amitySetupApiKey(
        amityKeyByCountry: String
    )
}
