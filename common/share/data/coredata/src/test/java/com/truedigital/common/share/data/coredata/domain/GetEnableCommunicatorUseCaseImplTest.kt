package com.truedigital.common.share.data.coredata.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.data.FirestoreTest
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(InstantTaskExecutorExtension::class)
internal class GetEnableCommunicatorUseCaseImplTest : FirestoreTest() {

    private lateinit var getEnableCommunicatorUseCase: GetEnableCommunicatorUseCase
    private val localizationRepository: LocalizationRepository = mock()

    @BeforeEach
    fun setup() {
        getEnableCommunicatorUseCase = GetEnableCommunicatorUseCaseImpl(
            fireStoreUtil = firestoreUtil,
            localizationRepository = localizationRepository
        )
        setUpFirestoreTest()
    }

    @Test
    fun `when configMap has value thai and enable of android is true then execute should return true`() {
        // given
        getDataSuccess()

        val configMap = mapOf(
            "communicator" to mapOf(
                "enable" to mapOf(
                    "android" to true,
                    "image" to "mockImage",
                    "error_msg1_en" to "mockErrorEN1",
                    "error_msg1_local" to "mockErrorTH1",
                    "error_msg2_en" to "mockErrorEN2",
                    "error_msg2_local" to "mockErrorTH2",
                    "error_msg3_en" to "mockErrorEN3",
                    "error_msg3_local" to "mockErrorTH3",
                )
            )
        )
        whenever(firestoreDocumentSnapshot.data).thenReturn(configMap)
        whenever(localizationRepository.getAppCountryCode()).thenReturn("TH")

        // when
        val config = getEnableCommunicatorUseCase.execute()

        val resultConfig = config.blockingGet()
        // then
        assertTrue(resultConfig.isEnable)
        assertEquals(resultConfig.image, "mockImage")
        assertEquals(resultConfig.errorMessage1, "mockErrorTH1")
        assertEquals(resultConfig.errorMessage2, "mockErrorTH2")
        assertEquals(resultConfig.errorMessage3, "mockErrorTH3")
    }

    @Test
    fun `when configMap has value english and enable of android is true then execute should return true`() {
        // given
        getDataSuccess()

        val configMap = mapOf(
            "communicator" to mapOf(
                "enable" to mapOf(
                    "android" to true,
                    "image" to "mockImage",
                    "error_msg1_en" to "mockErrorEN1",
                    "error_msg1_local" to "mockErrorTH1",
                    "error_msg2_en" to "mockErrorEN2",
                    "error_msg2_local" to "mockErrorTH2",
                    "error_msg3_en" to "mockErrorEN3",
                    "error_msg3_local" to "mockErrorTH3",
                )
            )
        )
        whenever(firestoreDocumentSnapshot.data).thenReturn(configMap)
        whenever(localizationRepository.getAppCountryCode()).thenReturn("en")
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("en")

        // when
        val config = getEnableCommunicatorUseCase.execute()

        val resultConfig = config.blockingGet()
        // then
        assertTrue(resultConfig.isEnable)
        assertEquals(resultConfig.image, "mockImage")
        assertEquals(resultConfig.errorMessage1, "mockErrorEN1")
        assertEquals(resultConfig.errorMessage2, "mockErrorEN2")
        assertEquals(resultConfig.errorMessage3, "mockErrorEN3")
    }

    @Test
    fun `when configMap has value myanmar and enable of android is true then execute should return true`() {
        // given
        getDataSuccess()

        val configMap = mapOf(
            "communicator" to mapOf(
                "enable" to mapOf(
                    "android" to true,
                    "image" to "mockImage",
                    "error_msg1_en" to "mockErrorEN1",
                    "error_msg1_local" to "mockErrorTH1",
                    "error_msg2_en" to "mockErrorEN2",
                    "error_msg2_local" to "mockErrorTH2",
                    "error_msg3_en" to "mockErrorEN3",
                    "error_msg3_local" to "mockErrorTH3",
                )
            )
        )
        whenever(firestoreDocumentSnapshot.data).thenReturn(configMap)
        whenever(localizationRepository.getAppCountryCode()).thenReturn("my")
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("my")

        // when
        val config = getEnableCommunicatorUseCase.execute()

        val resultConfig = config.blockingGet()
        // then
        assertTrue(resultConfig.isEnable)
        assertEquals(resultConfig.image, "mockImage")
        assertEquals(resultConfig.errorMessage1, "mockErrorEN1")
        assertEquals(resultConfig.errorMessage2, "mockErrorEN2")
        assertEquals(resultConfig.errorMessage3, "mockErrorEN3")
    }

    @Test
    fun `when configMap has no other value and enable of android is false then execute should return false`() {
        // given
        getDataSuccess()

        val configMap = mapOf("communicator" to mapOf("enable" to mapOf("android" to false)))
        whenever(firestoreDocumentSnapshot.data).thenReturn(configMap)
        whenever(localizationRepository.getAppCountryCode()).thenReturn("TH")

        // when
        val config = getEnableCommunicatorUseCase.execute()

        val resultConfig = config.blockingGet()
        // then
        assertFalse(resultConfig.isEnable)
        assertEquals(resultConfig.image, "")
        assertEquals(resultConfig.errorMessage1, "")
        assertEquals(resultConfig.errorMessage2, "")
        assertEquals(resultConfig.errorMessage3, "")
    }

    @Test
    fun `when configMap doesn't have value then execute should return false`() {
        // given
        getDataSuccess()

        val configMap = mapOf("communicator" to null)
        whenever(firestoreDocumentSnapshot.data).thenReturn(configMap)
        whenever(localizationRepository.getAppCountryCode()).thenReturn("TH")

        // when
        val config = getEnableCommunicatorUseCase.execute()

        val resultConfig = config.blockingGet()
        // then
        assertFalse(resultConfig.isEnable)
        assertEquals(resultConfig.image, "")
        assertEquals(resultConfig.errorMessage1, "")
        assertEquals(resultConfig.errorMessage2, "")
        assertEquals(resultConfig.errorMessage3, "")
    }

    @Test
    fun `when get firestore fail then execute should return false`() {
        // given
        whenever(localizationRepository.getAppCountryCode()).thenReturn("TH")
        getDataFail()

        // when
        val config = getEnableCommunicatorUseCase.execute()

        val resultConfig = config.blockingGet()
        // then
        assertFalse(resultConfig.isEnable)
        assertEquals(resultConfig.image, "")
        assertEquals(resultConfig.errorMessage1, "")
        assertEquals(resultConfig.errorMessage2, "")
        assertEquals(resultConfig.errorMessage3, "")
    }
}
