package com.truedigital.common.share.amityserviceconfig.data.repository

import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.amityserviceconfig.domain.repository.CommunityGetRegexRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import com.truedigital.share.mock.firebase.firestore.FirebaseFirestoreTest
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CommunityGetRegexRepositoryImplTest : FirebaseFirestoreTest() {

    private val firestoreUtil: FirestoreUtil = mock()
    private lateinit var communityGetRegexRepository: CommunityGetRegexRepository

    private val countryCode = "th"

    @BeforeEach
    fun setUp() {
        whenever(firestoreUtil.getFirestore()).thenReturn(firebaseFirestore)
        communityGetRegexRepository = CommunityGetRegexRepositoryImpl(
            firestoreUtil = firestoreUtil
        )
    }

    @Test
    fun `Get config when CommunityGetRegex is opened`() = runTest {
        getDataSuccess()
        val mockResponse = mapOf(
            "masking_mobile_number" to mapOf(
                "android" to true,
                "ios" to true,
                "community_feature" to arrayOf(
                    "livestream",
                    "comment",
                    "post"
                )
            )
        )
        val mockExpectedResponse = mapOf(
            "android" to true,
            "ios" to true,
            "community_feature" to arrayOf(
                "livestream",
                "comment",
                "post"
            )
        )
        whenever(firestoreDocumentSnapshot.data).thenReturn(mockResponse)

        val actual =
            communityGetRegexRepository.getFeatureConfigRegex(countryCode).first()
        val expected = Gson().toJson(mockExpectedResponse)

        assertEquals(expected, actual)
    }

    @Test
    fun `Given onFailure When getConfig Then throw exception`() = runTest {
        getDataFailure()

        communityGetRegexRepository.getFeatureConfigRegex(countryCode)
            .catch {
                assertEquals("Failed", it.message)
            }.collectSafe { }
    }
}
