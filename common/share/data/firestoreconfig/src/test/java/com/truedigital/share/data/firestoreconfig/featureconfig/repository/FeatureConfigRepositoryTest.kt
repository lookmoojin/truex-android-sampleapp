package com.truedigital.share.data.firestoreconfig.featureconfig.repository

import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.share.data.firestoreconfig.FirestoreTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FeatureConfigRepositoryTest : FirestoreTest() {
    private lateinit var featureConfigRepository: FeatureConfigRepository

    @BeforeEach
    fun setUp() {
        featureConfigRepository = FeatureConfigRepositoryImpl(firestoreUtil)
        setUpFirestoreTest()
    }

    @Test
    fun `test get firestore success`() {
        getDataSuccess()
        val customerScoreResponse = mapOf(
            "default_collapse" to "true",
            "enable_customer_score" to "true",
            "link" to "https://tmn-dev.test-app.link/paylater",
            "score" to "0",
            "scoreshelf_id" to "v2ggv3ZwxVB2"
        )
        whenever(firestoreDocumentSnapshot.data).thenReturn(customerScoreResponse)

        featureConfigRepository.loadInitialAppConfig(
            countryCode = "TH"
        )

        val responseA = featureConfigRepository.getConfigByKey("default_collapse")
        assertNotNull(responseA)
        assertEquals(responseA, "true")

        val responseB = featureConfigRepository.getConfigByKey("ABC")
        assertNull(responseB)
    }

    @Test
    fun `test get firebase fail`() {
        getDataFail()

        featureConfigRepository.loadInitialAppConfig(
            countryCode = "TH"
        )

        val responseA = featureConfigRepository.getConfigByKey("default_collapse")
        assertNull(responseA)
    }
}
