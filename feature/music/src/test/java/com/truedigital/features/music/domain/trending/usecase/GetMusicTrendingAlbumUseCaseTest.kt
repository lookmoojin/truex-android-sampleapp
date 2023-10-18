package com.truedigital.features.music.domain.trending.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.data.trending.model.response.album.AlbumArtist
import com.truedigital.features.music.data.trending.model.response.album.AlbumPrimaryRelease
import com.truedigital.features.music.data.trending.model.response.album.AlbumResponse
import com.truedigital.features.music.data.trending.model.response.album.MusicTrendingAlbumResponse
import com.truedigital.features.music.data.trending.repository.MusicTrendingAlbumCacheRepository
import com.truedigital.features.music.data.trending.repository.MusicTrendingRepository
import com.truedigital.features.music.domain.trending.model.TrendingAlbumModel
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
class GetMusicTrendingAlbumUseCaseTest {

    private lateinit var getMusicTrendingAlbumUseCase: GetMusicTrendingAlbumUseCase
    private val localizationRepository: LocalizationRepository = mock()
    private val musicTrendingRepository: MusicTrendingRepository = mock()
    private val musicTrendingAlbumCacheRepository: MusicTrendingAlbumCacheRepository = mock()

    @BeforeEach
    fun setup() {
        getMusicTrendingAlbumUseCase = GetMusicTrendingAlbumUseCaseImpl(
            localizationRepository = localizationRepository,
            musicTrendingRepository = musicTrendingRepository,
            musicTrendingAlbumCacheRepository = musicTrendingAlbumCacheRepository
        )
    }

    @Test
    fun testNoCache_apiResponseNotNull_returnDataFromApi() = runTest {
        val responseData = getAlbumResponse()

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingAlbumCacheRepository.loadCacheTrendingAlbum())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingAlbum())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingAlbumUseCase.execute()
        flow.collect { dataItem ->
            val headerDataItem =
                dataItem?.firstOrNull() as? MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertThat(
                headerDataItem?.headerId,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ALBUM_ID)
            )
            assertThat(
                headerDataItem?.id,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ALBUM_ID)
            )
            assertThat(
                headerDataItem?.title,
                `is`(GetMusicTrendingAlbumUseCaseImpl.KEY_EXTRA_TRENDING_ALBUM_EN)
            )

            val albumDataItem =
                dataItem?.getOrNull(1) as? MusicSearchTrendingAdapter.DataItem.TrendingAlbumItem
            val trendingAlbum = albumDataItem?.trendingAlbumList?.firstOrNull()
            assertThat(
                albumDataItem?.contentId,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_ALBUM_ID)
            )
            assertThat(
                trendingAlbum?.id,
                `is`(responseData.results?.firstOrNull()?.albumId)
            )
            assertThat(trendingAlbum?.name, `is`(responseData.results?.firstOrNull()?.name))
            assertThat(
                trendingAlbum?.artistName,
                `is`(
                    responseData.results?.firstOrNull()?.artists?.map { it.name }
                        ?.joinToString().orEmpty()
                )
            )
            assertThat(
                trendingAlbum?.image,
                `is`(responseData.results?.firstOrNull()?.primaryRelease?.image)
            )
        }
    }

    @Test
    fun testNoCache_apiResponseNull_returnNull() = runTest {
        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingAlbumCacheRepository.loadCacheTrendingAlbum())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingAlbum())
            .thenReturn(flowOf(null))

        val flow = getMusicTrendingAlbumUseCase.execute()
        flow.collect { dataItem ->
            assertNull(dataItem)
        }
    }

    @Test
    fun testHasCache_apiResponseNull_returnDataFromCache() = runTest {
        val responseDataFromCache = listOf(
            TrendingAlbumModel().apply {
                id = 99
                name = "name"
                image = "image"
            }
        )

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingAlbumCacheRepository.loadCacheTrendingAlbum())
            .thenReturn(responseDataFromCache)

        whenever(musicTrendingRepository.getMusicTrendingAlbum())
            .thenReturn(flowOf((null)))

        val flow = getMusicTrendingAlbumUseCase.execute()
        flow.collect { dataItem ->
            val headerDataItem =
                dataItem?.firstOrNull() as? MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertThat(
                headerDataItem?.headerId,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ALBUM_ID)
            )
            assertThat(
                headerDataItem?.id,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ALBUM_ID)
            )
            assertThat(
                headerDataItem?.title,
                `is`(GetMusicTrendingAlbumUseCaseImpl.KEY_EXTRA_TRENDING_ALBUM_EN)
            )

            val albumDataItem =
                dataItem?.getOrNull(1) as? MusicSearchTrendingAdapter.DataItem.TrendingAlbumItem
            val trendingAlbum = albumDataItem?.trendingAlbumList?.firstOrNull()
            assertThat(
                albumDataItem?.contentId,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_ALBUM_ID)
            )
            assertThat(trendingAlbum?.id, `is`(responseDataFromCache.firstOrNull()?.id))
            assertThat(trendingAlbum?.name, `is`(responseDataFromCache.firstOrNull()?.name))
            assertThat(
                trendingAlbum?.artistName,
                `is`(responseDataFromCache.firstOrNull()?.artistName)
            )
            assertThat(
                trendingAlbum?.image,
                `is`(responseDataFromCache.firstOrNull()?.image)
            )
        }
    }

    @Test
    fun testHasCache_cacheResponseEmptyList_returnNull() = runTest {
        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingAlbumCacheRepository.loadCacheTrendingAlbum())
            .thenReturn(emptyList())

        whenever(musicTrendingRepository.getMusicTrendingAlbum())
            .thenReturn(flowOf((null)))

        val flow = getMusicTrendingAlbumUseCase.execute()
        flow.collect { dataItem ->
            assertNull(dataItem)
        }
    }

    @Test
    fun testLocalization_localizationIsTH_returnTitleTH() = runTest {
        val responseData = getAlbumResponse()

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.TH.languageCode)

        whenever(musicTrendingAlbumCacheRepository.loadCacheTrendingAlbum())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingAlbum())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingAlbumUseCase.execute()
        flow.collect { dataItem ->
            val headerDataItem =
                dataItem?.firstOrNull() as? MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertThat(
                headerDataItem?.title,
                `is`(GetMusicTrendingAlbumUseCaseImpl.KEY_EXTRA_TRENDING_ALBUM_TH)
            )
        }
    }

    @Test
    fun testLocalization_localizationIsEN_returnTitleEN() = runTest {
        val responseData = getAlbumResponse()

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingAlbumCacheRepository.loadCacheTrendingAlbum())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingAlbum())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingAlbumUseCase.execute()
        flow.collect { dataItem ->
            val headerDataItem =
                dataItem?.firstOrNull() as? MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertThat(
                headerDataItem?.title,
                `is`(GetMusicTrendingAlbumUseCaseImpl.KEY_EXTRA_TRENDING_ALBUM_EN)
            )
        }
    }

    @Test
    fun testNoCache_apiFail_returnNull() = runTest {
        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingAlbumCacheRepository.loadCacheTrendingAlbum())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingAlbum())
            .thenReturn(flow { error(Throwable("error API")) })

        val flow = getMusicTrendingAlbumUseCase.execute()
        flow.collect { dataItem ->
            assertNull(dataItem)
        }
        verify(musicTrendingAlbumCacheRepository, times(1)).saveCacheTrendingAlbum(emptyList())
    }

    private fun getAlbumResponse(): MusicTrendingAlbumResponse = MusicTrendingAlbumResponse(
        offset = 1,
        count = 10,
        total = 50,
        results = listOf(
            AlbumResponse(
                albumId = 135702388,
                name = "รักให้ถึงที่สุด - Single",
                artists = listOf(AlbumArtist(artistId = 2340548, name = "O-Pavee")),
                albumType = "Album",
                primaryRelease = AlbumPrimaryRelease(
                    image = "https://tunedglobal-a.akamaihd.net" +
                        "/images1373/137/3_0/060/243/570/345/9/" +
                        "104_1373_00602435703459_20210128_0933.jpg"
                )
            )
        )
    )
}
