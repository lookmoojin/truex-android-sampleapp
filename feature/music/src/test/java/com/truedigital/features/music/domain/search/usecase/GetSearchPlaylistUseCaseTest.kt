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
import com.truedigital.features.music.domain.search.model.MusicType
import com.truedigital.features.music.domain.search.model.ThemeType
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetSearchPlaylistUseCaseTest {
    private lateinit var getSearchPlaylistUseCase: GetSearchPlaylistUseCase
    private val musicSearchRepository: MusicSearchRepository = mock()

    @BeforeEach
    fun setUp() {
        getSearchPlaylistUseCase = GetSearchPlaylistUseCaseImpl(musicSearchRepository)
    }

    @Test
    fun `test GetSearchPlaylistUseCaseTest success should return data`() = runTest {
        val response = flowOf(fakeResponseSuccess())

        whenever(musicSearchRepository.getSearchPlaylistQuery(any(), any(), any()))
            .thenReturn(response)

        val flow = getSearchPlaylistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { musicList ->
            assertThat(musicList.size, CoreMatchers.`is`(8))
            assertThat(
                (musicList[0] as MusicSearchModel.MusicItemModel).id,
                CoreMatchers.`is`("1")
            )
            assertThat(
                (musicList[0] as MusicSearchModel.MusicItemModel).title,
                CoreMatchers.`is`("title playlist 1")
            )
            assertThat(
                (musicList[0] as MusicSearchModel.MusicItemModel).description,
                CoreMatchers.`is`("")
            )
            assertThat(
                (musicList[0] as MusicSearchModel.MusicItemModel).thumb,
                CoreMatchers.`is`("images1 playlist 1")
            )
            assertThat(
                (musicList[0] as MusicSearchModel.MusicItemModel).type,
                CoreMatchers.`is`(MusicType.PLAYLIST)
            )
            assertThat(
                (musicList[0] as MusicSearchModel.MusicItemModel).musicTheme?.type,
                CoreMatchers.`is`(ThemeType.DARK)
            )
        }
    }

    @Test
    fun `test GetSearchPlaylistUseCaseTest should return error`() = runTest {
        whenever(musicSearchRepository.getSearchPlaylistQuery(any(), any(), any()))
            .thenReturn(
                flow {
                    error(Throwable("error API"))
                }
            )

        val flow = getSearchPlaylistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.catch {
            assertThat(
                it.localizedMessage,
                CoreMatchers.`is`("java.lang.Throwable: error API")
            )
        }.collect()
    }

    @Test
    fun `test GetSearchPlaylistUseCaseTest should return empty`() = runTest {
        val mockResponse = flowOf(MusicSearchResponse())

        whenever(musicSearchRepository.getSearchPlaylistQuery(any(), any(), any()))
            .thenReturn(mockResponse)

        val flow = getSearchPlaylistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { item ->
            assertThat(item.size, CoreMatchers.`is`(0))
        }
    }

    @Test
    fun testGetSearchPlaylistSuccess_responseNull_shouldReturnEmptyList() = runTest {
        val response = flowOf(null)
        whenever(musicSearchRepository.getSearchPlaylistQuery(any(), any(), any()))
            .thenReturn(response)

        val flow = getSearchPlaylistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { item ->
            assertThat(item.size, CoreMatchers.`is`(0))
        }
    }

    @Test
    fun testGetSearchPlaylistSuccess_resultsNull_shouldReturnEmptyList() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(MusicSearchResponseItem(results = null))
            }
        )

        whenever(musicSearchRepository.getSearchPlaylistQuery(any(), any(), any()))
            .thenReturn(response)

        val flow = getSearchPlaylistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { item ->
            assertThat(item.size, CoreMatchers.`is`(0))
        }
    }

    @Test
    fun testGetSearchPlaylistSuccess_resultsHitsNull_shouldReturnEmptyList() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(MusicSearchResponseItem(results = Results(hits = null)))
            }
        )

        whenever(musicSearchRepository.getSearchPlaylistQuery(any(), any(), any()))
            .thenReturn(response)

        val flow = getSearchPlaylistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { item ->
            assertThat(item.size, CoreMatchers.`is`(0))
        }
    }

    @Test
    fun testGetSearchPlaylistSuccess_resultsHitsHitsListNull_shouldReturnEmptyList() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(MusicSearchResponseItem(results = Results(hits = Hits(hits = null))))
            }
        )

        whenever(musicSearchRepository.getSearchPlaylistQuery(any(), any(), any()))
            .thenReturn(response)

        val flow = getSearchPlaylistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { item ->
            assertThat(item.size, CoreMatchers.`is`(0))
        }
    }

    @Test
    fun testGetSearchPlaylistSuccess_resultsHitsHitsListEmpty_shouldReturnEmptyList() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(MusicSearchResponseItem(results = Results(hits = Hits(hits = emptyList()))))
            }
        )

        whenever(musicSearchRepository.getSearchPlaylistQuery(any(), any(), any()))
            .thenReturn(response)

        val flow = getSearchPlaylistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { item ->
            assertThat(item.size, CoreMatchers.`is`(0))
        }
    }

    @Test
    fun testGetSearchPlaylistSuccess_sourceNull_shouldTitleIsNullAndThumbIsEmpty() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(
                    MusicSearchResponseItem(
                        results = Results(
                            hits = Hits(
                                hits = listOf(
                                    Hit(
                                        id = "1",
                                        source = null
                                    )
                                )
                            )
                        )
                    )
                )
            }
        )

        whenever(musicSearchRepository.getSearchPlaylistQuery(any(), any(), any()))
            .thenReturn(response)

        val flow = getSearchPlaylistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { musicList ->
            assertThat(musicList.size, CoreMatchers.`is`(1))
            assertThat(
                (musicList[0] as MusicSearchModel.MusicItemModel).title,
                CoreMatchers.`is`(nullValue())
            )
            assertThat(
                (musicList[0] as MusicSearchModel.MusicItemModel).thumb,
                CoreMatchers.`is`("")
            )
        }
    }

    @Test
    fun testGetSearchPlaylistSuccess_imagesListNull_shouldThumbIsEmpty() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(
                    MusicSearchResponseItem(
                        results = Results(
                            hits = Hits(
                                hits = listOf(Hit(id = "1", source = Source(images = null)))
                            )
                        )
                    )
                )
            }
        )

        whenever(musicSearchRepository.getSearchPlaylistQuery(any(), any(), any()))
            .thenReturn(response)

        val flow = getSearchPlaylistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { musicList ->
            assertThat(musicList.size, CoreMatchers.`is`(1))
            assertThat(
                (musicList[0] as MusicSearchModel.MusicItemModel).thumb,
                CoreMatchers.`is`("")
            )
        }
    }

    @Test
    fun testGetSearchPlaylistSuccess_imagesListIsEmpty_shouldThumbIsEmpty() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(
                    MusicSearchResponseItem(
                        results = Results(
                            hits = Hits(
                                hits = listOf(Hit(id = "1", source = Source(images = listOf())))
                            )
                        )
                    )
                )
            }
        )

        whenever(musicSearchRepository.getSearchPlaylistQuery(any(), any(), any()))
            .thenReturn(response)

        val flow = getSearchPlaylistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { musicList ->
            assertThat(musicList.size, CoreMatchers.`is`(1))
            assertThat(
                (musicList[0] as MusicSearchModel.MusicItemModel).thumb,
                CoreMatchers.`is`("")
            )
        }
    }

    private fun fakeResponseSuccess(): MusicSearchResponse {
        return MusicSearchResponse().apply {
            this.add(fakeMusicSearchResponseItem("playlist", "1", "playlist 1"))
            this.add(fakeMusicSearchResponseItem("playlist", "2", "playlist 2"))
            this.add(fakeMusicSearchResponseItem("playlist", "3", "playlist 3"))
            this.add(fakeMusicSearchResponseItem("playlist", "4", "playlist 4"))
        }
    }

    private fun fakeMusicSearchResponseItem(
        key: String,
        id: String,
        name: String
    ): MusicSearchResponseItem {
        return MusicSearchResponseItem(
            key = key,
            results = Results(
                Hits(
                    hits = listOf(
                        Hit(
                            id = id,
                            source = Source(
                                artists = listOf(
                                    Artist(id = 1, name = "artist $name")
                                ),
                                meta = listOf(
                                    Meta(
                                        image = "image $name",
                                        albumImage = ""
                                    )
                                ),
                                name = "title $name",
                                images = listOf(
                                    Image("images1 $name"),
                                    Image("images2 $name")
                                )
                            )
                        ),
                        Hit(
                            id = id,
                            source = Source(
                                artists = listOf(
                                    Artist(id = 1, name = "artist2 $name")
                                ),
                                meta = listOf(
                                    Meta(
                                        image = "image2 $name",
                                        albumImage = ""
                                    )
                                ),
                                name = "title2 $name",
                                images = listOf(
                                    Image("images2 $name")
                                )
                            )
                        )
                    )
                )
            )
        )
    }
}
