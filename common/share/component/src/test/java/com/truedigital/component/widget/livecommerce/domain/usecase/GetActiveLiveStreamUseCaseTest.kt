package com.truedigital.component.widget.livecommerce.domain.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.component.widget.livecommerce.data.model.CommerceActiveLiveStreamData
import com.truedigital.component.widget.livecommerce.data.model.CommerceActiveLiveStreamResponseUsers
import com.truedigital.component.widget.livecommerce.data.model.CommerceActiveLiveStreamResponseVideoStreamings
import com.truedigital.component.widget.livecommerce.data.repository.GetActiveLiveStreamRepository
import com.truedigital.component.widget.livecommerce.domain.model.CommerceActiveLiveStreamModel
import com.truedigital.core.extensions.collectSafe
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class GetActiveLiveStreamUseCaseTest {

    private lateinit var getActiveLiveStreamUseCase: GetActiveLiveStreamUseCaseImpl
    private val getActiveLiveStreamRepository: GetActiveLiveStreamRepository = mock()

    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    @BeforeEach
    fun setup() {
        getActiveLiveStreamUseCase = GetActiveLiveStreamUseCaseImpl(
            getActiveLiveStreamRepository
        )
    }

    @Test
    fun `get live stream`() = runTest {
        val expectedResult = listOf<CommerceActiveLiveStreamModel>(
            CommerceActiveLiveStreamModel(
                streamIds = "",
                postId = "",
                thumbnailField = "https://api.sg.amity.co/api/v3/files//download?size=medium",
                displayName = "",
                title = "",
                description = "",
                profileImageUrl = ""
            )
        )

        whenever(getActiveLiveStreamRepository.execute(userIds = "ssoIdList")).thenReturn(
            flowOf(
                listOf(
                    CommerceActiveLiveStreamData(
                        users = arrayListOf(CommerceActiveLiveStreamResponseUsers()),
                        commerceVideoStreamings = arrayListOf(
                            CommerceActiveLiveStreamResponseVideoStreamings(
                                isLive = true,
                                status = "live"
                            )
                        )
                    ),
                    CommerceActiveLiveStreamData(
                        users = arrayListOf(CommerceActiveLiveStreamResponseUsers()),
                        commerceVideoStreamings = arrayListOf(
                            CommerceActiveLiveStreamResponseVideoStreamings(
                                isLive = false,
                                status = "lives"
                            )
                        )
                    )
                )
            )
        )

        getActiveLiveStreamUseCase.execute("ssoIdList").collectSafe {
            assertNotNull(it)
            assertEquals(it, expectedResult)
        }
    }

    @Test
    fun test_getHashOfUserId_happyCase() {
        val expectedResult = "849"
        val result = getActiveLiveStreamUseCase.getHashOfUserId("4696849")
        assertEquals(expectedResult, result)
    }

    @Test
    fun test_getHashOfUserId_errorCase() {
        val expectedResult = ""
        val result = getActiveLiveStreamUseCase.getHashOfUserId("")
        assertEquals(expectedResult, result)
    }

    @Test
    fun test_getEnvironment_dev() {
        assertEquals(getActiveLiveStreamUseCase.getEnvironment(), "stg-avatar")
    }

    @Test
    fun test_generateProfileImageUrl_happyCase() {
        assertEquals(
            getActiveLiveStreamUseCase.generateProfileImageUrl(
                "4696849"
            ),
            "https://stg-avatar.dmpcdn.com/p40x40/849/4696849.png"
        )
    }

    @Test
    fun test_generateProfileImageUrl_noUserId() {
        assertEquals(
            getActiveLiveStreamUseCase.generateProfileImageUrl(
                ""
            ),
            "https://stg-avatar.dmpcdn.com/p40x40//.png"
        )
    }
}
