package com.truedigital.features.music.domain.trending.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.data.trending.model.response.artist.ArtistResponse
import com.truedigital.features.music.data.trending.model.response.artist.MusicTrendingArtistResponse
import com.truedigital.features.music.data.trending.repository.MusicTrendingArtistCacheRepository
import com.truedigital.features.music.data.trending.repository.MusicTrendingRepository
import com.truedigital.features.music.domain.trending.model.TrendingArtistModel
import com.truedigital.features.music.presentation.searchtrending.adapter.MusicSearchTrendingAdapter
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertNull

@ExtendWith(InstantTaskExecutorExtension::class)
class GetMusicTrendingArtistsUseCaseTest {

    private lateinit var getMusicTrendingArtistsUseCase: GetMusicTrendingArtistsUseCase
    private val localizationRepository: LocalizationRepository = mock()
    private val musicTrendingRepository: MusicTrendingRepository = mock()
    private val musicTrendingArtistCacheRepository: MusicTrendingArtistCacheRepository = mock()

    @BeforeEach
    fun setup() {
        getMusicTrendingArtistsUseCase = GetMusicTrendingArtistsUseCaseImpl(
            localizationRepository = localizationRepository,
            musicTrendingRepository = musicTrendingRepository,
            musicTrendingArtistCacheRepository = musicTrendingArtistCacheRepository
        )
    }

    @Test
    fun testNoCache_apiResponseNotNull_returnDataFromApi() = runTest {
        val results = arrayListOf(ArtistResponse(artistId = 99, name = "name", image = "image"))
        val responseData = MusicTrendingArtistResponse(results = results)

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingArtistCacheRepository.loadCacheTrendingArtist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingArtists())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingArtistsUseCase.execute()
        flow.collect { dataItem ->
            val headerDataItem =
                dataItem?.firstOrNull() as? MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertThat(
                headerDataItem?.headerId,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ARTIST_ID)
            )
            assertThat(
                headerDataItem?.id,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ARTIST_ID)
            )
            assertThat(
                headerDataItem?.title,
                `is`(GetMusicTrendingArtistsUseCaseImpl.KEY_EXTRA_TRENDING_ARTISTS_EN)
            )

            val artistDataItem =
                dataItem?.getOrNull(1) as? MusicSearchTrendingAdapter.DataItem.TrendingArtistItem
            val trendingArtist = artistDataItem?.trendingArtistList?.firstOrNull()
            assertThat(
                artistDataItem?.contentId,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_ARTIST_ID)
            )
            assertThat(trendingArtist?.id, `is`(results.firstOrNull()?.artistId))
            assertThat(trendingArtist?.name, `is`(results.firstOrNull()?.name))
            assertThat(trendingArtist?.image, `is`(results.firstOrNull()?.image))
        }
    }

    @Test
    fun testNoCache_apiResponseNull_returnNull() = runTest {
        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingArtistCacheRepository.loadCacheTrendingArtist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingArtists())
            .thenReturn(flowOf(null))

        val flow = getMusicTrendingArtistsUseCase.execute()
        flow.collect { dataItem ->
            assertNull(dataItem)
        }
    }

    @Test
    fun testNoCache_apiResultEmptyList_returnNull() = runTest {
        val responseData = MusicTrendingArtistResponse(results = emptyList())

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingArtistCacheRepository.loadCacheTrendingArtist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingArtists())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingArtistsUseCase.execute()
        flow.collect { dataItem ->
            assertNull(dataItem)
        }
    }

    @Test
    fun testNoCache_apiResultNull_returnNull() = runTest {
        val responseData = MusicTrendingArtistResponse(results = null)

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingArtistCacheRepository.loadCacheTrendingArtist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingArtists())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingArtistsUseCase.execute()
        flow.collect { dataItem ->
            assertNull(dataItem)
        }
    }

    @Test
    fun testNoCache_apiFail_returnNull() = runTest {
        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingArtistCacheRepository.loadCacheTrendingArtist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingArtists())
            .thenReturn(flow { error(Throwable("error API")) })

        val flow = getMusicTrendingArtistsUseCase.execute()
        flow.collect { dataItem ->
            assertNull(dataItem)
        }
    }

    @Test
    fun testHasCache_apiResponseNull_returnDataFromCache() = runTest {
        val responseDataFromCache = listOf(
            TrendingArtistModel().apply {
                id = 99
                name = "name"
                image = "image"
            }
        )

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingArtistCacheRepository.loadCacheTrendingArtist())
            .thenReturn(responseDataFromCache)

        whenever(musicTrendingRepository.getMusicTrendingArtists())
            .thenReturn(flowOf((null)))

        val flow = getMusicTrendingArtistsUseCase.execute()
        flow.collect { dataItem ->
            val headerDataItem =
                dataItem?.firstOrNull() as? MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertThat(
                headerDataItem?.headerId,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ARTIST_ID)
            )
            assertThat(
                headerDataItem?.id,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ARTIST_ID)
            )
            assertThat(
                headerDataItem?.title,
                `is`(GetMusicTrendingArtistsUseCaseImpl.KEY_EXTRA_TRENDING_ARTISTS_EN)
            )

            val artistDataItem =
                dataItem?.getOrNull(1) as? MusicSearchTrendingAdapter.DataItem.TrendingArtistItem
            val trendingArtist = artistDataItem?.trendingArtistList?.firstOrNull()
            assertThat(
                artistDataItem?.contentId,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_ARTIST_ID)
            )
            assertThat(trendingArtist?.id, `is`(responseDataFromCache.firstOrNull()?.id))
            assertThat(
                trendingArtist?.name,
                `is`(responseDataFromCache.firstOrNull()?.name)
            )
            assertThat(
                trendingArtist?.image,
                `is`(responseDataFromCache.firstOrNull()?.image)
            )
        }
    }

    @Test
    fun testHasCache_cacheResponseEmptyList_returnNull() = runTest {
        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingArtistCacheRepository.loadCacheTrendingArtist())
            .thenReturn(emptyList())

        whenever(musicTrendingRepository.getMusicTrendingArtists())
            .thenReturn(flowOf((null)))

        val flow = getMusicTrendingArtistsUseCase.execute()
        flow.collect { dataItem ->
            assertNull(dataItem)
        }
    }

    @Test
    fun testLocalization_localizationIsTH_returnTitleTH() = runTest {
        val results = arrayListOf(ArtistResponse(artistId = 99, name = "name", image = "image"))
        val responseData = MusicTrendingArtistResponse(results = results)

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.TH.languageCode)

        whenever(musicTrendingArtistCacheRepository.loadCacheTrendingArtist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingArtists())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingArtistsUseCase.execute()
        flow.collect { dataItem ->
            val headerDataItem =
                dataItem?.firstOrNull() as? MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertThat(
                headerDataItem?.title,
                `is`(GetMusicTrendingArtistsUseCaseImpl.KEY_EXTRA_TRENDING_ARTISTS_TH)
            )
        }
    }

    @Test
    fun testLocalization_localizationIsEN_returnTitleEN() = runTest {
        val results = arrayListOf(ArtistResponse(artistId = 99, name = "name", image = "image"))
        val responseData = MusicTrendingArtistResponse(results = results)

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingArtistCacheRepository.loadCacheTrendingArtist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingArtists())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingArtistsUseCase.execute()
        flow.collect { dataItem ->
            val headerDataItem =
                dataItem?.firstOrNull() as? MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertThat(
                headerDataItem?.title,
                `is`(GetMusicTrendingArtistsUseCaseImpl.KEY_EXTRA_TRENDING_ARTISTS_EN)
            )
        }
    }
}
