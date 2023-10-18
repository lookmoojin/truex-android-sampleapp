package com.truedigital.features.music.domain.search.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.repository.LocalizationRepository
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class GetSearchAllUseCaseTest {

    private lateinit var getSearchItemUseCase: GetSearchAllUseCase
    private val localizationRepository: LocalizationRepository = mock()
    private val musicSearchRepository: MusicSearchRepository = mock()

    @BeforeEach
    fun setUp() {
        getSearchItemUseCase = GetSearchAllUseCaseImpl(
            musicSearchRepository = musicSearchRepository,
            localizationRepository = localizationRepository
        )
    }

    @Test
    fun `test GetSearchItemUseCase success should return data`() = runTest {
        val response = flowOf(fakeResponseSuccess())
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.EN.languageCode
        )

        whenever(musicSearchRepository.getSearchQuery(any()))
            .thenReturn(response)

        val flow = getSearchItemUseCase.execute(query = "query", theme = ThemeType.DARK)
        flow.collect { musicList ->
            val headerList = mutableListOf<MusicSearchModel>()
            val musicItemList = mutableListOf<MusicSearchModel>()
            for (item in musicList) {
                when (item) {
                    is MusicSearchModel.MusicItemModel -> {
                        musicItemList.add(item)
                    }
                    is MusicSearchModel.MusicHeaderModel -> {
                        headerList.add(item)
                    }
                }
            }

            assertThat(headerList.size, `is`(4))
            assertThat(musicItemList.size, `is`(8))
            assertThat(
                (musicItemList[0] as MusicSearchModel.MusicItemModel).type.toString(),
                `is`("SONG")
            )
        }
    }

    @Test
    fun `test GetSearchItemUseCase should return error`() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.EN.languageCode
        )
        whenever(musicSearchRepository.getSearchQuery(any()))
            .thenReturn(
                flow {
                    error(Throwable("error API"))
                }
            )

        val flow = getSearchItemUseCase.execute(query = "query", theme = ThemeType.DARK)
        flow.catch {
            assertThat(it.localizedMessage, `is`("java.lang.Throwable: error API"))
        }.collect()
    }

    private fun fakeResponseSuccess(): MusicSearchResponse {
        return MusicSearchResponse().apply {
            this.add(fakeMusicSearchResponseItem("song", "1", "title 1"))
            this.add(fakeMusicSearchResponseItem("artist", "1", "title 1"))
            this.add(fakeMusicSearchResponseItem("album", "1", "title 1"))
            this.add(fakeMusicSearchResponseItem("playlist", "1", "title 1"))
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
                                    ),
                                    Artist(
                                        id = 1,
                                        name = "artist2 $name"
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
                                        name = "artist $name"
                                    ),
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
