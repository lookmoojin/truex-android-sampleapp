package com.truedigital.features.truecloudv3.data.repository

import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.truecloudv3.data.model.ConfigIntroModel
import com.truedigital.features.truecloudv3.data.model.IntroLanguageModel
import com.truedigital.features.truecloudv3.data.model.IntroLoginModel
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import com.truedigital.share.mock.firebase.firestore.FirebaseFirestoreTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal interface ConfigIntroImageRepositoryTestCase {
    fun `test getConfig case mobile success return image`()
    fun `test getConfig case tablet success return image`()
    fun `test getConfig case mobile and IntroLoginModel is null return null`()
    fun `test getConfig case tablet and IntroLoginModel is null return null`()
    fun `test getConfig case mobile failed`()
    fun `test getConfig case tablet failed`()
}

internal class ConfigIntroImageRepositoryTest : ConfigIntroImageRepositoryTestCase,
    FirebaseFirestoreTest() {

    private lateinit var configIntroImageRepository: ConfigIntroImageRepository
    private val firestoreUtil: FirestoreUtil = mockk()

    @BeforeEach
    fun setUp() {
        every {
            firestoreUtil.getFirestore()
        } returns firebaseFirestore

        configIntroImageRepository = ConfigIntroImageRepositoryImpl(firestoreUtil)
    }

    @Test
    override fun `test getConfig case mobile success return image`() = runTest {
        // arrange
        getDataSuccess()
        val mockCountryCode = LocalizationRepository.Localization.TH.countryCode
        val mockIsTablet = false
        val mockLanguageMobileModel = mapOf(
            "en" to "https://www.mock-url.com/mobile/image-en",
            "my" to "https://www.mock-url.com/mobile/image-my",
            "th" to "https://www.mock-url.com/mobile/image-th"
        )
        val mockLanguageTabletModel = mapOf(
            "en" to "https://www.mock-url.com/tablet/image-en",
            "my" to "https://www.mock-url.com/tablet/image-my",
            "th" to "https://www.mock-url.com/tablet/image-th"
        )
        val mockLoginModel = mapOf(
            "login_image" to mockLanguageMobileModel,
            "login_image_table" to mockLanguageTabletModel
        )
        val mockResponse = mapOf(
            "login" to mockLoginModel
        )
        whenever(firestoreDocumentSnapshot.data).thenReturn(mockResponse)

        // act
        val response = configIntroImageRepository.getConfig(mockCountryCode, mockIsTablet)

        // assert
        assertEquals(getExpected().login.introImage.en, response?.en)
        assertEquals(getExpected().login.introImage.my, response?.my)
        assertEquals(getExpected().login.introImage.th, response?.th)
    }

    @Test
    override fun `test getConfig case tablet success return image`() = runTest {
        // arrange
        getDataSuccess()
        val mockCountryCode = LocalizationRepository.Localization.TH.countryCode
        val mockIsTablet = true
        val mockLanguageMobileModel = mapOf(
            "en" to "https://www.mock-url.com/mobile/image-en",
            "my" to "https://www.mock-url.com/mobile/image-my",
            "th" to "https://www.mock-url.com/mobile/image-th"
        )
        val mockLanguageTabletModel = mapOf(
            "en" to "https://www.mock-url.com/tablet/image-en",
            "my" to "https://www.mock-url.com/tablet/image-my",
            "th" to "https://www.mock-url.com/tablet/image-th"
        )
        val mockLoginModel = mapOf(
            "login_image" to mockLanguageMobileModel,
            "login_image_tablet" to mockLanguageTabletModel
        )
        val mockResponse = mapOf(
            "login" to mockLoginModel
        )
        whenever(firestoreDocumentSnapshot.data).thenReturn(mockResponse)

        // act
        val response = configIntroImageRepository.getConfig(mockCountryCode, mockIsTablet)

        // assert
        assertEquals(getExpected().login.introImageTablet.en, response?.en)
        assertEquals(getExpected().login.introImageTablet.my, response?.my)
        assertEquals(getExpected().login.introImageTablet.th, response?.th)
    }

    @Test
    override fun `test getConfig case mobile and IntroLoginModel is null return null`() = runTest {
        // arrange
        getDataSuccess()
        val mockCountryCode = LocalizationRepository.Localization.TH.countryCode
        val mockIsTablet = false
        val mockResponse = mapOf(
            "login" to null
        )
        whenever(firestoreDocumentSnapshot.data).thenReturn(mockResponse)

        // act
        val response = configIntroImageRepository.getConfig(mockCountryCode, mockIsTablet)

        // assert
        assertNull(response)
    }

    @Test
    override fun `test getConfig case tablet and IntroLoginModel is null return null`() = runTest {
        // arrange
        getDataSuccess()
        val mockCountryCode = LocalizationRepository.Localization.TH.countryCode
        val mockIsTablet = true
        val mockResponse = mapOf(
            "login" to null
        )
        whenever(firestoreDocumentSnapshot.data).thenReturn(mockResponse)

        // act
        val response = configIntroImageRepository.getConfig(mockCountryCode, mockIsTablet)

        // assert
        assertNull(response)
    }

    @Test
    override fun `test getConfig case mobile failed`() = runTest {
        // arrange
        getDataFailure()
        val mockCountryCode = LocalizationRepository.Localization.TH.countryCode
        val mockIsTablet = false
        whenever(firestoreDocumentSnapshot.data).thenReturn(null)

        // act
        val response = configIntroImageRepository.getConfig(mockCountryCode, mockIsTablet)

        assertNull(response)
    }

    @Test
    override fun `test getConfig case tablet failed`() = runTest {
        // arrange
        getDataFailure()
        val mockCountryCode = LocalizationRepository.Localization.TH.countryCode
        val mockIsTablet = true
        whenever(firestoreDocumentSnapshot.data).thenReturn(null)

        // act
        val response = configIntroImageRepository.getConfig(mockCountryCode, mockIsTablet)

        assertNull(response)
    }

    private fun getExpected() = ConfigIntroModel(
        login = IntroLoginModel(
            introImage = IntroLanguageModel(
                en = "https://www.mock-url.com/mobile/image-en",
                my = "https://www.mock-url.com/mobile/image-my",
                th = "https://www.mock-url.com/mobile/image-th"
            ),
            introImageTablet = IntroLanguageModel(
                en = "https://www.mock-url.com/tablet/image-en",
                my = "https://www.mock-url.com/tablet/image-my",
                th = "https://www.mock-url.com/tablet/image-th"
            )
        )
    )
}
