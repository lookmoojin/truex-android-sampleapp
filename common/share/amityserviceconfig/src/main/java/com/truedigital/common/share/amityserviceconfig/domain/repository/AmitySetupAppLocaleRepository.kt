package com.truedigital.common.share.amityserviceconfig.domain.repository

interface AmitySetupAppLocaleRepository {
    fun amitySetupAppLocale(
        countryLang: String,
        languageCode: String
    )
}
