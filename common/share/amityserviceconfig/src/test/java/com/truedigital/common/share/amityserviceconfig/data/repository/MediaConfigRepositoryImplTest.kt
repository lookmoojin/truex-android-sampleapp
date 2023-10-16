package com.truedigital.common.share.amityserviceconfig.data.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.amityserviceconfig.domain.repository.MediaConfigRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import com.truedigital.share.mock.firebase.firestore.FirebaseFirestoreTest
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MediaConfigRepositoryImplTest : FirebaseFirestoreTest() {
    private val firestoreUtil: FirestoreUtil = mock()
    private lateinit var mediaConfigRepository: MediaConfigRepository

    private val countryCode = "th"

    @BeforeEach
    fun setUp() {
        whenever(firestoreUtil.getFirestore()).thenReturn(firebaseFirestore)
        mediaConfigRepository = MediaConfigRepositoryImpl(
            firestoreUtil = firestoreUtil
        )
    }

    @Test
    fun testGetConfig_whenMediaConfig_IsTrue() = runTest {
        getDataSuccess()

        val mockExpectedResponse = mapOf(
            "android" to true,
            "ios" to true,
        )
        val mockResponse = mapOf(
            "popular_feed" to mapOf(
                "enable" to mockExpectedResponse
            )
        )

        whenever(firestoreDocumentSnapshot.data).thenReturn(mockResponse)

        val actual =
            mediaConfigRepository.getFeatureConfigMediaConfig(countryCode).first()

        Assertions.assertFalse(actual)
    }

    @Test
    fun testGetConfig_whenPopularFeedConfig_getDataSuccessEmpty_androidIsFalse() = runTest {
        getDataSuccess()

        val mockExpectedResponse = mapOf(
            "ios" to true,
        )
        val mockResponse = mapOf(
            "popular_feed" to mapOf(
                "enable" to mockExpectedResponse
            )
        )

        whenever(firestoreDocumentSnapshot.data).thenReturn(mockResponse)

        val actual =
            mediaConfigRepository.getFeatureConfigMediaConfig(countryCode).first()

        Assertions.assertFalse(actual)
    }

    @Test
    fun testGivenOnFailure_whenGetConfig_ThenThrowException() = runTest {
        getDataFailure()

        mediaConfigRepository.getFeatureConfigMediaConfig(countryCode)
            .catch {
                Assertions.assertEquals("Failed", it.message)
            }.collectSafe { }
    }
}
