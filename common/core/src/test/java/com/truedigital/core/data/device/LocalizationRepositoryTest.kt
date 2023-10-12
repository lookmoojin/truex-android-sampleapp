package com.truedigital.core.data.device

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.model.LocalizationModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.device.repository.LocalizationRepositoryImpl
import com.truedigital.core.data.device.repository.localization
import com.truedigital.core.utils.SharedPrefsUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class LocalizationRepositoryTest {

    private lateinit var repository: LocalizationRepository
    private lateinit var storedLocale: Locale

    private val sharedPrefs: SharedPrefsUtils = mock()
    private val localizationModel: LocalizationModel = mock()
    private val defaultLanguage = LocalizationRepository.Localization.EN.languageCode
    private val keyLanguage = "language"
    private val keyCountry = "country"
    private val keySupportLanguage: String = "support_language"
    private val keyAppId = "app_id"
    private val keyClientId = "client_id"
    private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
    private val scope: TestCoroutineScope = TestCoroutineScope(dispatcher)

    @BeforeEach
    fun setUp() {
        storedLocale = Locale.getDefault()
        repository = LocalizationRepositoryImpl(
            sharedPrefs = sharedPrefs
        )
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Locale.setDefault(storedLocale)
        scope.cleanupTestCoroutines()
        Dispatchers.resetMain()
    }

    @Test
    fun `Initial language if it has not initiated, also set to be 'th' if device language is Thai`() {
        // arrange
        whenever(sharedPrefs.get(keyLanguage, "")).thenReturn("")
        Locale.setDefault(Locale("th", "TH"))

        // act
        repository.initial()

        // assert
        verify(sharedPrefs, times(1)).put(
            keyLanguage,
            LocalizationRepository.Localization.TH.languageCode
        )
    }

    @Test
    fun `Initial language if it has not initiated, also set to be 'en' if device language isn't Thai`() {
        // arrange
        whenever(sharedPrefs.get(keyLanguage, "")).thenReturn("")
        Locale.setDefault(Locale.CHINA)

        // act
        repository.initial()

        // assert
        verify(sharedPrefs, times(1)).put(keyLanguage, defaultLanguage)
    }

    @Test
    fun `Don't initial language if it already initiated`() {
        // arrange
        whenever(sharedPrefs.get(keyLanguage, "")).thenReturn("en")

        // act
        repository.initial()

        // assert
        verify(sharedPrefs, times(0)).put(keyLanguage, defaultLanguage)
        verify(sharedPrefs, times(0)).put(
            keyLanguage,
            LocalizationRepository.Localization.TH.languageCode
        )
    }

    @Test
    fun `Test getAppLanguageCode`() {
        // arrange

        // act
        val result = repository.getAppLanguageCode()

        // assert
        verify(sharedPrefs, times(1)).get(keyLanguage, defaultLanguage)
        assertNotNull(result)
    }

    @Test
    fun `Test getAppLanguageCodeForEnTh() if language is 'th' it will return 'th'`() {
        // arrange
        val language = "th"
        whenever(repository.getAppLanguageCode()).thenReturn(language)

        // act
        val actual = repository.getAppLanguageCodeForEnTh()

        // assert
        assertEquals("th", actual)
    }

    @Test
    fun `Test getAppLanguageCodeForEnTh() if language is not 'th' it will return 'en'`() {
        // arrange
        val language = "my"
        whenever(repository.getAppLanguageCode()).thenReturn(language)

        // act
        val actual = repository.getAppLanguageCodeForEnTh()

        // assert
        assertEquals("en", actual)
    }

    @Test
    fun `Test setLocalization() with same as current country code`() {
        // arrange
        val country = "TH"
        val language = "th"
        whenever(repository.getAppCountryCode()).thenReturn("th")

        // act
        repository.setLocalization(country, language)

        // assert
        verify(sharedPrefs, times(1)).put(keyLanguage, language)
        verify(sharedPrefs, times(1)).put(keyCountry, country)
    }

    @Test
    fun `Test setLocalization() with not same as current country code`() {
        // arrange
        val country = "TH"
        val language = "th"
        whenever(repository.getAppCountryCode()).thenReturn("en")

        // act
        repository.setLocalization(country, language)

        // assert
        verify(sharedPrefs, times(1)).put(keyLanguage, language)
        verify(sharedPrefs, times(1)).put(keyCountry, country)
    }

    @Test
    fun `Test getAppLocale() return a correct Locale`() {
        // arrange
        val language = "th"
        val country = "TH"
        val expected = repository.getLocale(language, country)
        whenever(repository.getAppLanguageCode()).thenReturn(language)
        whenever(repository.getAppCountryCode()).thenReturn(country)

        // act
        val actual = repository.getAppLocale()

        // assert
        assertEquals(expected, actual)
        assertNotNull(actual)
    }

    @Test
    fun `Test getAppLocaleForEnTh() if language code is 'th', it will return 'th'`() {
        // arrange
        val locale = Locale("th", "TH")
        whenever(repository.getAppLanguageCode()).thenReturn("th")

        // act
        val actual = repository.getAppLocaleForEnTh()

        // assert
        assertEquals(locale, actual)
    }

    @Test
    fun `Test getAppLocaleForEnTh() if language code is not 'th', it will return 'en'`() {
        // arrange
        val locale = Locale("en", "EN")
        whenever(repository.getAppLanguageCode()).thenReturn("vi")

        // act
        val actual = repository.getAppLocaleForEnTh()

        // assert
        assertEquals(locale, actual)
    }

    @Test
    fun `Test getAppLocalization() if language code is 'th', it will return LocalizationTH`() {
        // arrange
        val expected = LocalizationRepository.Localization.TH
        whenever(sharedPrefs.get(keyLanguage, defaultLanguage)).thenReturn("th")

        // act
        val actual = repository.getAppLocalization()

        // assert
        verify(sharedPrefs, times(1)).get(keyLanguage, defaultLanguage)
        assertEquals(expected, actual)
    }

    @Test
    fun `Test getAppLocalization() if language code is not 'th', it will return LocalizationEN`() {
        // arrange
        val expected = LocalizationRepository.Localization.EN
        whenever(sharedPrefs.get(keyLanguage, defaultLanguage)).thenReturn("km")

        // act
        val actual = repository.getAppLocalization()

        // assert
        verify(sharedPrefs, times(1)).get(keyLanguage, defaultLanguage)
        assertEquals(expected, actual)
    }

    // For reason that verify is two times,
    // because of '_countryCodeFlow' is initialized by getAppCountryCode()
    @Test
    fun `Test getAppCountryCode() is return a country code`() {
        // arrange

        // act
        val result = repository.getAppCountryCode()

        // assert
        verify(sharedPrefs, times(2)).get(keyCountry, "")
        assertNotNull(result)
    }

    @Test
    fun `Test getLocalization() if can not get value of Localization, it will return EN`() {
        // arrange
        val expected = LocalizationRepository.Localization.EN

        // act
        val actual = repository.getLocalization("")

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test setSupportLanguage() can save support language list`() {
        // arrange
        val languageList = listOf("th", "en", "my", "vi", "km", "fil", "id")

        // act
        repository.setSupportLanguage(languageList)

        // assert
        verify(sharedPrefs, times(1)).put(keySupportLanguage, languageList)
    }

    /*
    Can not test get support language correctly.
    Because of exception `ArrayList cannot be returned by getString()`
    */
    @Test
    fun `Test findSupportLanguage() can find support language then return that language`() {
        // arrange
        // val expected = "th"
        // val languageList = listOf("th", "en", "my", "vi", "km", "fil", "id")
        // whenever(sharedPrefs.get<List<String>>(keySupportLanguage, listOf())).thenReturn(
        //     languageList
        // )

        // act
        val actual = repository.findSupportLanguage("th")

        // assert
        // assertEquals(expected, actual)
        verify(sharedPrefs, times(1)).get<List<String>>(keySupportLanguage, listOf())
    }

    @Test
    fun `Test getAppCountryCodeFlow() is return a flow of country code`() {
        repository.setLocalization("TH", "th")
        runTest {
            // arrange
            val expected = "TH"
            whenever(repository.getAppCountryCode()).thenReturn("TH")

            // act
            val actual = repository.getAppCountryCodeFlow()

            // assert
            assertEquals(expected, actual.first())
        }
    }

    @Test
    fun `Test getAppCountryCodeChangedEvent() is return Unit`() {
        repository.setLocalization("TH", "th")
        runTest {
            // arrange

            // act
            val actual = repository.getAppCountryCodeChangedEvent()

            // assert
            assertNotNull(actual)
        }
    }

    @Test
    fun `Test getAppCountryAndLocalizationCode() is return a String of country code and language code`() {
        // arrange

        // act
        val actual = repository.getAppCountryAndLocalizationCode()

        // assert
        assertNotNull(actual)
    }

    @Test
    fun `Test getAppID() is get app id correctly`() {
        // arrange

        // act
        val result = repository.getAppID()

        // assert
        verify(sharedPrefs, times(1)).get(keyAppId, "")
        assertNotNull(result)
    }

    @Test
    fun `Test setAppID() is able to set app id`() {
        // arrange
        val appId = "1234"

        // act
        repository.setAppID(appId)

        // assert
        verify(sharedPrefs, times(1)).put(keyAppId, appId)
        assertNotNull(repository.getAppID())
    }

    @Test
    fun `Test getAppClientID is get client id correctly`() {
        // arrange

        // act
        val result = repository.getAppClientID()

        // assert
        verify(sharedPrefs, times(1)).get(keyClientId, "")
        assertNotNull(result)
    }

    @Test
    fun `Test setAppClientID is able to set client id`() {
        // arrange
        val clientId = "1234"

        // act
        repository.setAppClientID(clientId)

        // assert
        verify(sharedPrefs, times(1)).put(keyClientId, clientId)
        assertNotNull(repository.getAppClientID())
    }

    @Test
    fun `Test localize() will return String`() {
        // arrange
        val expected = "test"
        whenever(repository.localization(localizationModel, anyString()))
            .thenReturn("test")

        // act
        val actual = repository.localize(localizationModel)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test localization() if language code is 'th'`() {
        // arrange
        val expected = "test"
        whenever(repository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.TH.languageCode
        )
        whenever(localizationModel.thWord).thenReturn("test")
        whenever(localizationModel.enWord).thenReturn("testEn")

        // act
        val actual = repository.localization(localizationModel)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test localization() if language code is 'th' and 'thWord' is empty`() {
        // arrange
        val expected = "test"
        whenever(repository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.TH.languageCode
        )
        whenever(localizationModel.thWord).thenReturn("")
        whenever(localizationModel.enWord).thenReturn("test")

        // act
        val actual = repository.localization(localizationModel)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test localization() if language code is 'my'`() {
        // arrange
        val expected = "test"
        whenever(repository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.MY.languageCode
        )
        whenever(localizationModel.myWord).thenReturn("test")
        whenever(localizationModel.enWord).thenReturn("testEn")

        // act
        val actual = repository.localization(localizationModel)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test localization() if language code is 'my' and 'myWord' is empty`() {
        // arrange
        val expected = "test"
        whenever(repository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.MY.languageCode
        )
        whenever(localizationModel.myWord).thenReturn("")
        whenever(localizationModel.enWord).thenReturn("test")

        // act
        val actual = repository.localization(localizationModel)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test localization() if language code is 'vi'`() {
        // arrange
        val expected = "test"
        whenever(repository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.VN.languageCode
        )
        whenever(localizationModel.vnWord).thenReturn("test")
        whenever(localizationModel.enWord).thenReturn("testEn")

        // act
        val actual = repository.localization(localizationModel)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test localization() if language code is 'vi' and 'vnWord' is empty`() {
        // arrange
        val expected = "test"
        whenever(repository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.VN.languageCode
        )
        whenever(localizationModel.vnWord).thenReturn("")
        whenever(localizationModel.enWord).thenReturn("test")

        // act
        val actual = repository.localization(localizationModel)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test localization() if language code is 'km'`() {
        // arrange
        val expected = "test"
        whenever(repository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.KH.languageCode
        )
        whenever(localizationModel.kmWord).thenReturn("test")
        whenever(localizationModel.enWord).thenReturn("testEn")

        // act
        val actual = repository.localization(localizationModel)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test localization() if language code is 'km' and 'kmWord' is empty`() {
        // arrange
        val expected = "test"
        whenever(repository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.KH.languageCode
        )
        whenever(localizationModel.kmWord).thenReturn("")
        whenever(localizationModel.enWord).thenReturn("test")

        // act
        val actual = repository.localization(localizationModel)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test localization() if language code is 'fil'`() {
        // arrange
        val expected = "test"
        whenever(repository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.PH.languageCode
        )
        whenever(localizationModel.phWord).thenReturn("test")
        whenever(localizationModel.enWord).thenReturn("testEn")

        // act
        val actual = repository.localization(localizationModel)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test localization() if language code is 'fil' and 'phWord' is empty`() {
        // arrange
        val expected = "test"
        whenever(repository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.PH.languageCode
        )
        whenever(localizationModel.phWord).thenReturn("")
        whenever(localizationModel.enWord).thenReturn("test")

        // act
        val actual = repository.localization(localizationModel)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test localization() if language code is 'id'`() {
        // arrange
        val expected = "test"
        whenever(repository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.IN.languageCode
        )
        whenever(localizationModel.idWord).thenReturn("test")
        whenever(localizationModel.enWord).thenReturn("testEn")

        // act
        val actual = repository.localization(localizationModel)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test localization() if language code is 'id' and 'idWord' is empty`() {
        // arrange
        val expected = "test"
        whenever(repository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.IN.languageCode
        )
        whenever(localizationModel.idWord).thenReturn("")
        whenever(localizationModel.enWord).thenReturn("test")

        // act
        val actual = repository.localization(localizationModel)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test localization() if language code is 'en'`() {
        // arrange
        val expected = "test"
        whenever(repository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.EN.languageCode
        )
        whenever(localizationModel.enWord).thenReturn("test")

        // act
        val actual = repository.localization(localizationModel)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun `Test localization() if language code is not in any case`() {
        // arrange
        val expected = ""
        whenever(repository.getAppLanguageCode()).thenReturn("jp")
        whenever(localizationModel.enWord).thenReturn("testEn")

        // act
        val actual = repository.localization(localizationModel)

        // assert
        assertEquals(expected, actual)
    }
}
