package com.truedigital.features.music.domain.landing.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.music.data.landing.model.response.tagartist.TagArtistResponse
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetTagArtistShelfUseCaseTest {

    private lateinit var getTagArtistShelfUseCase: GetTagArtistShelfUseCase
    private val musicLandingRepository: MusicLandingRepository = mock()

    @BeforeEach
    fun setup() {
        getTagArtistShelfUseCase = GetTagArtistShelfUseCaseImpl(
            musicLandingRepository
        )
    }

    @Test
    fun `test load artist_by_tag success`() = runTest {
        val tagArtistResponseResultMock = TagArtistResponse.Result(
            artistId = 12345,
            image = "artist image url",
            name = "artist name"
        )
        val expectedDataMock = listOf(
            MusicForYouItemModel.ArtistShelfItem(
                artistId = 12345,
                coverImage = "artist image url",
                name = "artist name"
            )
        )

        whenever(musicLandingRepository.getTagArtist(any(), any(), any())).thenReturn(
            flowOf(
                TagArtistResponse(
                    10,
                    1,
                    listOf(tagArtistResponseResultMock),
                    10
                )
            )
        )

        getTagArtistShelfUseCase.execute("tag_artist", "10")
            .collectSafe {
                assertEquals(expectedDataMock, it)
            }
    }

    @Test
    fun `test load artist_by_tag success limit empty`() = runTest {
        val tagArtistResponseResultMock = TagArtistResponse.Result(
            artistId = 12345,
            image = "artist image url",
            name = "artist name"
        )
        val expectedDataMock = listOf(
            MusicForYouItemModel.ArtistShelfItem(
                artistId = 12345,
                coverImage = "artist image url",
                name = "artist name"
            )
        )

        whenever(musicLandingRepository.getTagArtist(any(), any(), any())).thenReturn(
            flowOf(
                TagArtistResponse(
                    10,
                    1,
                    listOf(tagArtistResponseResultMock),
                    10
                )
            )
        )

        getTagArtistShelfUseCase.execute("tag_artist", "")
            .collectSafe {
                assertEquals(expectedDataMock, it)
            }
        verify(musicLandingRepository, times(1)).getTagArtist("tag_artist", 1, 10)
    }

    @Test
    fun `test tag empty return_error`() = runTest {
        getTagArtistShelfUseCase.execute("", "10")
            .catch { exception ->
                assertEquals(
                    "tag for you shelf error is empty",
                    exception.message
                )
            }.collect()
    }
}
