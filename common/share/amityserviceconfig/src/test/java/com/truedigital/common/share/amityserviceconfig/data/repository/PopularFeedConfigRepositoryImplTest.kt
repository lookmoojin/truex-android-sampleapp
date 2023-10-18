package com.truedigital.common.share.amityserviceconfig.data.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.amityserviceconfig.domain.repository.PopularFeedConfigRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import com.truedigital.share.mock.firebase.firestore.FirebaseFirestoreTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PopularFeedConfigRepositoryImplTest : FirebaseFirestoreTest() {
    private val firestoreUtil: FirestoreUtil = mock()
    private lateinit var popularFeedConfigRepository: PopularFeedConfigRepository

    private val countryCode = "th"

    @BeforeEach
    fun setUp() {
        whenever(firestoreUtil.getFirestore()).thenReturn(firebaseFirestore)
        popularFeedConfigRepository = PopularFeedConfigRepositoryImpl(
            firestoreUtil = firestoreUtil
        )
    }

    @Test
    fun `Get config when PopularFeedConfig is true`() = runTest {
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
            popularFeedConfigRepository.getFeatureConfigPopularFeed(countryCode).first()

        assertEquals(true, actual)
    }

    @Test
    fun `Get config when PopularFeedConfig getDataSuccess empty android is false`() = runTest {
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
            popularFeedConfigRepository.getFeatureConfigPopularFeed(countryCode).first()

        assertEquals(false, actual)
    }

    @Test
    fun `Given onFailure When getConfig Then throw exception`() = runTest {
        getDataFailure()

        popularFeedConfigRepository.getFeatureConfigPopularFeed(countryCode)
            .catch {
                assertEquals("Failed", it.message)
            }.collectSafe { }
    }
}
