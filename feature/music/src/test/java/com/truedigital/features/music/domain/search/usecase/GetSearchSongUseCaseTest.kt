package com.truedigital.features.music.domain.search.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.search.model.response.Artist
import com.truedigital.features.music.data.search.model.response.Hit
import com.truedigital.features.music.data.search.model.response.Hits
import com.truedigital.features.music.data.search.model.response.Image
import com.truedigital.features.music.data.search.model.response.Meta
import com.truedigital.features.music.data.search.model.response.MusicSearchResponse
import com.truedigital.features.music.data.search.model.response.MusicSearchResponseItem
import com.truedigital.features.music.data.search.model.response.Results
import com.truedigital.features.music.data.search.model.response.Source
import com.truedigital.features.music.data.search.repository.MusicSearchRepository
import com.truedigital.features.music.domain.search.model.MusicSearchModel
import com.truedigital.features.music.domain.search.model.ThemeType
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class GetSearchSongUseCaseTest {
    private lateinit var getSearchSongUseCase: GetSearchSongUseCase
    private val musicSearchRepository: MusicSearchRepository = mock()

    @BeforeEach
    fun setup() {
        getSearchSongUseCase = GetSearchSongUseCaseImpl(
            musicSearchRepository = musicSearchRepository
        )
    }

    @Test
    fun getSongSearch_Success() = runTest {
        val response = flowOf(fakeResponseSuccess())
        whenever(musicSearchRepository.getSongQuery(any(), any(), any())).thenReturn(response)

        val flow =
            getSearchSongUseCase.execute(query = "query", theme = ThemeType.DARK, offset = "20")
        flow.collect { musicList ->

            val musicItemList = mutableListOf<MusicSearchModel>()
            for (item in musicList) {
                when (item) {
                    is MusicSearchModel.MusicItemModel -> {
                        musicItemList.add(item)
                    }
                    else -> {
                        // do nothing
                    }
                }
            }

            assertThat(
                (
                    musicItemList[0] as MusicSearchModel
                    .MusicItemModel
                    ).type.toString(),
                Is.`is`("SONG")
            )
            assertThat(
                (
                    musicItemList[0] as MusicSearchModel
                    .MusicItemModel
                    ).id,
                Is.`is`("1")
            )
            assertThat(
                (
                    musicItemList[0] as MusicSearchModel
                    .MusicItemModel
                    ).title,
                Is.`is`("title title 1")
            )
            assertThat(
                (
                    musicItemList[0] as MusicSearchModel
                    .MusicItemModel
                    ).description,
                Is.`is`("artist1 title 1")
            )
            assertThat(
                (
                    musicItemList[1] as MusicSearchModel
                    .MusicItemModel
                    ).title,
                Is.`is`("title1 title 1")
            )
            assertThat(
                (
                    musicItemList[1] as MusicSearchModel
                    .MusicItemModel
                    ).description,
                Is.`is`("artist2-1 title 1, artist2-2 title 1")
            )
        }
    }

    private fun fakeResponseSuccess(): MusicSearchResponse {
        return MusicSearchResponse().apply {
            this.add(fakeMusicSearchResponseItem())
        }
    }

    private fun fakeMusicSearchResponseItem(): MusicSearchResponseItem {
        return MusicSearchResponseItem(
            key = "song",
            results = Results(
                Hits(
                    hits = listOf(
                        Hit(
                            id = "1",
                            source = Source(
                                name = "title title 1",
                                artists = listOf(
                                    Artist(
                                        id = 1,
                                        name = "artist1 title 1"
                                    )
                                ),
                                meta = listOf(
                                    Meta(
                                        image = "image title 1",
                                        albumImage = ""
                                    )
                                ),
                                images = listOf(
                                    Image(
                                        "images title 1"
                                    )
                                )
                            )
                        ),
                        Hit(
                            id = "2",
                            source = Source(
                                name = "title1 title 1",
                                artists = listOf(
                                    Artist(
                                        id = 2,
                                        name = "artist2-1 title 1"
                                    ),
                                    Artist(
                                        id = 2,
                                        name = "artist2-2 title 1"
                                    )
                                ),
                                meta = listOf(
                                    Meta(
                                        image = "image2 title 1",
                                        albumImage = ""
                                    )
                                ),
                                images = listOf(
                                    Image(
                                        "images2 title 1"
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }
}
