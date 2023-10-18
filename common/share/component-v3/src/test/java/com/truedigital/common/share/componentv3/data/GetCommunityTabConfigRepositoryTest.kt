package com.truedigital.common.share.componentv3.data

import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.componentv3.firebase.FirestoreTest
import com.truedigital.common.share.componentv3.widget.feedmenutab.data.GetCommunityTabConfigRepository
import com.truedigital.common.share.componentv3.widget.feedmenutab.data.GetCommunityTabConfigRepositoryImpl
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(InstantTaskExecutorExtension::class)
class GetCommunityTabConfigRepositoryTest : FirestoreTest() {

    private lateinit var getCommunityTabConfigRepository: GetCommunityTabConfigRepository

    @RegisterExtension
    @JvmField
    val coroutinesExtension = TestCoroutinesExtension()

    @BeforeEach
    fun setUp() {
        getCommunityTabConfigRepository = GetCommunityTabConfigRepositoryImpl(firestoreUtil)
        setUpFirestoreTest()
    }

    @Test
    fun testGetCategoriesConfig_LoadConfig_Success() = runTest {
        getDataSuccess()

        val childMap = mapOf(
            "community_tab" to mapOf(
                "enable" to mapOf(
                    "android" to true
                ),

                "title" to mapOf(
                    "en" to "Top Result",
                    "th" to "ยอดนิยม",
                    "my" to "my"
                )
            ),
            "foryou_tab" to mapOf(
                "title" to mapOf(
                    "en" to "For You",
                    "th" to "สำหรับข่อย",
                    "my" to "For You"
                )
            )
        )

        whenever(firestoreDocumentSnapshot.data).thenReturn(childMap)
        val result = getCommunityTabConfigRepository.getCommunityTabConfig("th")
        assertNotNull(result)
        assertEquals("ยอดนิยม", result.communityTab?.title?.th)
        assertEquals("Top Result", result.communityTab?.title?.en)
        assertTrue(result.communityTab?.enable?.isEnable == true)
        assertEquals("For You", result.forYouTab?.title?.en)
        assertEquals("สำหรับข่อย", result.forYouTab?.title?.th)
        assertTrue(result.forYouTab?.enable == null)
    }

    @Test
    fun testGetCategoriesConfig_LoadConfig_fail() = runTest {
        getDataSuccess()

        val childMap = mapOf(
            "community_tab" to listOf(
                mapOf(
                    "enable" to mapOf(
                        "android" to true
                    ),

                    "title" to mapOf(
                        "en" to "Top Result",
                        "th" to "ยอดนิยม",
                        "my" to "my",
                    )
                )
            ),
            "foryou_tab" to mapOf(
                "title" to mapOf(
                    "en" to "For You",
                    "th" to "สำหรับข่อย",
                    "my" to "For You"
                )
            )
        )

        whenever(firestoreDocumentSnapshot.data).thenReturn(childMap)
        val result = getCommunityTabConfigRepository.getCommunityTabConfig("th")
        assertNull(result)
    }
}
