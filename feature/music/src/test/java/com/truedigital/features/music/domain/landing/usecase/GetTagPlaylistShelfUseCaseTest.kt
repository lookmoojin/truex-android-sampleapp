package com.truedigital.features.music.domain.landing.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.music.data.landing.model.response.tagplaylist.TagPlaylistResponse
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetTagPlaylistShelfUseCaseTest {

    private lateinit var getTagPlaylistShelfUseCase: GetTagPlaylistShelfUseCase
    private val musicLandingRepository: MusicLandingRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()

    @BeforeEach
    fun setup() {
        getTagPlaylistShelfUseCase = GetTagPlaylistShelfUseCaseImpl(
            musicLandingRepository,
            localizationRepository
        )
    }

    @Test
    fun `test load playlist_by_tag success`() = runTest {
        val tagPlaylistResponseResultMock = TagPlaylistResponse.Result(
            coverImage = listOf(
                Translation(
                    language = "en",
                    value = "playlist by tag"
                )
            ),
            name = listOf(
                Translation(
                    language = "en",
                    value = "playlist name"
                )
            ),
            playlistId = 1234,
        )

        val expectedDataMock = listOf(
            MusicForYouItemModel.PlaylistShelfItem(
                playlistId = 1234,
                coverImage = "playlist by tag",
                name = "playlist name",
                nameEn = "playlist name"
            )
        )
        whenever(musicLandingRepository.getTagPlaylistShelf(any(), any(), any(), any())).thenReturn(
            flowOf(
                TagPlaylistResponse(
                    10,
                    1,
                    listOf(tagPlaylistResponseResultMock),
                    10
                )
            )
        )
        whenever(localizationRepository.getAppLanguageCodeForEnTh()).thenReturn("en")

        getTagPlaylistShelfUseCase.execute("tag", "10")
            .collectSafe {
                assertTrue {
                    it.isNotEmpty()
                }

                assertEquals(expectedDataMock, it)
            }
    }

    @Test
    fun `test load playlist_by_tag success limit is empty`() = runTest {
        val tagPlaylistResponseResultMock = TagPlaylistResponse.Result(
            coverImage = listOf(
                Translation(
                    language = "en",
                    value = "playlist by tag"
                )
            ),
            name = listOf(
                Translation(
                    language = "en",
                    value = "playlist name"
                )
            ),
            playlistId = 1234,
        )

        val expectedDataMock = listOf(
            MusicForYouItemModel.PlaylistShelfItem(
                playlistId = 1234,
                coverImage = "playlist by tag",
                name = "playlist name",
                nameEn = "playlist name"
            )
        )
        whenever(musicLandingRepository.getTagPlaylistShelf(any(), any(), any(), any())).thenReturn(
            flowOf(
                TagPlaylistResponse(
                    10,
                    1,
                    listOf(tagPlaylistResponseResultMock),
                    10
                )
            )
        )
        whenever(localizationRepository.getAppLanguageCodeForEnTh()).thenReturn("en")

        getTagPlaylistShelfUseCase.execute("tag", "")
            .collectSafe {
                assertTrue {
                    it.isNotEmpty()
                }
                assertEquals(expectedDataMock, it)
            }
        verify(musicLandingRepository, times(1)).getTagPlaylistShelf("tag", 1, 10, "all")
    }

    @Test
    fun `test load playlist_by_tag success but cannot find name by lang code`() = runTest {
        val tagPlaylistResponseResultMock = TagPlaylistResponse.Result(
            coverImage = listOf(
                Translation(
                    language = "en",
                    value = "playlist by tag"
                )
            ),
            name = listOf(
                Translation(
                    language = "th",
                    value = "playlist name th"
                )
            ),
            playlistId = 1234,
        )

        val expectedDataMock = listOf(
            MusicForYouItemModel.PlaylistShelfItem(
                playlistId = 1234,
                coverImage = "playlist by tag",
                name = "playlist name th",
                nameEn = null
            )
        )
        whenever(musicLandingRepository.getTagPlaylistShelf(any(), any(), any(), any())).thenReturn(
            flowOf(
                TagPlaylistResponse(
                    10,
                    1,
                    listOf(tagPlaylistResponseResultMock),
                    10
                )
            )
        )
        whenever(localizationRepository.getAppLanguageCodeForEnTh()).thenReturn("en")

        getTagPlaylistShelfUseCase.execute("tag", "10")
            .collectSafe {
                assertEquals(expectedDataMock, it)
            }
    }

    @Test
    fun `test tag empty return_error`() = runTest {
        getTagPlaylistShelfUseCase.execute("", "10")
            .catch { exception ->
                assertEquals(
                    "tag for you shelf error is empty",
                    exception.message
                )
            }.collect()
    }
}
