package com.truedigital.common.share.amityserviceconfig.data.repository

import com.amity.socialcloud.uikit.common.utils.AmityAppLocale
import com.truedigital.common.share.amityserviceconfig.domain.repository.AmitySetupAppLocaleRepository
import javax.inject.Inject

class AmitySetupAppLocaleRepositoryImpl @Inject constructor(
    private val amityAppLocale: AmityAppLocale
) : AmitySetupAppLocaleRepository {

    override fun amitySetupAppLocale(
        countryLang: String,
        languageCode: String
    ) {
        amityAppLocale.apply {
            appCountry = countryLang
            appLanguage = languageCode
        }
    }
}
