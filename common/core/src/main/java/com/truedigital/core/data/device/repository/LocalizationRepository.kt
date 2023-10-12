package com.truedigital.core.data.device.repository

import com.newrelic.agent.android.NewRelic
import com.truedigital.core.data.device.model.LocalizationModel
import com.truedigital.core.utils.SharedPrefsUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import java.util.Locale
import javax.inject.Inject

/***************************************************************************************************
 *
 * The `LocalizationRepository` class represents the localization things
 * such as
 * - LanguageCode
 * - CountryCode
 * - Locale
 *
 * Note: This class will replace [com.tdcm.trueidapp.features.utils.AppUtils]
 *
 **************************************************************************************************/
interface LocalizationRepository {

    enum class Localization(val languageCode: String, val countryCode: String) {
        TH("th", "TH"),
        EN("en", "EN"),
        MY("my", "MM"),
        VN("vi", "VN"),
        KH("km", "KH"),
        PH("fil", "PH"),
        IN("id", "ID")
    }

    fun initial()

    fun getAppLanguageCode(): String
    fun getAppLanguageCodeForEnTh(): String
    fun getAppCountryAndLocalizationCode(): String
    fun setLocalization(countryCode: String, languageCode: String)
    fun getAppCountryCode(): String
    fun getAppCountryCodeWithOutDefaultValue(): String
    fun getAppID(): String
    fun setAppID(appId: String)
    fun getAppClientID(): String
    fun setAppClientID(clientId: String)

    fun getAppLocale(): Locale
    fun getAppLocaleForEnTh(): Locale
    fun getLocale(localization: LocalizationRepository.Localization): Locale
    fun getLocale(language: String, country: String): Locale

    fun getAppLocalization(): Localization
    fun getAppLocalizationForEnTh(): Localization
    fun getLocalization(localizationString: String): Localization
    fun setSupportLanguage(listLanguageCode: List<String>)
    fun findSupportLanguage(remoteLanguage: String): String?

    fun getAppCountryCodeFlow(): Flow<String>
    fun getAppCountryCodeChangedEvent(): Flow<Unit>

    fun localize(model: LocalizationModel): String
}

class LocalizationRepositoryImpl @Inject constructor(private val sharedPrefs: SharedPrefsUtils) :
    LocalizationRepository {

    companion object {
        private val DEFAULT_LANGUAGE = LocalizationRepository.Localization.EN.languageCode
        private const val DEFAULT_APP_ID = "trueid.th"
        private const val DEFAULT_CLIENT_ID = "212"
        private const val DEFAULT_COUNTRY = "th"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_COUNTRY = "country"
        private const val KEY_SUPPORT_LANGUAGE = "support_language"
        private const val KEY_APP_ID = "app_id"
        private const val KEY_CLIENT_ID = "client_id"
    }

    private val _countryCodeFlow = MutableStateFlow(getAppCountryCode())
    private val _countryCodeChangedEvent: MutableStateFlow<Unit?> = MutableStateFlow(null)

    override fun initial() {
        if (sharedPrefs.get(KEY_LANGUAGE, "").isEmpty()) {
            if (Locale.getDefault().language == LocalizationRepository.Localization.TH.languageCode) {
                sharedPrefs.put(KEY_LANGUAGE, LocalizationRepository.Localization.TH.languageCode)
            } else {
                sharedPrefs.put(KEY_LANGUAGE, DEFAULT_LANGUAGE)
            }
        }
    }

    override fun getAppLanguageCode(): String {
        return sharedPrefs.get(KEY_LANGUAGE, DEFAULT_LANGUAGE)
    }

    override fun getAppLanguageCodeForEnTh(): String {
        // This function only returns EN/TH. (For APIs that only supports EN and TH language)
        val languageCode = getAppLanguageCode()
        return determineLanguageCodeForEnTh(languageCode)
    }

    override fun setLocalization(countryCode: String, languageCode: String) {
        notifyCountrySet(countryCode)
        sharedPrefs.put(KEY_LANGUAGE, languageCode.lowercase())
        sharedPrefs.put(KEY_COUNTRY, countryCode)
    }

    override fun getAppLocale(): Locale {
        return getLocale(getAppLanguageCode(), getAppCountryCode())
    }

    override fun getAppLocaleForEnTh(): Locale {
        // This function only returns EN/TH. (For APIs that only supports EN and TH language)
        return getLocale(getAppLocalizationForEnTh())
    }

    override fun getLocale(localization: LocalizationRepository.Localization): Locale {
        return Locale(localization.languageCode, localization.countryCode)
    }

    override fun getLocale(language: String, country: String): Locale {
        return Locale(language, country)
    }

    override fun getAppLocalization(): LocalizationRepository.Localization {
        val language = sharedPrefs.get(KEY_LANGUAGE, DEFAULT_LANGUAGE)
        return if (language.equals(
                LocalizationRepository.Localization.TH.languageCode,
                ignoreCase = true
            )
        ) {
            LocalizationRepository.Localization.TH
        } else {
            LocalizationRepository.Localization.EN
        }
    }

    override fun getAppLocalizationForEnTh(): LocalizationRepository.Localization {
        // This function only returns EN/TH. (For APIs that only supports EN and TH language)
        return getLocalization(getAppLanguageCodeForEnTh().toUpperCase())
    }

    override fun getAppCountryCode(): String {
        return sharedPrefs.get(KEY_COUNTRY, DEFAULT_COUNTRY)
    }

    override fun getAppCountryCodeWithOutDefaultValue(): String {
        return sharedPrefs.get(KEY_COUNTRY, "")
    }

    override fun getLocalization(localizationString: String): LocalizationRepository.Localization {
        return try {
            LocalizationRepository.Localization.valueOf(localizationString)
        } catch (e: IllegalArgumentException) {
            val handlingExceptionMap = mapOf(
                "Key" to "LocalizationRepository",
                "Value" to "getAppLocalization() localizationString is: $localizationString"
            )
            NewRelic.recordHandledException(e, handlingExceptionMap)

            LocalizationRepository.Localization.EN
        }
    }

    override fun setSupportLanguage(listLanguageCode: List<String>) {
        sharedPrefs.put(KEY_SUPPORT_LANGUAGE, listLanguageCode)
    }

    override fun findSupportLanguage(remoteLanguage: String): String? {
        val listLanguageCode = sharedPrefs.get<List<String>>(KEY_SUPPORT_LANGUAGE, listOf())
        return listLanguageCode.find { it.contains(remoteLanguage, true) }
    }

    override fun getAppCountryCodeFlow(): Flow<String> {
        return _countryCodeFlow
    }

    override fun getAppCountryCodeChangedEvent(): Flow<Unit> {
        return _countryCodeChangedEvent.filterNotNull()
    }

    override fun getAppCountryAndLocalizationCode(): String {
        return "${getAppCountryCodeWithOutDefaultValue().toLowerCase(Locale.ROOT)}_${getAppLanguageCode()}"
    }

    override fun getAppID(): String {
        return sharedPrefs.get(KEY_APP_ID, DEFAULT_APP_ID)
    }

    override fun setAppID(appId: String) {
        sharedPrefs.put(KEY_APP_ID, appId)
    }

    override fun getAppClientID(): String {
        return sharedPrefs.get(KEY_CLIENT_ID, DEFAULT_CLIENT_ID)
    }

    override fun setAppClientID(clientId: String) {
        sharedPrefs.put(KEY_CLIENT_ID, clientId)
    }

    override fun localize(model: LocalizationModel): String {
        return localization(model)
    }

    private fun notifyCountrySet(newCountryCode: String) {
        if (getAppCountryCode() != newCountryCode) {
            _countryCodeFlow.value = newCountryCode
            _countryCodeChangedEvent.value = Unit
        }
    }
}

fun LocalizationRepository.determineLanguageCodeForEnTh(languageCode: String): String {
    return if (languageCode.equals(
            LocalizationRepository.Localization.TH.languageCode,
            ignoreCase = true
        )
    ) {
        LocalizationRepository.Localization.TH.languageCode
    } else {
        LocalizationRepository.Localization.EN.languageCode
    }
}

fun LocalizationRepository.localization(
    localizationModel: LocalizationModel,
    default: String = localizationModel.enWord
): String {
    return when (this.getAppLanguageCode()) {
        LocalizationRepository.Localization.TH.languageCode -> {
            localizationModel.thWord.ifEmpty { default }
        }
        LocalizationRepository.Localization.MY.languageCode -> {
            localizationModel.myWord.ifEmpty { default }
        }
        LocalizationRepository.Localization.KH.languageCode -> {
            localizationModel.kmWord.ifEmpty { default }
        }
        LocalizationRepository.Localization.VN.languageCode -> {
            localizationModel.vnWord.ifEmpty { default }
        }
        LocalizationRepository.Localization.PH.languageCode -> {
            localizationModel.phWord.ifEmpty { default }
        }
        LocalizationRepository.Localization.IN.languageCode -> {
            localizationModel.idWord.ifEmpty { default }
        }
        LocalizationRepository.Localization.EN.languageCode -> {
            localizationModel.enWord
        }
        else -> {
            ""
        }
    }
}
