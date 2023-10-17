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
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class GetSearchAlbumUseCaseTest {

    private lateinit var getSearchAlbumUseCase: GetSearchAlbumUseCase
    private val musicSearchRepository: MusicSearchRepository = mock()

    @BeforeEach
    fun setup() {
        getSearchAlbumUseCase = GetSearchAlbumUseCaseImpl(musicSearchRepository)
    }

    @Test
    fun getAlbumSearchSuccess_returnList() = runTest {
        val response = flowOf(fakeResponseSuccess())
        whenever(musicSearchRepository.getAlbumQuery(any(), any(), any())).thenReturn(response)

        val flow = getSearchAlbumUseCase.execute(
            query = "query",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { itemList ->
            val firstItem = itemList[0] as MusicSearchModel.MusicItemModel
            MatcherAssert.assertThat(firstItem.type, `is`(MusicType.ALBUM))
            MatcherAssert.assertThat(firstItem.id, `is`("1"))
            MatcherAssert.assertThat(firstItem.title, `is`("title album 1"))
            MatcherAssert.assertThat(firstItem.description, `is`("artist1 album 1"))
            MatcherAssert.assertThat(firstItem.thumb, `is`("image album 1"))
            MatcherAssert.assertThat(firstItem.musicTheme?.type, `is`(ThemeType.DARK))

            val secondItem = itemList[1] as MusicSearchModel.MusicItemModel
            MatcherAssert.assertThat(secondItem.type, `is`(MusicType.ALBUM))
            MatcherAssert.assertThat(secondItem.id, `is`("1"))
            MatcherAssert.assertThat(secondItem.title, `is`("title1 album 1"))
            MatcherAssert.assertThat(
                secondItem.description,
                `is`("artist2-1 album 1, artist2-2 album 1")
            )
            MatcherAssert.assertThat(secondItem.thumb, `is`("image2 album 1"))
            MatcherAssert.assertThat(secondItem.musicTheme?.type, `is`(ThemeType.DARK))
        }
    }

    @Test
    fun getAlbumSearchSuccess_responseNull_returnEmptyList() = runTest {
        val response = flowOf(null)
        whenever(musicSearchRepository.getAlbumQuery(any(), any(), any())).thenReturn(response)

        val flow = getSearchAlbumUseCase.execute(
            query = "query",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { itemList ->
            MatcherAssert.assertThat(itemList.size, `is`(0))
        }
    }

    @Test
    fun getAlbumSearchSuccess_responseEmptyList_returnEmptyList() = runTest {
        val response = flowOf(MusicSearchResponse())
        whenever(musicSearchRepository.getAlbumQuery(any(), any(), any())).thenReturn(response)

        val flow = getSearchAlbumUseCase.execute(
            query = "query",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { itemList ->
            MatcherAssert.assertThat(itemList.size, `is`(0))
        }
    }

    @Test
    fun getAlbumSearchSuccess_resultsIsNull_returnEmptyList() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(MusicSearchResponseItem(results = null))
            }
        )
        whenever(musicSearchRepository.getAlbumQuery(any(), any(), any())).thenReturn(response)

        val flow = getSearchAlbumUseCase.execute(
            query = "query",
            theme = ThemeType.DARK,
            offset = "20"
        )

        flow.collect { itemList ->
            MatcherAssert.assertThat(itemList.size, `is`(0))
        }
    }

    @Test
    fun getAlbumSearchSuccess_resultsHitsIsNull_returnEmptyList() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(MusicSearchResponseItem(results = Results(hits = null)))
            }
        )
        whenever(musicSearchRepository.getAlbumQuery(any(), any(), any())).thenReturn(response)

        val flow = getSearchAlbumUseCase.execute(
            query = "query",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { itemList ->
            MatcherAssert.assertThat(itemList.size, `is`(0))
        }
    }

    @Test
    fun getAlbumSearchSuccess_resultsHitsHitsListIsNull_returnEmptyList() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(MusicSearchResponseItem(results = Results(hits = Hits(hits = null))))
            }
        )
        whenever(musicSearchRepository.getAlbumQuery(any(), any(), any())).thenReturn(response)

        val flow = getSearchAlbumUseCase.execute(
            query = "query",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { itemList ->
            MatcherAssert.assertThat(itemList.size, `is`(0))
        }
    }

    @Test
    fun getAlbumSearchSuccess_resultsHitsHitsListIsEmpty_returnEmptyList() = runTest {
        val response = flowOf(
            MusicSearchResponse().apply {
                this.add(MusicSearchResponseItem(results = Results(hits = Hits(hits = emptyList()))))
            }
        )
        whenever(musicSearchRepository.getAlbumQuery(any(), any(), any())).thenReturn(response)

        val flow = getSearchAlbumUseCase.execute(
            query = "query",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { itemList ->
            MatcherAssert.assertThat(itemList.size, `is`(0))
        }
    }

    @Test
    fun getAlbumSearchSuccess_sourceNull_returnTitleAndDescriptionIsNullAndThumbIsEmpty() =
        runTest {
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
            whenever(musicSearchRepository.getAlbumQuery(any(), any(), any())).thenReturn(response)

            val flow = getSearchAlbumUseCase.execute(
                query = "query",
                theme = ThemeType.DARK,
                offset = "20"
            )
            flow.collect { itemList ->
                MatcherAssert.assertThat(itemList.size, `is`(1))
                MatcherAssert.assertThat(
                    (itemList[0] as MusicSearchModel.MusicItemModel).title,
                    `is`(nullValue())
                )
                MatcherAssert.assertThat(
                    (itemList[0] as MusicSearchModel.MusicItemModel).description,
                    `is`(nullValue())
                )
                MatcherAssert.assertThat(
                    (itemList[0] as MusicSearchModel.MusicItemModel).thumb,
                    `is`("")
                )
            }
        }

    @Test
    fun getAlbumSearchSuccess_metaNull_returnThumbIsEmpty() = runTest {
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
        whenever(musicSearchRepository.getAlbumQuery(any(), any(), any())).thenReturn(response)

        val flow = getSearchAlbumUseCase.execute(
            query = "query",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { itemList ->
            MatcherAssert.assertThat(itemList.size, `is`(1))
            MatcherAssert.assertThat(
                (itemList[0] as MusicSearchModel.MusicItemModel).thumb,
                `is`("")
            )
        }
    }

    @Test
    fun getAlbumSearchSuccess_metaEmptyList_returnThumbIsEmpty() = runTest {
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
        whenever(musicSearchRepository.getAlbumQuery(any(), any(), any())).thenReturn(response)

        val flow = getSearchAlbumUseCase.execute(
            query = "query",
            theme = ThemeType.DARK,
            offset = "20"
        )
        flow.collect { itemList ->
            MatcherAssert.assertThat(itemList.size, `is`(1))
            MatcherAssert.assertThat(
                (itemList[0] as MusicSearchModel.MusicItemModel).thumb,
                `is`("")
            )
        }
    }

    private fun fakeResponseSuccess(): MusicSearchResponse {
        return MusicSearchResponse().apply {
            this.add(fakeMusicSearchResponseItem("1", "album 1"))
            this.add(fakeMusicSearchResponseItem("2", "album 2"))
        }
    }

    private fun fakeMusicSearchResponseItem(
        id: String,
        name: String
    ): MusicSearchResponseItem {
        return MusicSearchResponseItem(
            key = "album",
            results = Results(
                Hits(
                    hits = listOf(
                        Hit(
                            id = id,
                            source = Source(
                                name = "title $name",
                                artists = listOf(
                                    Artist(
                                        id = 1,
                                        name = "artist1 $name"
                                    )
                                ),
                                meta = listOf(
                                    Meta(
                                        image = "image $name",
                                        albumImage = ""
                                    )
                                ),
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
                                name = "title1 $name",
                                artists = listOf(
                                    Artist(
                                        id = 2,
                                        name = "artist2-1 $name"
                                    ),
                                    Artist(
                                        id = 2,
                                        name = "artist2-2 $name"
                                    )
                                ),
                                meta = listOf(
                                    Meta(
                                        image = "image2 $name",
                                        albumImage = ""
                                    )
                                ),
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
