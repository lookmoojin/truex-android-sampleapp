package com.truedigital.features.music.presentation.landing

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.music.domain.landing.model.ItemOptionsModel
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import com.truedigital.features.music.domain.landing.model.MusicHeroBannerDeeplinkType
import com.truedigital.features.music.domain.landing.usecase.DecodeMusicHeroBannerDeeplinkUseCase
import com.truedigital.features.music.domain.landing.usecase.GetCacheMusicShelfDataUseCase
import com.truedigital.features.music.domain.landing.usecase.GetContentBaseShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetContentItemUseCase
import com.truedigital.features.music.domain.landing.usecase.GetMusicBaseShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetMusicForYouShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetMusicUserByTagShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetRadioUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTagAlbumShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTagArtistShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTagPlaylistShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTrackPlaylistShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.SaveCacheMusicShelfDataUseCase
import com.truedigital.features.music.domain.model.ListenTopNavigationType
import com.truedigital.features.music.domain.queue.usecase.GetAllTrackQueueUseCase
import com.truedigital.features.music.domain.queue.usecase.GetCacheTrackQueueUseCase
import com.truedigital.features.music.domain.track.usecase.GetTrackListUseCase
import com.truedigital.features.music.domain.usecase.router.MusicRouterUseCase
import com.truedigital.features.music.navigation.router.MusicLandingToAlbum
import com.truedigital.features.music.navigation.router.MusicLandingToArtist
import com.truedigital.features.music.navigation.router.MusicLandingToExternalBrowser
import com.truedigital.features.music.navigation.router.MusicLandingToPlaylist
import com.truedigital.features.music.navigation.router.MusicLandingToSeeAll
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.utils.MockDataModel
import com.truedigital.navigation.domain.usecase.SetRouterSecondaryToNavControllerUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@ExtendWith(InstantTaskExecutorExtension::class)
internal class MusicLandingViewModelTest {

    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    private lateinit var musicLandingViewModel: MusicLandingViewModel
    private val router: MusicRouterUseCase = mock()
    private val decodeMusicHeroBannerDeeplinkUseCase: DecodeMusicHeroBannerDeeplinkUseCase = mock()
    private val getMusicBaseShelfUseCase: GetMusicBaseShelfUseCase = mock()
    private val saveCacheMusicShelfDataUseCase: SaveCacheMusicShelfDataUseCase = mock()
    private val getCacheMusicShelfDataUseCase: GetCacheMusicShelfDataUseCase = mock()
    private val getContentBaseShelfUseCase: GetContentBaseShelfUseCase = mock()
    private val getContentItemUseCase: GetContentItemUseCase = mock()
    private val getMusicForYouShelfUseCase: GetMusicForYouShelfUseCase = mock()
    private val getTagAlbumShelfUseCase: GetTagAlbumShelfUseCase = mock()
    private val getTagArtistShelfUseCase: GetTagArtistShelfUseCase = mock()
    private val getTagPlaylistShelfUseCase: GetTagPlaylistShelfUseCase = mock()
    private val getTrackPlaylistShelfUseCase: GetTrackPlaylistShelfUseCase = mock()
    private val getMusicUserByTagShelfUseCase: GetMusicUserByTagShelfUseCase = mock()
    private val getAllTrackQueueUseCase: GetAllTrackQueueUseCase = mock()
    private val getTrackListUseCase: GetTrackListUseCase = mock()
    private val getRadioUseCase: GetRadioUseCase = mock()
    private val getCacheTrackQueueUseCase: GetCacheTrackQueueUseCase = mock()
    private val setRouterSecondaryToNavControllerUseCase: SetRouterSecondaryToNavControllerUseCase = mockk()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val mockTrackList = listOf(
        Track(
            id = 1,
            playlistTrackId = 1,
            songId = 1,
            releaseId = 1,
            artists = listOf(),
            name = "name",
            originalCredit = "originalCredit",
            isExplicit = false,
            trackNumber = 1,
            trackNumberInVolume = 1,
            volumeNumber = 1,
            releaseArtists = listOf(),
            sample = "sample",
            isOnCompilation = false,
            releaseName = "releaseName",
            allowDownload = false,
            allowStream = false,
            duration = 3L,
            image = "image",
            hasLyrics = false,
            video = null,
            isVideo = false,
            vote = null,
            isDownloaded = false,
            syncProgress = 1F,
            isCached = false,
            translationsList = listOf(
                Translation(
                    language = MusicConstant.Language.LANG_TH,
                    value = "nameTh",
                ),
            ),
        ),
    )

    private val playlistShelfItemMock = MusicForYouItemModel.PlaylistShelfItem(
        playlistId = 1,
        "coverImage",
        "name",
        null,
    )
    private val musicForYouShelfMock = MusicForYouShelfModel(
        index = 0,
        title = "title",
        productListType = ProductListType.TAGGED_PLAYLISTS,
        itemList = listOf(playlistShelfItemMock),
    )
    private val mockBaseShelfId = "baseShelfId"

    @BeforeEach
    fun setUp() {
        musicLandingViewModel = MusicLandingViewModel(
            coroutineDispatcher,
            decodeMusicHeroBannerDeeplinkUseCase,
            router,
            saveCacheMusicShelfDataUseCase,
            getCacheMusicShelfDataUseCase,
            getContentBaseShelfUseCase,
            getContentItemUseCase,
            getMusicBaseShelfUseCase,
            getMusicForYouShelfUseCase,
            getTagAlbumShelfUseCase,
            getTagArtistShelfUseCase,
            getTagPlaylistShelfUseCase,
            getTrackPlaylistShelfUseCase,
            getMusicUserByTagShelfUseCase,
            getAllTrackQueueUseCase,
            getRadioUseCase,
            getCacheTrackQueueUseCase,
            setRouterSecondaryToNavControllerUseCase
        )
    }

    @Test
    fun loadData_cacheDataIsNotNull_shelfIsNotEmpty_showShelf() {
        // Given
        val mockShelfSlug = "shelfSlug"
        whenever(getCacheMusicShelfDataUseCase.execute(any())).thenReturn(
            Pair(mockBaseShelfId, mutableListOf(musicForYouShelfMock)),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(
            musicForYouShelfMock,
            musicLandingViewModel.onShowShelf().getOrAwaitValue().first(),
        )
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(any())
    }

    @Test
    fun loadData_cacheDataIsNotNull_shelfIsEmpty_showError() {
        // Given
        val mockShelfSlug = "shelfSlug"
        whenever(getCacheMusicShelfDataUseCase.execute(any())).thenReturn(
            Pair(mockBaseShelfId, mutableListOf()),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowError().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(any())
    }

    @Test
    fun loadData_cacheDataIsNull_baseShelfIdIsNull_showError() {
        // Given
        val mockShelfSlug = "shelfSlug"
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)

        // When
        musicLandingViewModel.loadData(null, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowError().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
    }

    @Test
    fun setShelfData_itemListIsNotEmpty_shelfIsSaved() {
        // Given
        val playlistShelfItemMock = MusicForYouItemModel.PlaylistShelfItem(
            playlistId = 1,
            "coverImage",
            "name",
            null,
        )
        val musicForYouShelfMock = MusicForYouShelfModel(
            index = 0,
            title = "title",
            productListType = ProductListType.TAGGED_PLAYLISTS,
            itemList = listOf(playlistShelfItemMock),
        )
        musicLandingViewModel.clearShelfData()

        // When
        musicLandingViewModel.setShelfData(musicForYouShelfMock)

        // Then
        val expectData = musicLandingViewModel.onShowShelf().getOrAwaitValue()
        assertEquals(expectData.size, 1)
        assertEquals(expectData.first().index, musicForYouShelfMock.index)
        assertEquals(expectData.first().title, musicForYouShelfMock.title)
        assertEquals(expectData.first().productListType, musicForYouShelfMock.productListType)
        assertEquals(expectData.first().itemList.size, 1)

        val expectItemList =
            expectData.first().itemList.first() as MusicForYouItemModel.PlaylistShelfItem
        assertEquals(expectItemList.playlistId, playlistShelfItemMock.playlistId)
        assertEquals(expectItemList.coverImage, playlistShelfItemMock.coverImage)
        assertEquals(expectItemList.name, playlistShelfItemMock.name)
    }

    @Test
    fun setShelfData_itemListIsEmpty_shelfIsNotSave() {
        // Given
        val musicForYouShelfMock = MusicForYouShelfModel(
            index = 0,
            title = "title",
            productListType = ProductListType.TAGGED_PLAYLISTS,
            itemList = listOf(),
        )
        musicLandingViewModel.clearShelfData()

        // When
        musicLandingViewModel.setShelfData(musicForYouShelfMock)

        // Then
        assertEquals(musicLandingViewModel.onShowShelf().value?.size ?: 0, 0)
    }

    @Test
    fun loadDataFromApi_baseShelfIdIsNotNullOrEmpty_shelfSlugIsForYou_loadBaseShelfSuccess_baseShelfIsNotEmpty_loadForYouBaseShelf() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.FOR_YOU.value
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getMusicBaseShelfUseCase.execute(any())).thenReturn(flowOf("apiPath"))
        whenever(getMusicForYouShelfUseCase.execute(any())).thenReturn(
            flowOf(
                listOf(
                    musicForYouShelfMock,
                ),
            ),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getMusicBaseShelfUseCase, times(1)).execute(any())
        verify(getMusicForYouShelfUseCase, times(1)).execute(any())
    }

    @Test
    fun loadDataFromApi_baseShelfIdIsNotNullOrEmpty_shelfSlugIsForYou_loadBaseShelfSuccess_baseShelfIsEmpty_showError() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.FOR_YOU.value
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getMusicBaseShelfUseCase.execute(any())).thenReturn(flowOf("apiPath"))
        whenever(getMusicForYouShelfUseCase.execute(any())).thenReturn(
            flowOf(emptyList()),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        assertEquals(Unit, musicLandingViewModel.onShowError().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getMusicBaseShelfUseCase, times(1)).execute(any())
        verify(saveCacheMusicShelfDataUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun loadDataFromApi_baseShelfIdIsNotNullOrEmpty_shelfSlugIsForYou_loadBaseShelfFail_showError() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.FOR_YOU.value
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getMusicBaseShelfUseCase.execute(any())).thenReturn(flow { error("something went wrong") })

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        assertEquals(Unit, musicLandingViewModel.onShowError().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getMusicBaseShelfUseCase, times(1)).execute(any())
        verify(saveCacheMusicShelfDataUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun loadDataFromApi_baseShelfIdIsNotNullOrEmpty_shelfSlugIsElse_loadBaseShelfSuccess_baseShelfIsNotEmpty_loadContentBaseShelf() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.MY_LIBRARY.value
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getContentBaseShelfUseCase.execute(any())).thenReturn(
            flowOf(
                listOf(musicForYouShelfMock),
            ),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getContentBaseShelfUseCase, times(1)).execute(any())
    }

    @Test
    fun loadDataFromApi_baseShelfIdIsNotNullOrEmpty_shelfSlugIsElse_loadBaseShelfSuccess_baseShelfIsEmpty_loadContentBaseShelf() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.MY_LIBRARY.value
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getContentBaseShelfUseCase.execute(any())).thenReturn(
            flowOf(emptyList()),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        assertEquals(Unit, musicLandingViewModel.onShowError().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getContentBaseShelfUseCase, times(1)).execute(any())
        verify(saveCacheMusicShelfDataUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun loadDataFromApi_baseShelfIdIsNotNullOrEmpty_shelfSlugIsElse_loadBaseShelfFail_showError() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.MY_LIBRARY.value
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getContentBaseShelfUseCase.execute(any())).thenReturn(
            flow { error("something went wrong") },
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        assertEquals(Unit, musicLandingViewModel.onShowError().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getContentBaseShelfUseCase, times(1)).execute(any())
        verify(saveCacheMusicShelfDataUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun loadDataFromApi_baseShelfIdIsNull_showError() {
        // Given
        val mockShelfSlug = "shelfSlug"
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)

        // When
        musicLandingViewModel.loadData(null, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowError().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
    }

    @Test
    fun loadDataFromApi_baseShelfIdIsEmpty_showError() {
        // Given
        val mockShelfSlug = "shelfSlug"
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)

        // When
        musicLandingViewModel.loadData("", mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowError().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
    }

    @Test
    fun getShelfUseCase_produceListTypeIsTaggedAlbums_optionIsNotNull_getTagAlbumShelfIsCalled() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.MY_LIBRARY.value
        val mockMusicMusicForYouShelfData = musicForYouShelfMock.copy(
            productListType = ProductListType.TAGGED_ALBUMS,
            options = ItemOptionsModel(tag = "tag", limit = "limit"),
        )
        val albumItemResponseMock = MusicForYouItemModel.AlbumShelfItem(
            albumId = 1,
            coverImage = "coverImage",
            albumName = "albumName",
            artistName = "artistName",
            releaseId = 2,
        )
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getContentBaseShelfUseCase.execute(any())).thenReturn(
            flowOf(listOf(mockMusicMusicForYouShelfData)),
        )
        whenever(getTagAlbumShelfUseCase.execute(any(), any())).thenReturn(
            flowOf(listOf(albumItemResponseMock)),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getContentBaseShelfUseCase, times(1)).execute(any())
        verify(getTagAlbumShelfUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun getShelfUseCase_produceListTypeIsTaggedAlbums_optionIsNull_getTagAlbumShelfIsCalled() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.MY_LIBRARY.value
        val mockMusicMusicForYouShelfData = musicForYouShelfMock.copy(
            productListType = ProductListType.TAGGED_ALBUMS,
            options = null,
        )
        val albumItemResponseMock = MusicForYouItemModel.AlbumShelfItem(
            albumId = 1,
            coverImage = "coverImage",
            albumName = "albumName",
            artistName = "artistName",
            releaseId = 2,
        )
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getContentBaseShelfUseCase.execute(any())).thenReturn(
            flowOf(listOf(mockMusicMusicForYouShelfData)),
        )
        whenever(getTagAlbumShelfUseCase.execute(any(), any())).thenReturn(
            flowOf(listOf(albumItemResponseMock)),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getContentBaseShelfUseCase, times(1)).execute(any())
        verify(getTagAlbumShelfUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun getShelfUseCase_produceListTypeIsTaggedArtist_optionIsNotNull_getTagArtistShelfIsCalled() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.MY_LIBRARY.value
        val mockMusicMusicForYouShelfData = musicForYouShelfMock.copy(
            productListType = ProductListType.TAGGED_ARTISTS,
            options = ItemOptionsModel(tag = "tag", limit = "limit"),
        )
        val artistItemResponseMock = MusicForYouItemModel.ArtistShelfItem(
            artistId = 1,
            coverImage = "coverImage",
            name = "artistName",
        )
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getContentBaseShelfUseCase.execute(any())).thenReturn(
            flowOf(listOf(mockMusicMusicForYouShelfData)),
        )
        whenever(getTagArtistShelfUseCase.execute(any(), any())).thenReturn(
            flowOf(listOf(artistItemResponseMock)),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getContentBaseShelfUseCase, times(1)).execute(any())
        verify(getTagArtistShelfUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun getShelfUseCase_produceListTypeIsTaggedArtist_optionIsNull_getTagArtistShelfIsCalled() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.MY_LIBRARY.value
        val mockMusicMusicForYouShelfData = musicForYouShelfMock.copy(
            productListType = ProductListType.TAGGED_ARTISTS,
            options = null,
        )
        val artistItemResponseMock = MusicForYouItemModel.ArtistShelfItem(
            artistId = 1,
            coverImage = "coverImage",
            name = "artistName",
        )
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getContentBaseShelfUseCase.execute(any())).thenReturn(
            flowOf(listOf(mockMusicMusicForYouShelfData)),
        )
        whenever(getTagArtistShelfUseCase.execute(any(), any())).thenReturn(
            flowOf(listOf(artistItemResponseMock)),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getContentBaseShelfUseCase, times(1)).execute(any())
        verify(getTagArtistShelfUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun getShelfUseCase_produceListTypeIsTaggedPlaylist_optionIsNotNull_getTagPlaylistShelfIsCalled() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.MY_LIBRARY.value
        val mockMusicMusicForYouShelfData = musicForYouShelfMock.copy(
            productListType = ProductListType.TAGGED_PLAYLISTS,
            options = ItemOptionsModel(tag = "tag", limit = "limit"),
        )
        val playlistItemResponseMock = MusicForYouItemModel.PlaylistShelfItem(
            playlistId = 1,
            coverImage = "coverImage",
            name = "name",
            nameEn = null,
        )
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getContentBaseShelfUseCase.execute(any())).thenReturn(
            flowOf(listOf(mockMusicMusicForYouShelfData)),
        )
        whenever(getTagPlaylistShelfUseCase.execute(any(), any())).thenReturn(
            flowOf(listOf(playlistItemResponseMock)),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getContentBaseShelfUseCase, times(1)).execute(any())
        verify(getTagPlaylistShelfUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun getShelfUseCase_produceListTypeIsTaggedPlaylist_optionIsNull_getTagPlaylistShelfIsCalled() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.MY_LIBRARY.value
        val mockMusicMusicForYouShelfData = musicForYouShelfMock.copy(
            productListType = ProductListType.TAGGED_PLAYLISTS,
            options = null,
        )
        val playlistItemResponseMock = MusicForYouItemModel.PlaylistShelfItem(
            playlistId = 1,
            coverImage = "coverImage",
            name = "name",
            nameEn = null,
        )
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getContentBaseShelfUseCase.execute(any())).thenReturn(
            flowOf(listOf(mockMusicMusicForYouShelfData)),
        )
        whenever(getTagPlaylistShelfUseCase.execute(any(), any())).thenReturn(
            flowOf(listOf(playlistItemResponseMock)),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getContentBaseShelfUseCase, times(1)).execute(any())
        verify(getTagPlaylistShelfUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun getShelfUseCase_produceListTypeIsTaggedUser_optionIsNotNull_getUserByTagShelfShelfIsCalled() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.MY_LIBRARY.value
        val mockMusicMusicForYouShelfData = musicForYouShelfMock.copy(
            productListType = ProductListType.TAGGED_USER,
            options = ItemOptionsModel(tag = "tag", limit = "limit"),
        )
        val mockSeeMoreDeeplinkPair = Pair(MusicHeroBannerDeeplinkType.SEE_MORE_RADIO, "deeplink")
        val heroBannerResponseMock = MusicForYouItemModel.MusicHeroBannerShelfItem(
            index = 1,
            coverImage = "coverImage",
            deeplinkPair = Pair(MusicHeroBannerDeeplinkType.PLAYLIST, "url"),
            heroBannerId = "heroBannerId",
            title = "title",
            contentType = "type",
        )
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getContentBaseShelfUseCase.execute(any())).thenReturn(
            flowOf(listOf(mockMusicMusicForYouShelfData)),
        )
        whenever(getMusicUserByTagShelfUseCase.execute(any())).thenReturn(
            flowOf(Triple(listOf(heroBannerResponseMock), ProductListType.TAGGED_USER, "")),
        )
        whenever(decodeMusicHeroBannerDeeplinkUseCase.execute(any())).thenReturn(
            mockSeeMoreDeeplinkPair,
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getContentBaseShelfUseCase, times(1)).execute(any())
        verify(getMusicUserByTagShelfUseCase, times(1)).execute(any())
        verify(decodeMusicHeroBannerDeeplinkUseCase, times(1)).execute(any())
    }

    @Test
    fun getShelfUseCase_produceListTypeIsTaggedUser_optionIsNull_getUserByTagShelfShelfIsCalled() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.MY_LIBRARY.value
        val mockMusicMusicForYouShelfData = musicForYouShelfMock.copy(
            productListType = ProductListType.TAGGED_USER,
            options = null,
        )
        val mockSeeMoreDeeplinkPair = Pair(MusicHeroBannerDeeplinkType.SEE_MORE_RADIO, "deeplink")
        val heroBannerResponseMock = MusicForYouItemModel.MusicHeroBannerShelfItem(
            index = 1,
            coverImage = "coverImage",
            deeplinkPair = Pair(MusicHeroBannerDeeplinkType.PLAYLIST, "url"),
            heroBannerId = "heroBannerId",
            title = "title",
            contentType = "type",
        )
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getContentBaseShelfUseCase.execute(any())).thenReturn(
            flowOf(listOf(mockMusicMusicForYouShelfData)),
        )
        whenever(getMusicUserByTagShelfUseCase.execute(any())).thenReturn(
            flowOf(Triple(listOf(heroBannerResponseMock), ProductListType.TAGGED_USER, "")),
        )
        whenever(decodeMusicHeroBannerDeeplinkUseCase.execute(any())).thenReturn(
            mockSeeMoreDeeplinkPair,
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getContentBaseShelfUseCase, times(1)).execute(any())
        verify(getMusicUserByTagShelfUseCase, times(1)).execute(any())
        verify(decodeMusicHeroBannerDeeplinkUseCase, times(1)).execute(any())
    }

    @Test
    fun getShelfUseCase_produceListTypeIsTracksPlaylist_optionIsNotNull_getTrackPlaylistShelfIsCalled() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.MY_LIBRARY.value
        val mockMusicMusicForYouShelfData = musicForYouShelfMock.copy(
            productListType = ProductListType.TRACKS_PLAYLIST,
            options = ItemOptionsModel(
                playlistId = "playlistId",
                limit = "limit",
                displayType = "displayType",
            ),
        )
        val trackPlaylistItemResponseMock = MusicForYouItemModel.TrackPlaylistShelf(
            playlistId = 1,
            playlistTrackId = 12345,
            trackId = 67890,
            artist = "artist name",
            coverImage = "cover image url",
            name = "song name",
            position = "1",
        )
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getContentBaseShelfUseCase.execute(any())).thenReturn(
            flowOf(listOf(mockMusicMusicForYouShelfData)),
        )
        whenever(getTrackPlaylistShelfUseCase.execute(any(), any(), any())).thenReturn(
            flowOf(listOf(trackPlaylistItemResponseMock)),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getContentBaseShelfUseCase, times(1)).execute(any())
        verify(getTrackPlaylistShelfUseCase, times(1)).execute(any(), any(), any())
    }

    @Test
    fun getShelfUseCase_produceListTypeIsTracksPlaylist_optionIsNull_getTrackPlaylistShelfIsCalled() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.MY_LIBRARY.value
        val mockMusicMusicForYouShelfData = musicForYouShelfMock.copy(
            productListType = ProductListType.TRACKS_PLAYLIST,
            options = null,
        )
        val trackPlaylistItemResponseMock = MusicForYouItemModel.TrackPlaylistShelf(
            playlistId = 1,
            playlistTrackId = 12345,
            trackId = 67890,
            artist = "artist name",
            coverImage = "cover image url",
            name = "song name",
            position = "1",
        )
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getContentBaseShelfUseCase.execute(any())).thenReturn(
            flowOf(listOf(mockMusicMusicForYouShelfData)),
        )
        whenever(getTrackPlaylistShelfUseCase.execute(any(), any(), any())).thenReturn(
            flowOf(listOf(trackPlaylistItemResponseMock)),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getContentBaseShelfUseCase, times(1)).execute(any())
        verify(getTrackPlaylistShelfUseCase, times(1)).execute(any(), any(), any())
    }

    @Test
    fun getShelfUseCase_produceListTypeIsContent_getContentItemIsCalled() {
        // Given
        val mockShelfSlug = ListenTopNavigationType.MY_LIBRARY.value
        val mockMusicMusicForYouShelfData = musicForYouShelfMock.copy(
            productListType = ProductListType.CONTENT,
            options = ItemOptionsModel(
                playlistId = "playlistId",
                limit = "limit",
                displayType = "displayType",
            ),
        )
        whenever(getCacheMusicShelfDataUseCase.execute(anyOrNull())).thenReturn(null)
        whenever(getContentBaseShelfUseCase.execute(any())).thenReturn(
            flowOf(listOf(mockMusicMusicForYouShelfData)),
        )
        whenever(getContentItemUseCase.execute(any(), any())).thenReturn(
            flowOf(listOf(MockDataModel.mockRadioShelf)),
        )

        // When
        musicLandingViewModel.loadData(mockBaseShelfId, mockShelfSlug)

        // Then
        assertEquals(Unit, musicLandingViewModel.onShowLoading().getOrAwaitValue())
        verify(getCacheMusicShelfDataUseCase, times(1)).execute(anyOrNull())
        verify(getContentBaseShelfUseCase, times(1)).execute(any())
        verify(getContentItemUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun handlerNavigateWithBundle_contentIdIsEmpty_contentTypeIsNotEmpty_doNothing() {
        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = "",
            contentType = MusicConstant.Type.ALBUM,
        )

        // Then
        verify(router, times(0)).execute(destination = any(), bundle = anyOrNull())
    }

    @Test
    fun handlerNavigateWithBundle_contentIdIsNotEmpty_contentTypeIsEmpty_doNothing() {
        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = "contentId",
            contentType = "",
        )

        // Then
        verify(router, times(0)).execute(destination = any(), bundle = anyOrNull())
    }

    @Test
    fun handlerNavigateWithBundle_contentIdIsEmpty_contentTypeIsEmpty_doNothing() {
        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = "",
            contentType = "",
        )

        // Then
        verify(router, times(0)).execute(destination = any(), bundle = anyOrNull())
    }

    @Test
    fun handlerNavigateWithBundle_shelfTypeIsAlbum_canConvertContentIdToInt_navigateToAlbum() {
        // Given
        val mockId = "110"

        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = mockId,
            contentType = MusicConstant.Type.ALBUM,
        )

        // Then
        verify(router, times(1)).execute(destination = any(), bundle = anyOrNull())
    }

    @Test
    fun handlerNavigateWithBundle_shelfTypeIsAlbum_canNotConvertContentIdToInt_navigateToAlbum() {
        // Given
        val mockId = "aa"

        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = mockId,
            contentType = MusicConstant.Type.ALBUM,
        )

        // Then
        verify(router, times(1)).execute(destination = any(), bundle = anyOrNull())
    }

    @Test
    fun handlerNavigateWithBundle_shelfTypeIsArtist_canConvertContentIdToInt_navigateToArtist() {
        // Given
        val mockId = "110"

        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = mockId,
            contentType = MusicConstant.Type.ARTIST,
        )

        // Then
        verify(router, times(1)).execute(destination = any(), bundle = anyOrNull())
    }

    @Test
    fun handlerNavigateWithBundle_shelfTypeIsArtist_canNotConvertContentIdToInt_navigateToArtist() {
        // Given
        val mockId = "aa"

        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = mockId,
            contentType = MusicConstant.Type.ARTIST,
        )

        // Then
        verify(router, times(1)).execute(destination = any(), bundle = anyOrNull())
    }

    @Test
    fun handlerNavigateWithBundle_shelfTypeIsPlaylist_canConvertContentIdToInt_navigateToPlaylist() {
        // Given
        val mockId = "110"

        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = mockId,
            contentType = MusicConstant.Type.PLAYLIST,
        )

        // Then
        verify(router, times(1)).execute(destination = any(), bundle = anyOrNull())
    }

    @Test
    fun handlerNavigateWithBundle_shelfTypeIsPlaylist_canNotConvertContentIdToInt_navigateToPlaylist() {
        // Given
        val mockId = "aa"

        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = mockId,
            contentType = MusicConstant.Type.PLAYLIST,
        )

        // Then
        verify(router, times(1)).execute(destination = any(), bundle = anyOrNull())
    }

    @Test
    fun handlerNavigateWithBundle_shelfTypeIsSong_canConvertContentIdToInt_returnGetTrack() {
        // Given
        val mockId = "110"

        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = mockId,
            contentType = MusicConstant.Type.SONG,
        )

        // Then
        assertEquals(mockId.toInt(), musicLandingViewModel.onGetTrack().getOrAwaitValue())
    }

    @Test
    fun handlerNavigateWithBundle_shelfTypeIsSong_canNotConvertContentIdToInt_doNothing() {
        // Given
        val mockId = "aa"

        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = mockId,
            contentType = MusicConstant.Type.SONG,
        )

        // Then
        assertNull(musicLandingViewModel.onGetTrack().value)
    }

    @Test
    fun handlerNavigateWithBundle_shelfTypeIsRadio_idIsNotEmpty_getRadioIsCalled() {
        // Given
        whenever(getRadioUseCase.execute(any())).thenReturn(flowOf(MockDataModel.mockRadioShelf))

        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = "id",
            contentType = MusicConstant.Type.RADIO,
        )

        // Then
        verify(getRadioUseCase, times(1)).execute(any())
    }

    @Test
    fun getRadio_success_radioIsNotNull_playRadio() {
        // Given
        whenever(getRadioUseCase.execute(any())).thenReturn(flowOf(MockDataModel.mockRadioShelf))

        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = "id",
            contentType = MusicConstant.Type.RADIO,
        )

        // Then
        verify(getRadioUseCase, times(1)).execute(any())
        assertEquals(
            MockDataModel.mockRadioShelf,
            musicLandingViewModel.onPlayRadio().getOrAwaitValue(),
        )
    }

    @Test
    fun getRadio_success_radioIsNull_showRadioError() {
        // Given
        whenever(getRadioUseCase.execute(any())).thenReturn(flowOf(null))

        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = "id",
            contentType = MusicConstant.Type.RADIO,
        )

        // Then
        assertEquals(
            Unit,
            musicLandingViewModel.onShowRadioError().getOrAwaitValue(),
        )
        assertEquals(
            Unit,
            musicLandingViewModel.onPausePlayer().getOrAwaitValue(),
        )
    }

    @Test
    fun getRadio_fail_showRadioError() {
        // Given
        whenever(getRadioUseCase.execute(any())).thenReturn(flow { error("Something went wrong") })

        // When
        musicLandingViewModel.handlerNavigateWithBundle(
            contentId = "id",
            contentType = MusicConstant.Type.RADIO,
        )

        // Then
        assertEquals(
            Unit,
            musicLandingViewModel.onShowRadioError().getOrAwaitValue(),
        )
        assertEquals(
            Unit,
            musicLandingViewModel.onPausePlayer().getOrAwaitValue(),
        )
    }

    @Test
    fun performClickItem_album_returnRouteToAlbum() {
        // Given
        whenever(router.getLastDestination()).thenReturn(MusicLandingToAlbum)

        // When
        musicLandingViewModel.performClickItem(
            MusicForYouItemModel.AlbumShelfItem(
                1,
                2,
                "image",
                "albumName",
                "artistName",
            ),
        )

        // Then
        assertEquals(MusicLandingToAlbum, router.getLastDestination())
    }

    @Test
    fun performClickItem_artist_returnRouteToArtist() {
        // Given
        whenever(router.getLastDestination()).thenReturn(MusicLandingToArtist)

        // When
        musicLandingViewModel.performClickItem(
            MusicForYouItemModel.ArtistShelfItem(
                1,
                "image",
                "name",
            ),
        )

        // Then
        assertEquals(MusicLandingToArtist, router.getLastDestination())
    }

    @Test
    fun performClickItem_playlist_returnRouteToPlaylist() {
        // Given
        whenever(router.getLastDestination()).thenReturn(MusicLandingToPlaylist)

        // When
        musicLandingViewModel.performClickItem(
            MusicForYouItemModel.PlaylistShelfItem(
                1,
                "image",
                "name",
                null,
            ),
        )

        // Then
        assertEquals(MusicLandingToPlaylist, router.getLastDestination())
    }

    @Test
    fun performClickItem_heroBannerAlbum_returnRouteToAlbum() {
        // Given
        whenever(router.getLastDestination()).thenReturn(MusicLandingToAlbum)

        // When
        musicLandingViewModel.performClickItem(
            MusicForYouItemModel.MusicHeroBannerShelfItem(
                index = 1,
                coverImage = "image",
                deeplinkPair = Pair(MusicHeroBannerDeeplinkType.ALBUM, "1"),
                title = "title",
                heroBannerId = "heroBannerId",
                contentType = "type",
            ),
        )

        // Then
        assertEquals(MusicLandingToAlbum, router.getLastDestination())
    }

    @Test
    fun performClickItem_heroBannerExternalBrowser_returnRouteToExternalBrowser() {
        // Given
        whenever(router.getLastDestination()).thenReturn(MusicLandingToExternalBrowser)

        // When
        musicLandingViewModel.performClickItem(
            MusicForYouItemModel.MusicHeroBannerShelfItem(
                index = 1,
                coverImage = "image",
                deeplinkPair = Pair(MusicHeroBannerDeeplinkType.EXTERNAL_BROWSER, "url"),
                title = "title",
                heroBannerId = "heroBannerId",
                contentType = "type",
            ),
        )

        // Then
        assertEquals(MusicLandingToExternalBrowser, router.getLastDestination())
    }

    @Test
    fun performClickItem_heroBannerExternalBrowserUrlEmpty_returnRouteToExternalBrowser() {
        // When
        musicLandingViewModel.performClickItem(
            MusicForYouItemModel.MusicHeroBannerShelfItem(
                index = 1,
                coverImage = "image",
                deeplinkPair = Pair(MusicHeroBannerDeeplinkType.EXTERNAL_BROWSER, ""),
                title = "title",
                heroBannerId = "heroBannerId",
                contentType = "type",
            ),
        )

        // Then
        assertEquals(null, router.getLastDestination())
    }

    @Test
    fun performClickItem_heroBannerInternalBrowser_returnRouteToInternalBrowser() {
        // When
        musicLandingViewModel.performClickItem(
            MusicForYouItemModel.MusicHeroBannerShelfItem(
                index = 1,
                coverImage = "image",
                deeplinkPair = Pair(MusicHeroBannerDeeplinkType.INTERNAL_DEEPLINK, "url"),
                title = "title",
                heroBannerId = "heroBannerId",
                contentType = "type",
            ),
        )

        // Then
        verify(router, times((1))).execute(stringUrl = "url")
    }

    @Test
    fun performClickItem_heroBannerPlaylist_returnRouteToPlaylist() {
        // Given
        whenever(router.getLastDestination()).thenReturn(MusicLandingToPlaylist)

        // When
        musicLandingViewModel.performClickItem(
            MusicForYouItemModel.MusicHeroBannerShelfItem(
                index = 1,
                coverImage = "image",
                deeplinkPair = Pair(MusicHeroBannerDeeplinkType.PLAYLIST, "1"),
                title = "title",
                heroBannerId = "heroBannerId",
                contentType = "type",
            ),
        )

        // Then
        assertEquals(MusicLandingToPlaylist, router.getLastDestination())
    }

    @Test
    fun performClickItem_heroBannerSong_returnGetTrack() {
        // Given
        val mockId = 1

        // When
        musicLandingViewModel.performClickItem(
            MusicForYouItemModel.MusicHeroBannerShelfItem(
                index = 1,
                coverImage = "image",
                deeplinkPair = Pair(MusicHeroBannerDeeplinkType.SONG, mockId.toString()),
                title = "title",
                heroBannerId = "heroBannerId",
                contentType = "type",
            ),
        )

        // Then
        assertEquals(musicLandingViewModel.onGetTrack().getOrAwaitValue(), mockId)
    }

    @Test
    fun performClickItem_heroBannerSeeMoreRadio_returnGetTrack() {
        // When
        musicLandingViewModel.performClickItem(
            MusicForYouItemModel.MusicHeroBannerShelfItem(
                index = 1,
                coverImage = "image",
                deeplinkPair = Pair(MusicHeroBannerDeeplinkType.SEE_MORE_RADIO, ""),
                title = "title",
                heroBannerId = "heroBannerId",
                contentType = "type",
            ),
        )

        // Then
        assertEquals(
            MusicConstant.Type.RADIO,
            musicLandingViewModel.onRenderContentBySlug().getOrAwaitValue(),
        )
    }

    @Test
    fun performClickItemTrackPlaylist_notLoadAllTrackQueue_returnPlayTrackList() {
        // Given
        val trackPosition = 0
        whenever(getCacheTrackQueueUseCase.execute(any())).thenReturn(Pair(mockTrackList, false))

        // When
        musicLandingViewModel.performClickItem(
            MusicForYouItemModel.TrackPlaylistShelf(
                index = 0,
                playlistTrackId = 12345,
                trackId = 67890,
                trackIdList = listOf(67890, 67891, 67892),
                artist = "artist name",
                coverImage = "cover image url",
                name = "song name",
                position = "",
            ),
        )

        // Then
        assertEquals(musicLandingViewModel.onTrackPosition().getOrAwaitValue(), trackPosition)
        assertEquals(musicLandingViewModel.onPlayTrackList().getOrAwaitValue(), mockTrackList)
    }

    @Test
    fun performClickItemTrackPlaylist_getTrackListSuccess_returnUpdateTrackList() = runTest {
        // Given
        val trackPosition = 0
        whenever(getCacheTrackQueueUseCase.execute(any())).thenReturn(Pair(mockTrackList, true))
        whenever(getTrackListUseCase.execute(any())).thenReturn(flowOf(mockTrackList))

        // When
        musicLandingViewModel.performClickItem(
            MusicForYouItemModel.TrackPlaylistShelf(
                index = 0,
                playlistTrackId = 12345,
                trackId = 67890,
                trackIdList = listOf(67890, 67891, 67892),
                artist = "artist name",
                coverImage = "cover image url",
                name = "song name",
                position = "",
            ),
        )

        // Then
        assertEquals(musicLandingViewModel.onTrackPosition().getOrAwaitValue(), trackPosition)
        assertEquals(musicLandingViewModel.onPlayTrackList().getOrAwaitValue(), mockTrackList)
    }

    @Test
    fun performClickItemTrackPlaylist_getTrackListFail_returnPlayTrackList() {
        // Given
        val trackPosition = 0
        whenever(getCacheTrackQueueUseCase.execute(any())).thenReturn(Pair(mockTrackList, true))
        whenever(getTrackListUseCase.execute(any())).thenReturn(flow { error("error") })

        // When
        musicLandingViewModel.performClickItem(
            MusicForYouItemModel.TrackPlaylistShelf(
                index = 0,
                playlistTrackId = 12345,
                trackId = 67890,
                trackIdList = listOf(67890, 67891, 67892),
                artist = "artist name",
                coverImage = "cover image url",
                name = "song name",
                position = "",
            ),
        )

        // Then
        assertEquals(musicLandingViewModel.onTrackPosition().getOrAwaitValue(), trackPosition)
        assertEquals(musicLandingViewModel.onPlayTrackList().getOrAwaitValue(), mockTrackList)
    }

    @Test
    fun performClickItem_radio_playRadio() {
        // When
        musicLandingViewModel.performClickItem(MockDataModel.mockRadioShelf)

        // Then
        assertEquals(MockDataModel.mockRadioShelf, musicLandingViewModel.onPlayRadio().value)
    }

    @Test
    fun performClickSeeAll_tagAlbum_returnRouteToMusicLandingToSeeAll() {
        // Given
        whenever(router.getLastDestination()).thenReturn(MusicLandingToSeeAll)

        // When
        musicLandingViewModel.performClickSeeAll(
            MusicForYouShelfModel(
                productListType = ProductListType.TAGGED_ALBUMS,
                title = "album",
                options = null,
            ),
        )

        // Then
        assertEquals(MusicLandingToSeeAll, router.getLastDestination())
    }

    @Test
    fun performClickSeeAll_tagPlaylist_returnRouteToMusicLandingToSeeAll() {
        // Given
        whenever(router.getLastDestination()).thenReturn(MusicLandingToSeeAll)

        // When
        musicLandingViewModel.performClickSeeAll(
            MusicForYouShelfModel(
                productListType = ProductListType.TAGGED_PLAYLISTS,
                title = "playlist",
                options = ItemOptionsModel(
                    tag = "tag",
                    format = "format",
                    displayTitle = true,
                    targetTime = true,
                ),
            ),
        )

        // Then
        assertEquals(MusicLandingToSeeAll, router.getLastDestination())
    }

    @Test
    fun performClickSeeAll_trackPlaylist_returnRouteToPlaylist() {
        // Given
        whenever(router.getLastDestination()).thenReturn(MusicLandingToPlaylist)

        // When
        musicLandingViewModel.performClickSeeAll(
            MusicForYouShelfModel(
                productListType = ProductListType.TRACKS_PLAYLIST,
                options = ItemOptionsModel(
                    playlistId = "007",
                ),
            ),
        )

        // Then
        assertEquals(MusicLandingToPlaylist, router.getLastDestination())
    }

    @Test
    fun performClickSeeAll_productTypeIsTaggedRadio_seeMoreDeeplinkPairIsNotNull_deeplinkTypeIsSeeMoreRadio_renderContent() {
        // Given
        val mockSeeMoreDeeplinkPair = Pair(MusicHeroBannerDeeplinkType.SEE_MORE_RADIO, "deeplink")

        // When
        musicLandingViewModel.performClickSeeAll(
            MusicForYouShelfModel(
                productListType = ProductListType.TAGGED_RADIO,
                seeMoreDeeplinkPair = mockSeeMoreDeeplinkPair,
            ),
        )

        // Then
        assertEquals(
            MusicConstant.Type.RADIO,
            musicLandingViewModel.onRenderContentBySlug().getOrAwaitValue(),
        )
    }

    @Test
    fun performClickSeeAll_productTypeIsTaggedRadio_seeMoreDeeplinkPairIsNotNull_deeplinkTypeIsNotSeeMoreRadio_doNothing() {
        // Given
        val mockSeeMoreDeeplinkPair = Pair(MusicHeroBannerDeeplinkType.RADIO, "deeplink")

        // When
        musicLandingViewModel.performClickSeeAll(
            MusicForYouShelfModel(
                productListType = ProductListType.TAGGED_RADIO,
                seeMoreDeeplinkPair = mockSeeMoreDeeplinkPair,
            ),
        )

        // Then
        assertNull(musicLandingViewModel.onRenderContentBySlug().value)
    }

    @Test
    fun performClickSeeAll_productTypeIsTaggedRadio_seeMoreDeeplinkPairIsNull_doNothing() {
        // When
        musicLandingViewModel.performClickSeeAll(
            MusicForYouShelfModel(
                productListType = ProductListType.TAGGED_RADIO,
                seeMoreDeeplinkPair = null,
            ),
        )

        // Then
        assertNull(musicLandingViewModel.onRenderContentBySlug().value)
    }

    @Test
    fun performClickSeeAll_trackPlaylistOptionNull_returnRouteToPlaylist() {
        // When
        musicLandingViewModel.performClickSeeAll(
            MusicForYouShelfModel(
                productListType = ProductListType.TRACKS_PLAYLIST,
                options = null,
            ),
        )

        // Then
        assertNull(router.getLastDestination())
    }

    @Test
    fun performClickSeeAll_unSupported_returnNotRouteTo() {
        // When
        musicLandingViewModel.performClickSeeAll(
            MusicForYouShelfModel(
                productListType = ProductListType.UNSUPPORTED,
            ),
        )

        // Then
        verify(router, times(0)).execute(destination = any(), bundle = anyOrNull())
    }
}
