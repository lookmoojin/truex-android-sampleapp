package com.truedigital.features.tuned.presentation.tag

import android.os.Bundle
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.domain.facade.tag.TagFacade
import com.truedigital.features.tuned.presentation.tag.presenter.TagPresenter
import com.truedigital.features.utils.MockDataModel
import com.truedigital.features.utils.MockJson
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

internal class TagPresenterTest {

    private lateinit var tagPresenter: TagPresenter
    private val tagFacade: TagFacade = mock()
    private val router: TagPresenter.RouterSurface = mock()
    private val view: TagPresenter.ViewSurface = mock()
    private val bundle = Mockito.mock(Bundle::class.java)

    private val mockAlbum = MockDataModel.mockAlbum
    private val mockArtist = MockDataModel.mockArtist
    private val mockPlaylist = MockDataModel.mockPlaylist
    private val mockStation = MockDataModel.mockStation
    private val mockTag = MockDataModel.mockTag

    @BeforeEach
    fun setup() {
        tagPresenter = TagPresenter(tagFacade)
        tagPresenter.onInject(view, router)
        tagPresenter.onResume()
    }

    @Test
    fun onStart_argumentIsNotNull_tagStringIsNotNull_tagNameIsNull_showTagContent() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getString(TagPresenter.TAG_KEY)).thenReturn(MockJson.tagJSON)
        tagPresenter.setObservable(
            loadAlbumsObservable = Single.just(listOf(MockDataModel.mockAlbum)),
            loadArtistsObservable = Single.just(listOf(MockDataModel.mockArtist)),
            loadPlaylistsObservable = Single.just(listOf(MockDataModel.mockPlaylist)),
            loadStationsObservable = Single.just(listOf(MockDataModel.mockStation))
        )

        // When
        tagPresenter.onStart(mockBundle)

        // Then
        verify(view, times(1)).showTagContent(any(), any(), any())
    }

    @Test
    fun onStart_argumentIsNotNull_tagStringIsNull_tagNameIsNotNull_getTagByName() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getString(TagPresenter.TAG_NAME_KEY)).thenReturn("tagName")
        whenever(tagFacade.getTagByName(any())).thenReturn(Single.just(MockDataModel.mockTag))

        // When
        tagPresenter.onStart(mockBundle)

        // Then
        verify(tagFacade, times(1)).getTagByName(any())
    }

    @Test
    fun onStart_argumentIsNotNull_tagStringIsNull_tagNameIsNull_showTagContentError() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)

        // When
        tagPresenter.onStart(mockBundle)

        // Then
        verify(view, times(1)).showTagContentError()
    }

    @Test
    fun onStart_argumentIsNull_doNothing() {
        // When
        tagPresenter.onStart(null)

        // Then
        verify(view, times(0)).showTagContentError()
        verify(tagFacade, times(0)).getTagByName(any())
    }

    @Test
    fun onResume_getTagContentByNameSubscription_success_showTagContent() {
        // Given
        val mockGetTagContentSubscription: Disposable = mock()
        tagPresenter.setSubscription(getTagContentSubscription = mockGetTagContentSubscription)
        tagPresenter.setObservable(getTagContentObservable = Single.just(MockDataModel.mockTag))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showTagContent(any(), any(), any())
    }

    @Test
    fun onResume_getTagContentByNameSubscription_fail_showTagContentError() {
        // Given
        val mockGetTagContentSubscription: Disposable = mock()
        tagPresenter.setSubscription(getTagContentSubscription = mockGetTagContentSubscription)
        tagPresenter.setObservable(getTagContentObservable = Single.error(Throwable("error")))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showTagContentError()
    }

    @Test
    fun onResume_getAlbumsSubscription_success_showTaggedAlbums() {
        // Given
        val mockLoadAlbumsSubscription: Disposable = mock()
        tagPresenter.setSubscription(loadAlbumsSubscription = mockLoadAlbumsSubscription)
        tagPresenter.setObservable(loadAlbumsObservable = Single.just(listOf(MockDataModel.mockAlbum)))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showTaggedAlbums(any(), any())
    }

    @Test
    fun onResume_getAlbumsSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_albumIsEmpty_hideAlbumSection() {
        // Given
        val mockLoadAlbumsSubscription: Disposable = mock()
        tagPresenter.setPrivateData(albums = mutableListOf())
        tagPresenter.setSubscription(loadAlbumsSubscription = mockLoadAlbumsSubscription)
        tagPresenter.setObservable(loadAlbumsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).hideAlbumsSection()
    }

    @Test
    fun onResume_getAlbumsSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_albumIsNotEmpty_showTaggedAlbums() {
        // Given
        val mockLoadAlbumsSubscription: Disposable = mock()
        tagPresenter.setPrivateData(albums = mutableListOf(MockDataModel.mockAlbum))
        tagPresenter.setSubscription(loadAlbumsSubscription = mockLoadAlbumsSubscription)
        tagPresenter.setObservable(loadAlbumsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showTaggedAlbums(any(), any())
    }

    @Test
    fun onResume_getAlbumsSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showAlbumsError() {
        // Given
        val mockLoadAlbumsSubscription: Disposable = mock()
        tagPresenter.setPrivateData(albums = mutableListOf())
        tagPresenter.setSubscription(loadAlbumsSubscription = mockLoadAlbumsSubscription)
        tagPresenter.setObservable(loadAlbumsObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showAlbumsError()
    }

    @Test
    fun onResume_getAlbumsSubscription_fail_errorIsNotHttpException_showAlbumsError() {
        // Given
        val mockLoadAlbumsSubscription: Disposable = mock()
        tagPresenter.setSubscription(loadAlbumsSubscription = mockLoadAlbumsSubscription)
        tagPresenter.setObservable(loadAlbumsObservable = Single.error(Throwable("error")))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showAlbumsError()
    }

    @Test
    fun onResume_getArtistsSubscription_success_showTaggedArtists() {
        // Given
        val mockLoadArtistsSubscription: Disposable = mock()
        tagPresenter.setSubscription(loadArtistsSubscription = mockLoadArtistsSubscription)
        tagPresenter.setObservable(loadArtistsObservable = Single.just(listOf(MockDataModel.mockArtist)))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showTaggedArtists(any(), any())
    }

    @Test
    fun onResume_getArtistsSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_artistIsEmpty_hideArtistSection() {
        // Given
        val mockLoadArtistsSubscription: Disposable = mock()
        tagPresenter.setPrivateData(artists = mutableListOf())
        tagPresenter.setSubscription(loadArtistsSubscription = mockLoadArtistsSubscription)
        tagPresenter.setObservable(loadArtistsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).hideArtistsSection()
    }

    @Test
    fun onResume_getArtistsSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_artistIsNotEmpty_showTaggedArtists() {
        // Given
        val mockLoadArtistsSubscription: Disposable = mock()
        tagPresenter.setPrivateData(artists = mutableListOf(MockDataModel.mockArtist))
        tagPresenter.setSubscription(loadArtistsSubscription = mockLoadArtistsSubscription)
        tagPresenter.setObservable(loadArtistsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showTaggedArtists(any(), any())
    }

    @Test
    fun onResume_getArtistsSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showArtistsError() {
        // Given
        val mockLoadArtistsSubscription: Disposable = mock()
        tagPresenter.setSubscription(loadArtistsSubscription = mockLoadArtistsSubscription)
        tagPresenter.setObservable(loadArtistsObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showArtistsError()
    }

    @Test
    fun onResume_getArtistsSubscription_fail_errorIsNotHttpException_showArtistsError() {
        // Given
        val mockLoadArtistsSubscription: Disposable = mock()
        tagPresenter.setSubscription(loadArtistsSubscription = mockLoadArtistsSubscription)
        tagPresenter.setObservable(loadArtistsObservable = Single.error(Throwable("error")))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showArtistsError()
    }

    @Test
    fun onResume_getPlaylistsSubscription_success_showTaggedPlaylists() {
        // Given
        val mockLoadPlaylistsSubscription: Disposable = mock()
        tagPresenter.setSubscription(loadPlaylistsSubscription = mockLoadPlaylistsSubscription)
        tagPresenter.setObservable(loadPlaylistsObservable = Single.just(listOf(MockDataModel.mockPlaylist)))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showTaggedPlaylists(any(), any())
    }

    @Test
    fun onResume_getPlaylistsSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_playlistIsEmpty_hidePlaylistsSection() {
        // Given
        val mockLoadPlaylistsSubscription: Disposable = mock()
        tagPresenter.setPrivateData(playlists = mutableListOf())
        tagPresenter.setSubscription(loadPlaylistsSubscription = mockLoadPlaylistsSubscription)
        tagPresenter.setObservable(loadPlaylistsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).hidePlaylistsSection()
    }

    @Test
    fun onResume_getPlaylistsSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_playlistIsNotEmpty_showTaggedPlaylists() {
        // Given
        val mockLoadPlaylistsSubscription: Disposable = mock()
        tagPresenter.setPrivateData(playlists = mutableListOf(MockDataModel.mockPlaylist))
        tagPresenter.setSubscription(loadPlaylistsSubscription = mockLoadPlaylistsSubscription)
        tagPresenter.setObservable(loadPlaylistsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showTaggedPlaylists(any(), any())
    }

    @Test
    fun onResume_getPlaylistsSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showPlaylistError() {
        // Given
        val mockLoadPlaylistsSubscription: Disposable = mock()
        tagPresenter.setSubscription(loadPlaylistsSubscription = mockLoadPlaylistsSubscription)
        tagPresenter.setObservable(loadPlaylistsObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showPlaylistsError()
    }

    @Test
    fun onResume_getPlaylistsSubscription_fail_errorIsNotHttpException_showPlaylistsError() {
        // Given
        val mockLoadPlaylistsSubscription: Disposable = mock()
        tagPresenter.setSubscription(loadPlaylistsSubscription = mockLoadPlaylistsSubscription)
        tagPresenter.setObservable(loadPlaylistsObservable = Single.error(Throwable("error")))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showPlaylistsError()
    }

    @Test
    fun onResume_getStationsSubscription_success_showTaggedStations() {
        // Given
        val mockLoadStationsSubscription: Disposable = mock()
        tagPresenter.setSubscription(loadStationsSubscription = mockLoadStationsSubscription)
        tagPresenter.setObservable(loadStationsObservable = Single.just(listOf(MockDataModel.mockStation)))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showTaggedStations(any(), any())
    }

    @Test
    fun onResume_getStationsSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_stationsIsEmpty_hideStationSection() {
        // Given
        val mockLoadStationsSubscription: Disposable = mock()
        tagPresenter.setPrivateData(stations = mutableListOf())
        tagPresenter.setSubscription(loadStationsSubscription = mockLoadStationsSubscription)
        tagPresenter.setObservable(loadStationsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).hideStationsSection()
    }

    @Test
    fun onResume_getStationsSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_stationsIsNotEmpty_showTaggedStations() {
        // Given
        val mockLoadStationsSubscription: Disposable = mock()
        tagPresenter.setPrivateData(stations = mutableListOf(MockDataModel.mockStation))
        tagPresenter.setSubscription(loadStationsSubscription = mockLoadStationsSubscription)
        tagPresenter.setObservable(loadStationsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showTaggedStations(any(), any())
    }

    @Test
    fun onResume_getStationsSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showStationsError() {
        // Given
        val mockLoadStationsSubscription: Disposable = mock()
        tagPresenter.setSubscription(loadStationsSubscription = mockLoadStationsSubscription)
        tagPresenter.setObservable(loadStationsObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showStationsError()
    }

    @Test
    fun onResume_getStationsSubscription_fail_errorIsNotHttpException_errorCodeIsNotResourceNotFound_showStationsError() {
        // Given
        val mockLoadStationsSubscription: Disposable = mock()
        tagPresenter.setSubscription(loadStationsSubscription = mockLoadStationsSubscription)
        tagPresenter.setObservable(loadStationsObservable = Single.error(Throwable("error")))

        // When
        tagPresenter.onResume()

        // Then
        verify(view, times(1)).showStationsError()
    }

    @Test
    fun onPause_subscriptionIsNotNull_disposeSubscription() {
        // Given
        val mockGetTagContentSubscription: Disposable = mock()
        val mockLoadAlbumsSubscription: Disposable = mock()
        val mockLoadArtistsSubscription: Disposable = mock()
        val mockLoadPlaylistsSubscription: Disposable = mock()
        val mockLoadStationsSubscription: Disposable = mock()
        tagPresenter.setSubscription(
            getTagContentSubscription = mockGetTagContentSubscription,
            loadAlbumsSubscription = mockLoadAlbumsSubscription,
            loadArtistsSubscription = mockLoadArtistsSubscription,
            loadPlaylistsSubscription = mockLoadPlaylistsSubscription,
            loadStationsSubscription = mockLoadStationsSubscription
        )

        // When
        tagPresenter.onPause()

        // Then
        verify(mockGetTagContentSubscription, times(1)).dispose()
        verify(mockLoadAlbumsSubscription, times(1)).dispose()
        verify(mockLoadArtistsSubscription, times(1)).dispose()
        verify(mockLoadPlaylistsSubscription, times(1)).dispose()
        verify(mockLoadStationsSubscription, times(1)).dispose()
    }

    @Test
    fun onPause_subscriptionIsNull_doNothing() {
        // Given
        val mockGetTagContentSubscription: Disposable = mock()
        val mockLoadAlbumsSubscription: Disposable = mock()
        val mockLoadArtistsSubscription: Disposable = mock()
        val mockLoadPlaylistsSubscription: Disposable = mock()
        val mockLoadStationsSubscription: Disposable = mock()
        tagPresenter.setSubscription(
            getTagContentSubscription = null,
            loadAlbumsSubscription = null,
            loadArtistsSubscription = null,
            loadPlaylistsSubscription = null,
            loadStationsSubscription = null
        )

        // When
        tagPresenter.onPause()

        // Then
        verify(mockGetTagContentSubscription, times(0)).dispose()
        verify(mockLoadAlbumsSubscription, times(0)).dispose()
        verify(mockLoadArtistsSubscription, times(0)).dispose()
        verify(mockLoadPlaylistsSubscription, times(0)).dispose()
        verify(mockLoadStationsSubscription, times(0)).dispose()
    }

    @Test
    fun testOnPlaylistSelected() {
        // when
        tagPresenter.onPlaylistSelected(mockPlaylist)

        // then
        verify(router, times(1)).navigateToTaggedPlaylist(any())
    }

    @Test
    fun testOnArtistSelected() {
        // when
        tagPresenter.onArtistSelected(mockArtist)

        // then
        verify(router, times(1)).navigateToTaggedArtist(any())
    }

    @Test
    fun testOnAlbumSelected() {
        // when
        tagPresenter.onAlbumSelected(mockAlbum)

        // then
        verify(router, times(1)).navigateToTaggedAlbum(any())
    }

    @Test
    fun testOnStationSelected() {
        // when
        tagPresenter.onStationSelected(mockStation)

        // then
        verify(router, times(1)).navigateToTaggedStation(any())
    }

    @Test
    fun onLoadArtists_whenTrue() {
        // given
        setBundle()
        loadTagContent()

        // when
        tagPresenter.onStart(bundle)
        tagPresenter.onLoadArtists(doSubscribe = true, incremental = true)

        // then
        verify(tagFacade, times(1)).loadArtistsWithTag(any(), any(), any())
    }

    @Test
    fun onLoadArtists_whenFalse() {
        // given
        setBundle()
        loadTagContent()

        // when
        tagPresenter.onStart(bundle)
        tagPresenter.onLoadArtists(doSubscribe = false, incremental = false)

        // then
        verify(tagFacade, times(1)).loadArtistsWithTag(any(), any(), any())
    }

    @Test
    fun testLoadAlbums_whenTrue() {
        // given
        setBundle()
        loadTagContent()

        // when
        tagPresenter.onStart(bundle)
        tagPresenter.onLoadAlbums(doSubscribe = true, incremental = true)

        // then
        verify(tagFacade, times(1)).loadAlbumsWithTag(any(), any(), any())
    }

    @Test
    fun testLoadAlbums_whenFalse() {
        // given
        setBundle()
        loadTagContent()

        // when
        tagPresenter.onStart(bundle)
        tagPresenter.onLoadAlbums(doSubscribe = false, incremental = false)

        // then
        verify(tagFacade, times(1)).loadAlbumsWithTag(any(), any(), any())
    }

    @Test
    fun testLoadPlaylist_whenTrue() {
        // given
        setBundle()
        loadTagContent()

        // when
        tagPresenter.onStart(bundle)
        tagPresenter.onLoadPlaylists(doSubscribe = true, incremental = true)

        // then
        verify(tagFacade, times(1)).loadPlaylistsWithTag(any(), any(), any())
    }

    @Test
    fun testLoadPlaylist_whenFalse() {
        // given
        setBundle()
        loadTagContent()

        // when
        tagPresenter.onStart(bundle)
        tagPresenter.onLoadPlaylists(doSubscribe = false, incremental = false)

        // then
        verify(tagFacade, times(1)).loadPlaylistsWithTag(any(), any(), any())
    }

    @Test
    fun testLoadStation_whenTrue() {
        // given
        setBundle()
        loadTagContent()

        // when
        tagPresenter.onStart(bundle)
        tagPresenter.onLoadStations(doSubscribe = false, incremental = false)

        // then
        verify(tagFacade, times(1)).loadStationsWithTag(any(), any(), any())
    }

    @Test
    fun testLoadStation_whenFalse() {
        // given
        setBundle()
        loadTagContent()

        // when
        tagPresenter.onStart(bundle)
        tagPresenter.onLoadStations(doSubscribe = false, incremental = false)

        // then
        verify(tagFacade, times(1)).loadStationsWithTag(any(), any(), any())
    }

    @Test
    fun onArtistSeeAllSelected_tagIsNotNull_navigateToProductListWithTag() {
        // Given
        tagPresenter.setPrivateData(tag = MockDataModel.mockTag)

        // When
        tagPresenter.onArtistSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductListWithTag(any(), anyOrNull())
    }

    @Test
    fun onArtistSeeAllSelected_tagIsNull_navigateToProductListWithTag() {
        // Given
        tagPresenter.setPrivateData(tag = null)

        // When
        tagPresenter.onArtistSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductListWithTag(any(), anyOrNull())
    }

    @Test
    fun onAlbumSeeAllSelected_tagIsNotNull_navigateToProductListWithTag() {
        // Given
        tagPresenter.setPrivateData(tag = MockDataModel.mockTag)

        // When
        tagPresenter.onAlbumSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductListWithTag(any(), anyOrNull())
    }

    @Test
    fun onAlbumSeeAllSelected_tagIsNull_navigateToProductListWithTag() {
        // Given
        tagPresenter.setPrivateData(tag = null)

        // When
        tagPresenter.onAlbumSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductListWithTag(any(), anyOrNull())
    }

    @Test
    fun onPlaylistSeeAllSelected_tagIsNotNull_navigateToProductListWithTag() {
        // Given
        tagPresenter.setPrivateData(tag = MockDataModel.mockTag)

        // When
        tagPresenter.onPlaylistSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductListWithTag(any(), anyOrNull())
    }

    @Test
    fun onPlaylistSeeAllSelected_tagIsNull_navigateToProductListWithTag() {
        // Given
        tagPresenter.setPrivateData(tag = null)

        // When
        tagPresenter.onPlaylistSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductListWithTag(any(), anyOrNull())
    }

    @Test
    fun onStationSeeAllSelected_tagIsNotNull_navigateToProductListWithTag() {
        // Given
        tagPresenter.setPrivateData(tag = MockDataModel.mockTag)

        // When
        tagPresenter.onStationSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductListWithTag(any(), anyOrNull())
    }

    @Test
    fun onStationSeeAllSelected_tagIsNull_navigateToProductListWithTag() {
        // Given
        tagPresenter.setPrivateData(tag = null)

        // When
        tagPresenter.onStationSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductListWithTag(any(), anyOrNull())
    }

    @Test
    fun testOnRetryTaggedStation() {
        // given
        setBundle()
        loadTagContent()

        // when
        tagPresenter.onRetryTaggedStation()

        // then
        verify(tagFacade, times(1)).loadStationsWithTag(any(), any(), anyOrNull())
    }

    @Test
    fun testOnRetryTaggedArtist() {
        // given
        setBundle()
        loadTagContent()

        // when
        tagPresenter.onRetryTaggedArtist()

        // then
        verify(tagFacade, times(1)).loadArtistsWithTag(any(), any(), anyOrNull())
    }

    @Test
    fun testOnRetryTaggedAlbum() {
        // given
        setBundle()
        loadTagContent()

        // when
        tagPresenter.onRetryTaggedAlbum()

        // then
        verify(tagFacade, times(1)).loadAlbumsWithTag(any(), any(), anyOrNull())
    }

    @Test
    fun testOnRetryTaggedPlaylist() {
        // given
        setBundle()
        loadTagContent()

        // when
        tagPresenter.onRetryTaggedPlaylist()

        // then
        verify(tagFacade, times(1)).loadPlaylistsWithTag(any(), any(), anyOrNull())
    }

    @Test
    fun testGetTagContentByName() {
        // given
        doReturn("tag_name").whenever(bundle)
            .getString(TagPresenter.TAG_NAME_KEY)

        whenever(tagFacade.getTagByName(any())).thenReturn(
            Single.just(mockTag)
        )

        // when
        tagPresenter.onStart(bundle)

        // then
        verify(tagFacade, times(1)).getTagByName(any())
    }

    @Test
    fun testShowTagContentError() {
        // when
        tagPresenter.onStart(bundle)

        // then
        verify(view, times(1)).showTagContentError()
    }

    @AfterEach
    fun tearDown() {
        tagPresenter.onPause()
    }

    private fun setBundle() {
        doReturn(MockJson.jsonTag).whenever(bundle)
            .getString(TagPresenter.TAG_KEY)
        doReturn("name").whenever(bundle)
            .getString(TagPresenter.TAG_NAME_KEY)
        doReturn("key").whenever(bundle)
            .getString(TagPresenter.TAG_DISPLAY_TYPE_KEY)
        doReturn(true).whenever(bundle)
            .getBoolean(TagPresenter.DISPLAY_TITLE_KEY)
    }

    private fun loadTagContent() {
        whenever(tagFacade.loadArtistsWithTag(any(), any(), any())).thenReturn(
            Single.just(listOf(mockArtist))
        )
        whenever(tagFacade.loadAlbumsWithTag(any(), any(), any())).thenReturn(
            Single.just(listOf(mockAlbum))
        )
        whenever(tagFacade.loadPlaylistsWithTag(any(), any(), any())).thenReturn(
            Single.just(listOf(mockPlaylist))
        )
        whenever(tagFacade.loadStationsWithTag(any(), any(), any())).thenReturn(
            Single.just(listOf(mockStation))
        )
        whenever(tagFacade.loadStationsWithTag(any(), any(), any())).thenReturn(
            Single.just(listOf(mockStation))
        )
        whenever(tagFacade.getTagByName(any())).thenReturn(
            Single.just(mockTag)
        )
    }
}
