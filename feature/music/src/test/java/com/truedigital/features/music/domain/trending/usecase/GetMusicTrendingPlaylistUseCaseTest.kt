package com.truedigital.features.music.domain.trending.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.data.trending.model.response.playlist.MusicTrendingPlaylistResponse
import com.truedigital.features.music.data.trending.model.response.playlist.PlaylistResponse
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.music.data.trending.repository.MusicTrendingPlaylistCacheRepository
import com.truedigital.features.music.data.trending.repository.MusicTrendingRepository
import com.truedigital.features.music.domain.trending.model.TrendingPlaylistModel
import com.truedigital.features.music.presentation.searchtrending.adapter.MusicSearchTrendingAdapter
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(InstantTaskExecutorExtension::class)
internal class GetMusicTrendingPlaylistUseCaseTest {

    private lateinit var getMusicTrendingPlaylistUseCase: GetMusicTrendingPlaylistUseCase
    private val localizationRepository: LocalizationRepository = mock()
    private val musicTrendingRepository: MusicTrendingRepository = mock()
    private val musicTrendingPlaylistCacheRepository: MusicTrendingPlaylistCacheRepository = mock()

    @RegisterExtension
    @JvmField
    val testCoroutinesExtension = TestCoroutinesExtension()

    @BeforeEach
    fun setUp() {
        getMusicTrendingPlaylistUseCase = GetMusicTrendingPlaylistUseCaseImpl(
            localizationRepository = localizationRepository,
            musicTrendingRepository = musicTrendingRepository,
            musicTrendingPlaylistCacheRepository = musicTrendingPlaylistCacheRepository
        )
    }

    @Test
    fun testNoCache_apiResponseNotNull_returnDataFromApi() = runTest {
        val name = getName()
        val coverImage = getCoverImage()
        val results =
            arrayListOf(PlaylistResponse(playlistId = 99, name = name, coverImage = coverImage))
        val responseData = MusicTrendingPlaylistResponse(results = results)

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingPlaylist())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingPlaylistUseCase.execute()
        flow.collect { dataItem ->
            val headerDataItem =
                dataItem?.firstOrNull() as? MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertThat(
                headerDataItem?.headerId,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_HEADER_PLAYLIST_ID)
            )
            assertThat(
                headerDataItem?.id,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_HEADER_PLAYLIST_ID)
            )
            assertThat(
                headerDataItem?.title,
                `is`(GetMusicTrendingPlaylistUseCaseImpl.KEY_EXTRA_TRENDING_PLAYLIST_EN)
            )

            val playlistDataItem =
                dataItem?.getOrNull(1) as? MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem
            val trendingPlaylist = playlistDataItem?.trendingPlaylistList?.firstOrNull()
            val nameEN = name[0].value
            val coverImageEN = coverImage[0].value

            assertThat(
                playlistDataItem?.contentId,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_PLAYLIST_ID)
            )
            assertThat(trendingPlaylist?.id, `is`(results.firstOrNull()?.playlistId))
            assertThat(trendingPlaylist?.name, `is`(nameEN))
            assertThat(trendingPlaylist?.image, `is`(coverImageEN))
            assertEquals(1, playlistDataItem?.trendingPlaylistList?.size)
        }
    }

    @Test
    fun testNoCache_apiResponseNull_returnNull() = runTest {
        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingPlaylist())
            .thenReturn(flowOf(null))

        val flow = getMusicTrendingPlaylistUseCase.execute()
        flow.collect { dataItem ->
            assertNull(dataItem)
        }
    }

    @Test
    fun testNoCache_apiFail_returnNull() = runTest {
        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingPlaylist())
            .thenReturn(flow { error(Throwable("error API")) })

        val flow = getMusicTrendingPlaylistUseCase.execute()
        flow.collect { dataItem ->
            assertNull(dataItem)
        }
        verify(musicTrendingPlaylistCacheRepository, times(1))
            .saveCacheTrendingPlaylist(emptyList())
    }

    @Test
    fun testHasCache_cacheNotNull_returnDataFromCache() = runTest {
        val results = arrayListOf(
            TrendingPlaylistModel().apply {
                this.id = 99
                this.name = "name"
                this.image = "image"
            }
        )

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist())
            .thenReturn(results)

        val flow = getMusicTrendingPlaylistUseCase.execute()
        flow.collect { dataItem ->
            val headerDataItem =
                dataItem?.firstOrNull() as? MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertThat(
                headerDataItem?.headerId,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_HEADER_PLAYLIST_ID)
            )
            assertThat(
                headerDataItem?.id,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_HEADER_PLAYLIST_ID)
            )
            assertThat(
                headerDataItem?.title,
                `is`(GetMusicTrendingPlaylistUseCaseImpl.KEY_EXTRA_TRENDING_PLAYLIST_EN)
            )

            val playlistDataItem =
                dataItem?.getOrNull(1) as? MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem
            val trendingPlaylist = playlistDataItem?.trendingPlaylistList?.firstOrNull()

            assertThat(
                playlistDataItem?.contentId,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_PLAYLIST_ID)
            )
            assertThat(trendingPlaylist?.id, `is`(results.firstOrNull()?.id))
            assertThat(trendingPlaylist?.name, `is`(results.firstOrNull()?.name))
            assertThat(trendingPlaylist?.image, `is`(results.firstOrNull()?.image))
        }
    }

    @Test
    fun testHasCache_cacheResponseEmptyList_returnNull() = runTest {
        val results = arrayListOf<TrendingPlaylistModel>()

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist())
            .thenReturn(results)

        val flow = getMusicTrendingPlaylistUseCase.execute()
        flow.collect {
            assertNull(it)
        }
    }

    @Test
    fun testLocalization_localizationIsEN_returnTitleEN() = runTest {
        val name = getName()
        val coverImage = getCoverImage()
        val results =
            arrayListOf(PlaylistResponse(playlistId = 99, name = name, coverImage = coverImage))
        val responseData = MusicTrendingPlaylistResponse(results = results)

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingPlaylist())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingPlaylistUseCase.execute()
        flow.collect { dataItem ->
            val playlistDataItem =
                dataItem?.getOrNull(1) as? MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem
            val trendingPlaylist = playlistDataItem?.trendingPlaylistList?.firstOrNull()
            val nameEN = name[0].value
            val coverImageEN = coverImage[0].value

            assertThat(
                playlistDataItem?.contentId,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_PLAYLIST_ID)
            )
            assertThat(trendingPlaylist?.id, `is`(results.firstOrNull()?.playlistId))
            assertThat(trendingPlaylist?.name, `is`(nameEN))
            assertThat(trendingPlaylist?.image, `is`(coverImageEN))
        }
    }

    @Test
    fun testLocalization_localizationIsTH_returnTitleTH() = runTest {
        val name = getName()
        val coverImage = getCoverImage()
        val results =
            arrayListOf(PlaylistResponse(playlistId = 99, name = name, coverImage = coverImage))
        val responseData = MusicTrendingPlaylistResponse(results = results)

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.TH.languageCode)

        whenever(musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingPlaylist())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingPlaylistUseCase.execute()
        flow.collect { dataItem ->
            val playlistDataItem =
                dataItem?.getOrNull(1) as? MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem
            val trendingPlaylist = playlistDataItem?.trendingPlaylistList?.firstOrNull()
            val nameTH = name[1].value
            val coverImageTH = coverImage[1].value

            assertThat(
                playlistDataItem?.contentId,
                `is`(MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_PLAYLIST_ID)
            )
            assertThat(trendingPlaylist?.id, `is`(results.firstOrNull()?.playlistId))
            assertThat(trendingPlaylist?.name, `is`(nameTH))
            assertThat(trendingPlaylist?.image, `is`(coverImageTH))
        }
    }

    @Test
    fun testLocalizationEn_translationNameIsNull_returnEmptyName() = runTest {
        val name = null
        val coverImage = getCoverImage()
        val results =
            arrayListOf(PlaylistResponse(playlistId = 99, name = name, coverImage = coverImage))
        val responseData = MusicTrendingPlaylistResponse(results = results)

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingPlaylist())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingPlaylistUseCase.execute()
        flow.collect { dataItem ->
            val playlistDataItem =
                dataItem?.getOrNull(1) as? MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem
            val trendingPlaylist = playlistDataItem?.trendingPlaylistList?.firstOrNull()

            assertThat(trendingPlaylist?.name, `is`(""))
        }
    }

    @Test
    fun testLocalizationEn_translationNameIsEmptyList_returnEmptyName() = runTest {
        val name = emptyList<Translation>()
        val coverImage = getCoverImage()
        val results =
            arrayListOf(PlaylistResponse(playlistId = 99, name = name, coverImage = coverImage))
        val responseData = MusicTrendingPlaylistResponse(results = results)

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingPlaylist())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingPlaylistUseCase.execute()
        flow.collect { dataItem ->
            val playlistDataItem =
                dataItem?.getOrNull(1) as? MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem
            val trendingPlaylist = playlistDataItem?.trendingPlaylistList?.firstOrNull()

            assertThat(trendingPlaylist?.name, `is`(""))
        }
    }

    @Test
    fun testLocalizationEn_translationCoverImageIsNull_returnEmptyCoverImage() = runTest {
        val name = getName()
        val coverImage = null
        val results =
            arrayListOf(PlaylistResponse(playlistId = 99, name = name, coverImage = coverImage))
        val responseData = MusicTrendingPlaylistResponse(results = results)

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingPlaylist())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingPlaylistUseCase.execute()
        flow.collect { dataItem ->
            val playlistDataItem =
                dataItem?.getOrNull(1) as? MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem
            val trendingPlaylist = playlistDataItem?.trendingPlaylistList?.firstOrNull()

            assertThat(trendingPlaylist?.image, `is`(""))
        }
    }

    @Test
    fun testLocalizationEn_translationCoverImageEmptyList_returnEmptyCoverImage() = runTest {
        val name = getName()
        val coverImage = emptyList<Translation>()
        val results =
            arrayListOf(PlaylistResponse(playlistId = 99, name = name, coverImage = coverImage))
        val responseData = MusicTrendingPlaylistResponse(results = results)

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingPlaylist())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingPlaylistUseCase.execute()
        flow.collect { dataItem ->
            val playlistDataItem =
                dataItem?.getOrNull(1) as? MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem
            val trendingPlaylist = playlistDataItem?.trendingPlaylistList?.firstOrNull()

            assertThat(trendingPlaylist?.image, `is`(""))
        }
    }

    @Test
    fun testLocalizationEn_translationThIsFirst_returnEmptyCoverImage() = runTest {
        val name = getName()
        val coverImage = listOf(
            Translation(
                language = GetMusicTrendingPlaylistUseCaseImpl.TH,
                value = "coverImageTh"
            ),
            Translation(
                language = GetMusicTrendingPlaylistUseCaseImpl.EN,
                value = "coverImageEn"
            )
        )
        val results =
            arrayListOf(PlaylistResponse(playlistId = 99, name = name, coverImage = coverImage))
        val responseData = MusicTrendingPlaylistResponse(results = results)

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.EN.languageCode)

        whenever(musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingPlaylist())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingPlaylistUseCase.execute()
        flow.collect { dataItem ->
            val playlistDataItem =
                dataItem?.getOrNull(1) as? MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem
            val trendingPlaylist = playlistDataItem?.trendingPlaylistList?.firstOrNull()
            assertThat(trendingPlaylist?.image, `is`(coverImage[1].value))
        }
    }

    @Test
    fun testLocalizationTh_translationCoverImageIsEmptyList_returnEmptyCoverImage() = runTest {
        val name = getName()
        val coverImage = emptyList<Translation>()
        val results =
            arrayListOf(PlaylistResponse(playlistId = 99, name = name, coverImage = coverImage))
        val responseData = MusicTrendingPlaylistResponse(results = results)

        whenever(localizationRepository.getAppLanguageCode())
            .thenReturn(LocalizationRepository.Localization.TH.languageCode)

        whenever(musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist())
            .thenReturn(null)

        whenever(musicTrendingRepository.getMusicTrendingPlaylist())
            .thenReturn(flowOf(responseData))

        val flow = getMusicTrendingPlaylistUseCase.execute()
        flow.collect { dataItem ->
            val playlistDataItem =
                dataItem?.getOrNull(1) as? MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem
            val trendingPlaylist = playlistDataItem?.trendingPlaylistList?.firstOrNull()

            assertThat(trendingPlaylist?.image, `is`(""))
        }
    }

    private fun getName(): List<Translation> {
        return listOf(
            Translation(
                language = GetMusicTrendingPlaylistUseCaseImpl.EN,
                value = "nameEn"
            ),
            Translation(
                language = GetMusicTrendingPlaylistUseCaseImpl.TH,
                value = "nameTh"
            )
        )
    }

    private fun getCoverImage(): List<Translation> {
        return listOf(
            Translation(
                language = GetMusicTrendingPlaylistUseCaseImpl.EN,
                value = "coverImageEn"
            ),
            Translation(
                language = GetMusicTrendingPlaylistUseCaseImpl.TH,
                value = "coverImageTh"
            )
        )
    }
}
