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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetSearchArtistUseCaseTest {
    private lateinit var getSearchArtistUseCase: GetSearchArtistUseCase
    private val musicSearchRepository: MusicSearchRepository = mock()

    @BeforeEach
    fun setUp() {
        getSearchArtistUseCase = GetSearchArtistUseCaseImpl(musicSearchRepository)
    }

    @Test
    fun `test GetSearchArtistUseCase success should return data`() = runTest {
        val response = flowOf(fakeResponseSuccess())

        whenever(musicSearchRepository.getSearchArtistQuery(any(), any(), any())).thenReturn(
            response
        )

        val flow = getSearchArtistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )

        flow.collect { itemList ->
            assertThat(itemList.size, `is`(8))
            assertThat((itemList[0] as MusicSearchModel.MusicItemModel).id, `is`("1"))
            assertThat(
                (itemList[0] as MusicSearchModel.MusicItemModel).title,
                `is`("title artist 1")
            )
            assertThat(
                (itemList[0] as MusicSearchModel.MusicItemModel).description,
                `is`("")
            )
            assertThat(
                (itemList[0] as MusicSearchModel.MusicItemModel).thumb,
                `is`("image artist 1")
            )
            assertThat(
                (itemList[0] as MusicSearchModel.MusicItemModel).type,
                `is`(MusicType.ARTIST)
            )
            assertThat(
                (itemList[0] as MusicSearchModel.MusicItemModel).musicTheme?.type,
                `is`(ThemeType.DARK)
            )
        }
    }

    @Test
    fun `test GetSearchArtistUseCaseTest should return error`() = runTest {

        whenever(musicSearchRepository.getSearchArtistQuery(any(), any(), any()))
            .thenReturn(
                flow {
                    error(Throwable("error API"))
                }
            )

        val flow = getSearchArtistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )

        flow.catch {
            assertThat(it.localizedMessage, `is`("java.lang.Throwable: error API"))
        }
    }

    @Test
    fun testGetSearchArtistUseCaseSuccess_responseNull_shouldReturnEmptyList() = runTest {
        whenever(musicSearchRepository.getSearchArtistQuery(any(), any(), any())).thenReturn(
            flowOf(null)
        )

        val flow = getSearchArtistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { itemList ->
            assertThat(itemList.size, `is`(0))
        }
    }

    @Test
    fun testGetSearchArtistUseCaseSuccess_resultsNull_shouldReturnEmptyList() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(MusicSearchResponseItem(results = null))
            }
        )

        whenever(musicSearchRepository.getSearchArtistQuery(any(), any(), any())).thenReturn(
            response
        )

        val flow = getSearchArtistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { itemList ->
            assertThat(itemList.size, `is`(0))
        }
    }

    @Test
    fun testGetSearchArtistUseCaseSuccess_resultsHitsNull_shouldReturnEmptyList() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(MusicSearchResponseItem(results = Results(hits = null)))
            }
        )

        whenever(musicSearchRepository.getSearchArtistQuery(any(), any(), any())).thenReturn(
            response
        )

        val flow = getSearchArtistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { itemList ->
            assertThat(itemList.size, `is`(0))
        }
    }

    @Test
    fun testGetSearchArtistUseCaseSuccess_resultsHitsHitsListNull_shouldReturnEmptyList() =
        runTest {
            val response = flowOf(
                MusicSearchResponse().apply {
                    this.add(MusicSearchResponseItem(results = Results(hits = Hits(hits = null))))
                }
            )

            whenever(musicSearchRepository.getSearchArtistQuery(any(), any(), any())).thenReturn(
                response
            )

            val flow = getSearchArtistUseCase.execute(
                query = "hello",
                theme = ThemeType.DARK,
                offset = "20"
            )
            flow.collect { itemList ->
                assertThat(itemList.size, `is`(0))
            }
        }

    @Test
    fun testGetSearchArtistUseCaseSuccess_resultsHitsHitsListEmpty_shouldReturnEmptyList() =
        runTest {
            val response = flowOf(
                MusicSearchResponse().apply {
                    this.add(MusicSearchResponseItem(results = Results(hits = Hits(hits = emptyList()))))
                }
            )

            whenever(musicSearchRepository.getSearchArtistQuery(any(), any(), any())).thenReturn(
                response
            )
            val flow = getSearchArtistUseCase.execute(
                query = "hello",
                theme = ThemeType.DARK,
                offset = "20"
            )
            flow.collect { itemList ->
                assertThat(itemList.size, `is`(0))
            }
        }

    @Test
    fun testGetSearchArtistUseCaseSuccess_sourceNull_shouldTitleIsNullAndThumbIsEmpty() = runTest {
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

        whenever(musicSearchRepository.getSearchArtistQuery(any(), any(), any())).thenReturn(
            response
        )

        val flow = getSearchArtistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { itemList ->
            assertThat(itemList.size, `is`(1))
            assertThat(
                (itemList[0] as MusicSearchModel.MusicItemModel).title,
                `is`(nullValue())
            )
            assertThat((itemList[0] as MusicSearchModel.MusicItemModel).thumb, `is`(""))
        }
    }

    @Test
    fun testGetSearchArtistUseCaseSuccess_metaNull_shouldThumbIsEmpty() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(
                    MusicSearchResponseItem(
                        results = Results(
                            hits = Hits(
                                hits = listOf(Hit(id = "1", source = Source(meta = null)))
                            )
                        )
                    )
                )
            }
        )

        whenever(musicSearchRepository.getSearchArtistQuery(any(), any(), any())).thenReturn(
            response
        )

        val flow = getSearchArtistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { itemList ->
            assertThat(itemList.size, `is`(1))
            assertThat((itemList[0] as MusicSearchModel.MusicItemModel).thumb, `is`(""))
        }
    }

    @Test
    fun testGetSearchArtistUseCaseSuccess_metaEmptyList_shouldThumbIsEmpty() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(
                    MusicSearchResponseItem(
                        results = Results(
                            hits = Hits(
                                hits = listOf(
                                    Hit(
                                        id = "1",
                                        source = Source(meta = emptyList())
                                    )
                                )
                            )
                        )
                    )
                )
            }
        )

        whenever(musicSearchRepository.getSearchArtistQuery(any(), any(), any())).thenReturn(
            response
        )

        val flow = getSearchArtistUseCase.execute(
            query = "hello",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { itemList ->
            assertThat(itemList.size, `is`(1))
            assertThat((itemList[0] as MusicSearchModel.MusicItemModel).thumb, `is`(""))
        }
    }

    private fun fakeResponseSuccess(): MusicSearchResponse {
        return MusicSearchResponse().apply {
            this.add(fakeMusicSearchResponseItem("artist", "1", "artist 1"))
            this.add(fakeMusicSearchResponseItem("artist", "2", "artist 2"))
            this.add(fakeMusicSearchResponseItem("artist", "3", "artist 3"))
            this.add(fakeMusicSearchResponseItem("artist", "4", "artist 4"))
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
                                    Artist(
                                        id = 1,
                                        name = "artist $name"
                                    )
                                ),
                                meta = listOf(
                                    Meta(
                                        image = "image $name",
                                        albumImage = ""
                                    )
                                ),
                                name = "title $name",
                                images = listOf(
                                    Image(
                                        "images $name"
                                    )
                                )
                            )
                        ),
                        Hit(
                            id = id,
                            source = Source(
                                artists = listOf(
                                    Artist(
                                        id = 1,
                                        name = "artist2 $name"
                                    )
                                ),
                                meta = listOf(
                                    Meta(
                                        image = "image2 $name",
                                        albumImage = ""
                                    )
                                ),
                                name = "title2 $name",
                                images = listOf(
                                    Image(
                                        "images2 $name"
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
