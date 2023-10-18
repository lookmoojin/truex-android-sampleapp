package com.truedigital.features.apppermission.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.utils.SharedPrefsInterface
import com.truedigital.core.utils.get
import com.truedigital.features.apppermission.constant.AppPermissionConstant.Key.PERMISSION_UI_KEY
import com.truedigital.features.apppermission.data.repository.AppPermissionGetConfigRepository
import com.truedigital.features.apppermission.data.repository.AppPermissionGetConfigRepositoryImpl
import com.truedigital.features.apppermission.data.repository.AppPermissionGetConfigRepositoryImpl.Companion.KEY_SAVE_BUTTON
import com.truedigital.features.apppermission.data.repository.AppPermissionGetConfigRepositoryImpl.Companion.KEY_SAVE_IMAGE_LOCATION
import com.truedigital.features.apppermission.data.repository.AppPermissionGetConfigRepositoryImpl.Companion.KEY_SAVE_IMAGE_STORAGE
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AppPermissionGetConfigRepositoryTest {

    private val initialAppConfigRepository: InitialAppConfigRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()
    private val sharedPrefsInterface: SharedPrefsInterface = mock()
    private lateinit var appPermissionGetConfigRepository: AppPermissionGetConfigRepository

    @BeforeEach
    fun setUp() {
        appPermissionGetConfigRepository = AppPermissionGetConfigRepositoryImpl(
            initialAppConfigRepository = initialAppConfigRepository,
            localizationRepository = localizationRepository,
            sharedPrefsInterface = sharedPrefsInterface
        )
    }

    @Test
    fun testGetAppPermissionDataENSuccess_HaveData_ThenReturnData() {
        val dataEn = mapOf(
            "button" to "Continue",
            "image_location" to "url_location",
            "image_storage" to "url_storage"
        )
        val enable = mapOf("enable" to false)
        val data = mapOf(
            "enable" to enable,
            "en" to dataEn
        )
        val response = mapOf("android" to data)
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("en")
        whenever(initialAppConfigRepository.getConfigByKey(PERMISSION_UI_KEY)).thenReturn(response)
        val result = appPermissionGetConfigRepository.getAppPermissionData()
        verify(initialAppConfigRepository, times(1)).getConfigByKey(PERMISSION_UI_KEY)
        assertEquals(result.button, "Continue")
        assertEquals(result.imageLocation, "url_location")
        assertEquals(result.imageStorage, "url_storage")
    }

    @Test
    fun testGetAppPermissionDataTHSuccess_HaveData_ThenReturnData() {
        val dataTh = mapOf(
            "button" to "ดำเนินการ",
            "image_location" to "url_location",
            "image_storage" to "url_storage"
        )
        val dataEn = mapOf(
            "button" to "Continue",
            "image_location" to "url_location",
            "image_storage" to "url_storage"
        )
        val enable = mapOf("enable" to false)
        val data = mapOf(
            "enable" to enable,
            "th" to dataTh,
            "en" to dataEn
        )
        val response = mapOf("android" to data)
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
        whenever(initialAppConfigRepository.getConfigByKey(PERMISSION_UI_KEY)).thenReturn(response)
        val result = appPermissionGetConfigRepository.getAppPermissionData()
        verify(initialAppConfigRepository, times(1)).getConfigByKey(PERMISSION_UI_KEY)
        assertEquals(result.button, "ดำเนินการ")
        assertEquals(result.imageLocation, "url_location")
        assertEquals(result.imageStorage, "url_storage")
    }

    @Test
    fun testGetAppPermissionDataTHSuccess_EmptyData_ThenReturnEmpty() {
        val dataTh = mapOf(
            "button" to "",
            "image_location" to "",
            "image_storage" to ""
        )
        val dataEn = mapOf(
            "button" to "",
            "image_location" to "",
            "image_storage" to ""
        )
        val enable = mapOf("enable" to false)
        val data = mapOf(
            "enable" to enable,
            "th" to dataTh,
            "en" to dataEn
        )
        val response = mapOf("android" to data)
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
        whenever(initialAppConfigRepository.getConfigByKey(PERMISSION_UI_KEY)).thenReturn(response)
        val result = appPermissionGetConfigRepository.getAppPermissionData()
        verify(initialAppConfigRepository, times(1)).getConfigByKey(PERMISSION_UI_KEY)
        assertEquals(result.button, "")
        assertEquals(result.imageLocation, "")
        assertEquals(result.imageStorage, "")
    }

    @Test
    fun testGetAppPermissionDataENSuccess_Empty_ThenReturnEmpty() {
        val dataEn = mapOf(
            "button" to "",
            "image_location" to "",
            "image_storage" to ""
        )
        val enable = mapOf("enable" to false)
        val data = mapOf(
            "enable" to enable,
            "en" to dataEn
        )
        val response = mapOf("android" to data)
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("en")
        whenever(initialAppConfigRepository.getConfigByKey(PERMISSION_UI_KEY)).thenReturn(response)
        val result = appPermissionGetConfigRepository.getAppPermissionData()
        verify(initialAppConfigRepository, times(1)).getConfigByKey(PERMISSION_UI_KEY)
        assertEquals(result.button, "")
        assertEquals(result.imageLocation, "")
        assertEquals(result.imageStorage, "")
    }

    @Test
    fun testGetAppPermissionDataENSuccess_NullData_ThenReturnEmpty() {
        val dataEn = mapOf(
            "button" to null,
            "image_location" to null,
            "image_storage" to null
        )
        val enable = mapOf("enable" to false)
        val data = mapOf(
            "enable" to enable,
            "en" to dataEn
        )
        val response = mapOf("android" to data)
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("en")
        whenever(initialAppConfigRepository.getConfigByKey(PERMISSION_UI_KEY)).thenReturn(response)
        val result = appPermissionGetConfigRepository.getAppPermissionData()
        verify(initialAppConfigRepository, times(1)).getConfigByKey(PERMISSION_UI_KEY)
        assertEquals(result.button, "")
        assertEquals(result.imageLocation, "")
        assertEquals(result.imageStorage, "")
    }

    @Test
    fun testGetAppPermissionDataTHSuccess_NullData_ThenReturnEmpty() {
        val dataTh = mapOf(
            "button" to null,
            "image_location" to null,
            "image_storage" to null
        )
        val dataEn = mapOf(
            "button" to "",
            "image_location" to "",
            "image_storage" to ""
        )
        val enable = mapOf("enable" to false)
        val data = mapOf(
            "enable" to enable,
            "th" to dataTh,
            "en" to dataEn
        )
        val response = mapOf("android" to data)
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
        whenever(initialAppConfigRepository.getConfigByKey(PERMISSION_UI_KEY)).thenReturn(response)
        val result = appPermissionGetConfigRepository.getAppPermissionData()
        verify(initialAppConfigRepository, times(1)).getConfigByKey(PERMISSION_UI_KEY)
        assertEquals(result.button, "")
        assertEquals(result.imageLocation, "")
        assertEquals(result.imageStorage, "")
    }

    @Test
    fun testGetAppPermissionDataSuccess_NullData_ThenDoNothing() {
        val enable = mapOf("enable" to false)
        val data = mapOf(
            "enable" to enable,
            "th" to null,
            "en" to null
        )
        val response = mapOf("android" to data)
        whenever(initialAppConfigRepository.getConfigByKey(PERMISSION_UI_KEY)).thenReturn(response)
        appPermissionGetConfigRepository.getAppPermissionData()
        verify(initialAppConfigRepository, times(1)).getConfigByKey(PERMISSION_UI_KEY)
    }

    @Test
    fun testGetAppPermissionDataFailed_NullData_ThenDoNothing() {
        val response = mapOf("android" to null)
        whenever(initialAppConfigRepository.getConfigByKey(PERMISSION_UI_KEY)).thenReturn(response)
        appPermissionGetConfigRepository.getAppPermissionData()
        verify(initialAppConfigRepository, times(1)).getConfigByKey(PERMISSION_UI_KEY)
    }

    @Test
    fun testGetAppPermissionDataFailed_ThenReturnSaveDataEmpty() {
        whenever(initialAppConfigRepository.getConfigByKey(PERMISSION_UI_KEY)).thenReturn(null)
        whenever(sharedPrefsInterface.get(KEY_SAVE_BUTTON, "")).thenReturn("")
        whenever(sharedPrefsInterface.get(KEY_SAVE_IMAGE_STORAGE, "")).thenReturn("")
        whenever(sharedPrefsInterface.get(KEY_SAVE_IMAGE_LOCATION, "")).thenReturn("")

        val result = appPermissionGetConfigRepository.getAppPermissionData()
        verify(initialAppConfigRepository, times(1)).getConfigByKey(PERMISSION_UI_KEY)
        verify(sharedPrefsInterface, times(1)).get(KEY_SAVE_BUTTON, "")
        verify(sharedPrefsInterface, times(1)).get(KEY_SAVE_IMAGE_STORAGE, "")
        verify(sharedPrefsInterface, times(1)).get(KEY_SAVE_IMAGE_LOCATION, "")
        assertEquals(result.button, "")
        assertEquals(result.imageLocation, "")
        assertEquals(result.imageStorage, "")
    }

    @Test
    fun testGetAppPermissionDataFailed_ThenReturnSaveDataNull() {
        whenever(initialAppConfigRepository.getConfigByKey(PERMISSION_UI_KEY)).thenReturn(null)
        whenever(sharedPrefsInterface.get(KEY_SAVE_BUTTON, "")).thenReturn(null)
        whenever(sharedPrefsInterface.get(KEY_SAVE_IMAGE_STORAGE, "")).thenReturn(null)
        whenever(sharedPrefsInterface.get(KEY_SAVE_IMAGE_LOCATION, "")).thenReturn(null)

        val result = appPermissionGetConfigRepository.getAppPermissionData()
        verify(initialAppConfigRepository, times(1)).getConfigByKey(PERMISSION_UI_KEY)
        verify(sharedPrefsInterface, times(1)).get(KEY_SAVE_BUTTON, "")
        verify(sharedPrefsInterface, times(1)).get(KEY_SAVE_IMAGE_STORAGE, "")
        verify(sharedPrefsInterface, times(1)).get(KEY_SAVE_IMAGE_LOCATION, "")
        assertEquals(result.button, "")
        assertEquals(result.imageLocation, "")
        assertEquals(result.imageStorage, "")
    }

    @Test
    fun testGetAppPermissionDataFailed_ThenReturnSaveData() {
        whenever(initialAppConfigRepository.getConfigByKey(PERMISSION_UI_KEY)).thenReturn(null)
        whenever(sharedPrefsInterface.get(KEY_SAVE_BUTTON, "")).thenReturn("Continue")
        whenever(sharedPrefsInterface.get(KEY_SAVE_IMAGE_STORAGE, "")).thenReturn("url_storage")
        whenever(sharedPrefsInterface.get(KEY_SAVE_IMAGE_LOCATION, "")).thenReturn("url_location")

        val result = appPermissionGetConfigRepository.getAppPermissionData()
        verify(initialAppConfigRepository, times(1)).getConfigByKey(PERMISSION_UI_KEY)
        verify(sharedPrefsInterface, times(1)).get(KEY_SAVE_BUTTON, "")
        verify(sharedPrefsInterface, times(1)).get(KEY_SAVE_IMAGE_STORAGE, "")
        verify(sharedPrefsInterface, times(1)).get(KEY_SAVE_IMAGE_LOCATION, "")
        assertEquals(result.button, "Continue")
        assertEquals(result.imageLocation, "url_location")
        assertEquals(result.imageStorage, "url_storage")
    }
}
