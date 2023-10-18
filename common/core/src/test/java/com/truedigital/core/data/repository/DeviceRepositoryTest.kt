package com.truedigital.core.data.repository

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.truedigital.core.BuildConfig
import com.truedigital.core.provider.ContextDataProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals

class DeviceRepositoryTest {

    private val contextDataProvider: ContextDataProvider = mockk()
    private val telephonyManager: TelephonyManager = mockk()
    private lateinit var deviceRepository: DeviceRepository

    @BeforeEach
    fun setUp() {

        // Create DeviceRepositoryImpl instance
        deviceRepository = spyk(DeviceRepositoryImpl(contextDataProvider))

        // Mock contextDataProvider methods
        every { contextDataProvider.getDataContext() } returns mockk()

        // Mock telephonyManager methods
        every { telephonyManager.simOperatorName } returns "TestOperatorName"
    }

    @Ignore
    @Test
    fun `test getAndroidId with valid AndroidId Should Return AndroidId`() {
        // Given
        every { Settings.Secure.getString(any(), any()) } returns "TestAndroidId"

        // When
        val androidId = deviceRepository.getAndroidId()

        // Then
        assertEquals("TestAndroidId", androidId)
    }

    @Ignore
    @Test
    fun `test getAndroidId with invalid AndroidId should return GeneratedId`() {
        // Given
        every { Settings.Secure.getString(any(), any()) } returns null

        // When
        val androidId = deviceRepository.getAndroidId()

        // Then
        assertEquals("8-6-6-1-6-6", androidId)
    }

    @Test
    fun `test getAppVersion should return AppVersion`() {
        // When
        val appVersion = deviceRepository.getAppVersion()

        // Then
        assertEquals(BuildConfig.VERSION_NAME, appVersion)
    }

    @Test
    fun `test getAppVersion with versionCode should return AppVersion with versionCode`() {
        // Then
        val appVersionWithVersionCode = deviceRepository.getAppVersionWithVersionCode()

        // Then
        assertEquals(
            "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            appVersionWithVersionCode
        )
    }

    @Ignore
    @Test
    fun `test getDeviceName with Manufacturer and Model should return Capitalized Model`() {
        // Arrange
        val expectedManufacturer = "TestManufacturer"
        val expectedModel = "Test model"

        // Given
        mockkObject(CustomBuildObject)

        every { CustomBuildObject.getBuildManufacturer() } returns expectedManufacturer
        every { CustomBuildObject.getBuildModel() } returns expectedModel

        // When
        val deviceName = deviceRepository.getDeviceName()

        // Then
        assertEquals(expectedManufacturer + expectedModel, deviceName)
    }

    @Ignore
    @Test
    fun `test getDeviceName with Model Only should return Capitalized Model`() {
        // Arrange
        val expectedManufacturer = "TestManufacturer"
        val expectedModel = "Test model"

        // Given
        mockkObject(CustomBuildObject)

        every { CustomBuildObject.getBuildManufacturer() } returns "TestManufacturer"
        every { CustomBuildObject.getBuildModel() } returns "TestModel"

        // When
        val deviceName = deviceRepository.getDeviceName()

        // Then
    }

    @Test
    fun `test getEnvironment should return Environment`() {
        // When
        val environment = deviceRepository.getEnvironment()

        // Then
        assertEquals(BuildConfig.ENVIRONMENT, environment)
    }

    @Test
    fun `test getScreenSize`() {
        // Arrange
        val expectedScreenSize = "1080x1920"

        // Given
        every { contextDataProvider.getString(any()) } returns expectedScreenSize

        // When
        val result = deviceRepository.getScreenSize()

        // Then
        assertEquals(expectedScreenSize, result)
    }

    @Ignore
    @Test
    fun `test getModelName`() {
        // Arrange
        val expectedModel = "Test model"

        // Given
        mockkObject(CustomBuildObject)

        every { CustomBuildObject.getBuildModel() } returns expectedModel

        // When
        val result = deviceRepository.getModelName()

        // Then
        assertEquals(expectedModel, result)
    }

    @Ignore
    @Test
    fun `test getOSVersion`() {
        // Arrange
        val versionRelease = ""

        // Given
        mockkObject(CustomBuildObject)

        every { CustomBuildObject.getBuildVersionRelease() } returns versionRelease

        // When
        val result = deviceRepository.getOSVersion()

        // Then
        assertEquals(versionRelease, result)
    }

    @Test
    fun `test getSimOperatorName when simOperatorName is not blank`() {
        // Given
        every { contextDataProvider.getSystemService(Context.TELEPHONY_SERVICE) } returns telephonyManager
        every { telephonyManager.simOperatorName } returns "MyOperator"

        // When
        val result = deviceRepository.getSimOperatorName()

        // Then
        assertEquals("MyOperator", result)
    }

    @Test
    fun `test getSimOperatorName when simOperatorName is blank`() {
        // Given
        every { contextDataProvider.getSystemService(Context.TELEPHONY_SERVICE) } returns telephonyManager
        every { telephonyManager.simOperatorName } returns ""

        // When
        val result = deviceRepository.getSimOperatorName()

        // Then
        assertEquals("UNKNOWN", result)
    }

    @Ignore
    @Test
    fun `test getUserAgent`() {
        // Given
        mockkObject(CustomBuildObject)

        every { CustomBuildObject.getSdkInt() } returns 11

        every { deviceRepository.getModelName() } returns "MyModel"
        every { deviceRepository.getOSVersion() } returns "11"

        // When
        val result = deviceRepository.getUserAgent()

        // Then
        assertEquals("(MyDevice; MyModel; SDK ${Build.VERSION.SDK_INT}; Android 11)", result)
    }

    object CustomBuildObject {
        fun getBuildModel(): String {
            return Build.MODEL
        }

        fun getBuildManufacturer(): String {
            return Build.MANUFACTURER
        }

        fun getBuildVersionRelease(): String {
            return Build.VERSION.RELEASE
        }

        fun getSdkInt(): Int {
            return Build.VERSION.SDK_INT
        }
    }
}
