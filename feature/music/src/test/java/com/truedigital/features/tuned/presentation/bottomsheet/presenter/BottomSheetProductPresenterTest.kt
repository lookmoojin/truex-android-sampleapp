package com.truedigital.features.tuned.presentation.bottomsheet.presenter

import android.os.Bundle
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.artist.model.ArtistInfo
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.domain.facade.bottomsheetproduct.BottomSheetProductFacade
import com.truedigital.features.tuned.injection.module.NetworkModule
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.features.utils.MockDataModel
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.verify
import retrofit2.HttpException
import retrofit2.Response
import java.util.Date
import kotlin.test.assertEquals

internal class BottomSheetProductPresenterTest {

    private lateinit var bottomSheetProductPresenter: BottomSheetProductPresenter
    private val bottomSheetProductFacade: BottomSheetProductFacade = mock()
    private val router: BottomSheetProductPresenter.RouterSurface = mock()
    private val view: BottomSheetProductPresenter.ViewSurface = mock()
    private val mockBundle = Mockito.mock(Bundle::class.java)
    private val mockArtist = Artist(
        id = 1,
        name = "name",
        image = null
    )
    private val mockAlbum = Album(
        id = 1234,
        name = "album",
        artists = listOf(),
        primaryRelease = null,
        releaseIds = listOf()
    )
    private val mockPlaylist = Playlist(
        id = 1,
        name = listOf(),
        description = listOf(),
        creatorId = 2,
        creatorName = "creatorName",
        creatorImage = "creatorImage",
        trackCount = 3,
        publicTrackCount = 4,
        duration = 5,
        createDate = Date(),
        updateDate = Date(),
        trackIds = listOf(),
        coverImage = listOf()
    )

    private fun mockTrack(isVideo: Boolean, video: Track?) = Track(
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
        video = video,
        isVideo = isVideo,
        vote = null,
        isDownloaded = false,
        syncProgress = 1F,
        isCached = false,
        translationsList = listOf(
            Translation(
                language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_TH,
                value = "nameTh"
            )
        )
    )

    private fun mockRelease(artistList: List<ArtistInfo>) = Release(
        id = 1,
        albumId = 1,
        artists = artistList,
        name = "name",
        isExplicit = true,
        numberOfVolumes = 1, trackIds = listOf(),
        duration = 1,
        volumes = listOf(),
        image = "image",
        webPath = "webPath",
        copyRight = "copyRight",
        label = null,
        originalReleaseDate = null,
        digitalReleaseDate = null,
        physicalReleaseDate = null,
        saleAvailabilityDateTime = null,
        streamAvailabilityDateTime = null,
        allowStream = true,
        allowDownload = true
    )

    @BeforeEach
    fun setUp() {
        bottomSheetProductPresenter = BottomSheetProductPresenter(bottomSheetProductFacade)
    }

    @Test
    fun testTrackItemTypesList() {
        val result = BottomSheetProductPresenter.Companion.TRACK_ITEM_TYPES
        assertEquals(9, result.size)
    }

    // onStart /////////////////////////////////////////////////////////////////////////////////////
    @Test
    fun onStart_bundleNull_notVerifyAll() {
        bottomSheetProductPresenter.onInject(view, router, null, null)
        bottomSheetProductPresenter.onStart(null)

        verify(view, times(0)).showProduct(anyOrNull())
        verify(view, times(0)).showProductError()
        verify(view, times(0)).updateProductType(any())
        verify(view, times(0)).updateCollectionStatus(any())
        verify(view, times(0)).showArtistClearVote()
    }

    @Test
    fun onStart_productAndProductTypeNull_notVerifyGetProduct() {
        bottomSheetProductPresenter.onInject(view, router, null, null)
        whenever(mockBundle.getInt(BottomSheetProductPresenter.PRODUCT_ID_KEY)).thenReturn(1)
        whenever(mockBundle.getBoolean(BottomSheetProductPresenter.HAVE_COLLECTION_STATUS)).thenReturn(
            true
        )
        whenever(bottomSheetProductFacade.getProduct(anyInt(), anyOrNull())).thenReturn(
            Single.just(
                mockPlaylist
            )
        )

        bottomSheetProductPresenter.onStart(mockBundle)

        verify(bottomSheetProductFacade, times(0)).getProduct(anyInt(), anyOrNull())
    }

    @Test
    fun onStart_productNullAndProductTypeArtist_getProductSuccess_verifyGetProduct() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ARTIST)
        whenever(mockBundle.getInt(BottomSheetProductPresenter.PRODUCT_ID_KEY)).thenReturn(1)
        whenever(mockBundle.getBoolean(BottomSheetProductPresenter.HAVE_COLLECTION_STATUS)).thenReturn(
            true
        )
        whenever(bottomSheetProductFacade.getProduct(any(), anyOrNull())).thenReturn(
            Single.just(mockPlaylist)
        )

        bottomSheetProductPresenter.onStart(mockBundle)

        verify(bottomSheetProductFacade, times(1)).getProduct(any(), anyOrNull())
    }

    @Test
    fun onStart_productNullAndProductTypeArtist_getProductError_verifyGetProduct() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ARTIST)
        whenever(mockBundle.getInt(BottomSheetProductPresenter.PRODUCT_ID_KEY)).thenReturn(1)
        whenever(mockBundle.getBoolean(BottomSheetProductPresenter.HAVE_COLLECTION_STATUS)).thenReturn(
            true
        )
        whenever(bottomSheetProductFacade.getProduct(any(), anyOrNull())).thenReturn(
            Single.error(Throwable())
        )

        bottomSheetProductPresenter.onStart(mockBundle)

        verify(bottomSheetProductFacade, times(1)).getProduct(any(), anyOrNull())
    }

    @Test
    fun onStart_haveCollectionStatusTrue_productTypeArtist_verifyGetHasArtistAndSimilarStationReturnTrue() {
        bottomSheetProductPresenter.onInject(view, router, mockArtist, ProductPickerType.ARTIST)
        whenever(
            mockBundle.getBoolean(
                BottomSheetProductPresenter.HAVE_COLLECTION_STATUS,
                false
            )
        ).thenReturn(true)
        whenever(bottomSheetProductFacade.getHasArtistAndSimilarStation(anyInt())).thenReturn(
            Single.just(true)
        )

        bottomSheetProductPresenter.onStart(mockBundle)

        verify(bottomSheetProductFacade, times(1)).getHasArtistAndSimilarStation(anyInt())
    }

    @Test
    fun onStart_haveCollectionStatusTrue_productTypeArtist_verifyGetHasArtistAndSimilarStationReturnFalse() {
        bottomSheetProductPresenter.onInject(view, router, mockArtist, ProductPickerType.ARTIST)
        whenever(
            mockBundle.getBoolean(
                BottomSheetProductPresenter.HAVE_COLLECTION_STATUS,
                false
            )
        ).thenReturn(true)
        whenever(bottomSheetProductFacade.getHasArtistAndSimilarStation(anyInt())).thenReturn(
            Single.just(false)
        )

        bottomSheetProductPresenter.onStart(mockBundle)

        verify(bottomSheetProductFacade, times(1)).getHasArtistAndSimilarStation(anyInt())
    }

    @Test
    fun onStart_haveCollectionStatusTrue_productTypeArtist_verifyGetHasArtistAndSimilarStationReturnError() {
        bottomSheetProductPresenter.onInject(view, router, mockArtist, ProductPickerType.ARTIST)
        whenever(
            mockBundle.getBoolean(
                BottomSheetProductPresenter.HAVE_COLLECTION_STATUS,
                false
            )
        ).thenReturn(true)
        whenever(bottomSheetProductFacade.getHasArtistAndSimilarStation(anyInt())).thenReturn(
            Single.error(Throwable())
        )

        bottomSheetProductPresenter.onStart(mockBundle)

        verify(bottomSheetProductFacade, times(1)).getHasArtistAndSimilarStation(anyInt())
    }

    @Test
    fun onStart_haveCollectionStatusFalse_productTypeNull_notVerifyIsInCollection() {
        bottomSheetProductPresenter.onInject(view, router, mockAlbum, null)

        bottomSheetProductPresenter.onStart(mockBundle)

        verify(bottomSheetProductFacade, times(0)).isInCollection(anyInt(), anyOrNull())
    }

    @Test
    fun onStart_haveCollectionStatusFalse_isInCollectionTrue_verifyIsInCollection() {
        bottomSheetProductPresenter.onInject(view, router, mockAlbum, ProductPickerType.ALBUM)
        whenever(
            bottomSheetProductFacade.isInCollection(
                anyInt(),
                anyOrNull()
            )
        ).thenReturn(Single.just(true))

        bottomSheetProductPresenter.onStart(mockBundle)

        verify(bottomSheetProductFacade, times(1)).isInCollection(anyInt(), anyOrNull())
    }

    @Test
    fun onStart_haveCollectionStatusFalse_isInCollectionFalse_verifyIsInCollection() {
        bottomSheetProductPresenter.onInject(view, router, mockAlbum, ProductPickerType.ALBUM)
        whenever(
            bottomSheetProductFacade.isInCollection(
                anyInt(),
                anyOrNull()
            )
        ).thenReturn(Single.just(false))

        bottomSheetProductPresenter.onStart(mockBundle)

        verify(bottomSheetProductFacade, times(1)).isInCollection(anyInt(), anyOrNull())
    }

    @Test
    fun onStart_haveCollectionStatusFalse_isInCollectionError_verifyIsInCollection() {
        bottomSheetProductPresenter.onInject(view, router, mockAlbum, ProductPickerType.ALBUM)
        whenever(bottomSheetProductFacade.isInCollection(anyInt(), anyOrNull())).thenReturn(
            Single.error(Throwable())
        )

        bottomSheetProductPresenter.onStart(mockBundle)

        verify(bottomSheetProductFacade, times(1)).isInCollection(anyInt(), anyOrNull())
    }

    @Test
    fun getProductSubscription_success_showProduct() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableFirstSection(
            productObservable = Single.just(MockDataModel.mockAlbum)
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).showProduct(any())
    }

    @Test
    fun getProductSubscription_fail_showProductError() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableFirstSection(
            productObservable = Single.error(Exception())
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).showProductError()
    }

    @Test
    fun getIsInCollectionSubscription_success_inCollectionIsTrue_updateCollectionStatus() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableFirstSection(
            isInCollectionObservable = Single.just(true)
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).updateCollectionStatus(any())
    }

    @Test
    fun getIsInCollectionSubscription_success_inCollectionIsFalse_updateCollectionStatus() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableFirstSection(
            isInCollectionObservable = Single.just(false)
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).updateCollectionStatus(any())
    }

    @Test
    fun getIsInCollectionSubscription_fail_doNothing() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableFirstSection(
            isInCollectionObservable = Single.error(Exception())
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(0)).updateCollectionStatus(any())
    }

    @Test
    fun getHasArtistMixSubscription_success_hasArtistMix_showArtistClearVote() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableFirstSection(
            hasArtistMixObservable = Single.just(true)
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).showArtistClearVote()
    }

    @Test
    fun getHasArtistMixSubscription_success_hasNotArtistMix_doNothing() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableFirstSection(
            hasArtistMixObservable = Single.just(false)
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(0)).showArtistClearVote()
    }

    @Test
    fun getHasArtistMixSubscription_fail_doNothing() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableFirstSection(
            hasArtistMixObservable = Single.error(Exception())
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(0)).showArtistClearVote()
    }

    @Test
    fun getHasArtistMixSubscription_success_showFavouriteToast() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableFirstSection(
            addToCollectionObservable = Single.just(Any())
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).showFavouritedToast(true)
        verify(view, times(1)).inCollectionStatusChanged(any())
    }

    @Test
    fun getHasArtistMixSubscription_fail_showFavouriteError() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableFirstSection(
            addToCollectionObservable = Single.error(Exception())
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).showFavouritedError(true)
    }

    @Test
    fun getRemoveFromCollectionSubscription_success_showCollectionStatusChange() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableFirstSection(
            removeFromCollectionObservable = Single.just(Any())
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).inCollectionStatusChanged(any())
        verify(view, times(1)).showFavouritedToast(false)
    }

    @Test
    fun getRemoveFromCollectionSubscription_fail_showFavouriteError() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableFirstSection(
            removeFromCollectionObservable = Single.error(Exception())
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).showFavouritedError(false)
    }

    @Test
    fun getPlayTracksSubscription_success_playableTracksIsNotEmpty_addToQueue() {
        val mockTrack = MockDataModel.mockTrack.copy(allowStream = true)
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableSecondSection(
            tracksObservable = Single.just(listOf(mockTrack))
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).addToQueue(any(), any())
    }

    @Test
    fun getPlayTracksSubscription_success_playableTracksIsEmpty_showGenericErrorMessage() {
        val mockTrack = MockDataModel.mockTrack.copy(allowStream = false)
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableSecondSection(
            tracksObservable = Single.just(listOf(mockTrack))
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).showGenericErrorMessage()
    }

    @Test
    fun getPlayTracksSubscription_fail_showGenericErrorMessage() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setSubscription()
        bottomSheetProductPresenter.setObservableSecondSection(
            tracksObservable = Single.error(Exception())
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).showGenericErrorMessage()
    }

    @Test
    fun getClearVotesSubscription_success_showVotesCleared() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableSecondSection(
            clearVotesObservable = Single.just(Any())
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).showVotesCleared()
    }

    @Test
    fun getClearVotesSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_showVotesCleared() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableSecondSection(
            clearVotesObservable = Single.error(
                HttpException(
                    Response.error<Any>(
                        NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND,
                        "".toResponseBody("application/json".toMediaTypeOrNull())
                    )
                )
            )
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).showVotesCleared()
    }

    @Test
    fun getClearVotesSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showClearVotesError() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableSecondSection(
            clearVotesObservable = Single.error(
                HttpException(
                    Response.error<Any>(
                        NetworkModule.HTTP_CODE_UNAUTHORISED,
                        "".toResponseBody("application/json".toMediaTypeOrNull())
                    )
                )
            )
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).showClearVotesError()
    }

    @Test
    fun getClearVotesSubscription_fail_errorIsNotHttpException_showClearVotesError() {
        bottomSheetProductPresenter.onInject(view, router, null, ProductPickerType.ALBUM)
        bottomSheetProductPresenter.setObservableSecondSection(
            clearVotesObservable = Single.error(Exception())
        )

        bottomSheetProductPresenter.testSubscription()

        verify(view, times(1)).showClearVotesError()
    }

    @Test
    fun onStop_subscriptionIsNotNull_disposeSubscription() {
        val mockProductSubscription: Disposable = mock()
        val mockIsInCollectionSubscription: Disposable = mock()
        val mockHasArtistMixSubscription: Disposable = mock()

        bottomSheetProductPresenter.setSubscription(
            productSubscription = mockProductSubscription,
            isInCollectionSubscription = mockIsInCollectionSubscription,
            hasArtistMixSubscription = mockHasArtistMixSubscription
        )

        bottomSheetProductPresenter.onStop()

        verify(mockProductSubscription, times(1)).dispose()
        verify(mockIsInCollectionSubscription, times(1)).dispose()
        verify(mockHasArtistMixSubscription, times(1)).dispose()
    }

    @Test
    fun onStop_subscriptionIsNull_doNothing() {
        val mockProductSubscription: Disposable = mock()
        val mockIsInCollectionSubscription: Disposable = mock()
        val mockHasArtistMixSubscription: Disposable = mock()

        bottomSheetProductPresenter.setSubscription(
            productSubscription = null,
            isInCollectionSubscription = null,
            hasArtistMixSubscription = null
        )

        bottomSheetProductPresenter.onStop()

        verify(mockProductSubscription, times(0)).dispose()
        verify(mockIsInCollectionSubscription, times(0)).dispose()
        verify(mockHasArtistMixSubscription, times(0)).dispose()
    }

    // adjustProductType ///////////////////////////////////////////////////////////////////////////
    @Test
    fun testAdjustProductType_productTypeNull_notVerifyUpdateProductType() {
        bottomSheetProductPresenter.onInject(view, router, mockPlaylist, null)
        bottomSheetProductPresenter.adjustProductType()

        verify(bottomSheetProductFacade, times(0)).checkPlaylistType(any())
        verify(view, times(0)).updateProductType(any())
    }

    @Test
    fun testAdjustProductType_productTypePlaylist_verifyCheckPlaylistType() {
        val mockType = ProductPickerType.ALBUM
        bottomSheetProductPresenter.onInject(view, router, mockPlaylist, ProductPickerType.PLAYLIST)
        whenever(bottomSheetProductFacade.checkPlaylistType(any())).thenReturn(
            Single.just(mockType)
        )

        bottomSheetProductPresenter.adjustProductType()

        verify(bottomSheetProductFacade, times(1)).checkPlaylistType(any())
        verify(view, times(1)).updateProductType(mockType)
    }

    @Test
    fun testAdjustProductType_productTypeNotPlaylist_notVerifyCheckPlaylistType() {
        val mockType = ProductPickerType.ALBUM
        bottomSheetProductPresenter.onInject(view, router, mockPlaylist, mockType)

        bottomSheetProductPresenter.adjustProductType()

        verify(bottomSheetProductFacade, times(0)).checkPlaylistType(any())
        verify(view, times(1)).updateProductType(mockType)
    }

    // onOptionsSelected - other ///////////////////////////////////////////////////////////////////
    @Test
    fun testOnOptionsSelected_otherPickerOptions_notVerifyAll() {
        bottomSheetProductPresenter.onInject(view, router, null, null)
        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_PLAYLIST)

        verify(view, times(0)).updateCollectionStatus(any())
        verify(view, times(0)).showArtistClearVote()
        verify(view, times(0)).showArtistClearVote()
        verify(view, times(0)).inCollectionStatusChanged(any())
        verify(view, times(0)).addToQueue(any(), any())
        verify(view, times(0)).removeFromQueue()
        verify(view, times(0)).playVideo(any())
        verify(view, times(0)).showVotesCleared()
        verify(view, times(0)).showClearVotesError()
    }

    // onOptionsSelected - addToCollection /////////////////////////////////////////////////////////
    @Test
    fun testOnOptionsSelectedAddToCollection_productTypeNotNull_verifyAddToCollectionReturnSuccess() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockPlaylist,
            ProductPickerType.PLAYLIST
        )
        whenever(
            bottomSheetProductFacade.addToCollection(
                anyInt(),
                anyOrNull()
            )
        ).thenReturn(Single.just(Any()))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_COLLECTION)

        verify(bottomSheetProductFacade, times(1)).addToCollection(anyInt(), anyOrNull())
    }

    @Test
    fun testOnOptionsSelectedAddToCollection_productTypeNotNull_verifyAddToCollectionReturnError() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockPlaylist,
            ProductPickerType.PLAYLIST
        )
        whenever(
            bottomSheetProductFacade.addToCollection(
                anyInt(),
                anyOrNull()
            )
        ).thenReturn(Single.error(Throwable()))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_COLLECTION)

        verify(bottomSheetProductFacade, times(1)).addToCollection(anyInt(), anyOrNull())
    }

    @Test
    fun testOnOptionsSelectedAddToCollection_productTypeNull_notVerifyAddToCollectionReturn() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockPlaylist,
            null
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_COLLECTION)

        verify(bottomSheetProductFacade, times(0)).addToCollection(anyInt(), anyOrNull())
    }

    // onOptionsSelected - removeToCollection //////////////////////////////////////////////////////
    @Test
    fun testOnOptionsSelectedRemoveToCollection_productTypeNotNull_verifyRemoveToCollectionReturnSuccess() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockPlaylist,
            ProductPickerType.PLAYLIST
        )
        whenever(
            bottomSheetProductFacade.removeFromCollection(
                anyInt(),
                anyOrNull()
            )
        ).thenReturn(Single.just(Any()))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.REMOVE_FROM_COLLECTION)

        verify(bottomSheetProductFacade, times(1)).removeFromCollection(anyInt(), anyOrNull())
    }

    @Test
    fun testOnOptionsSelectedRemoveToCollection_productTypeNotNull_verifyRemoveToCollectionReturnError() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockPlaylist,
            ProductPickerType.PLAYLIST
        )
        whenever(
            bottomSheetProductFacade.removeFromCollection(
                anyInt(),
                anyOrNull()
            )
        ).thenReturn(Single.error(Throwable()))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.REMOVE_FROM_COLLECTION)

        verify(bottomSheetProductFacade, times(1)).removeFromCollection(anyInt(), anyOrNull())
    }

    @Test
    fun testOnOptionsSelectedRemoveToCollection_productTypeNull_notVerifyRemoveToCollectionReturn() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockPlaylist,
            null
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.REMOVE_FROM_COLLECTION)

        verify(bottomSheetProductFacade, times(0)).removeFromCollection(anyInt(), anyOrNull())
    }

    // onOptionsSelected - follow //////////////////////////////////////////////////////////////////
    @Test
    fun testOnOptionsSelectedFollow_productNull_verifyAddToCollectionReturnSuccess() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            null,
            ProductPickerType.PLAYLIST
        )
        whenever(
            bottomSheetProductFacade.addToCollection(
                anyInt(),
                anyOrNull()
            )
        ).thenReturn(Single.just(Any()))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.FOLLOW)

        verify(bottomSheetProductFacade, times(1)).addToCollection(anyInt(), anyOrNull())
    }

    // onOptionsSelected - unfollow ////////////////////////////////////////////////////////////////
    @Test
    fun testOnOptionsSelectedUnFollow_productNull_verifyRemoveToCollectionReturnSuccess() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            null,
            ProductPickerType.PLAYLIST
        )
        whenever(
            bottomSheetProductFacade.removeFromCollection(
                anyInt(),
                anyOrNull()
            )
        ).thenReturn(Single.just(Any()))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.UNFOLLOW)

        verify(bottomSheetProductFacade, times(1)).removeFromCollection(anyInt(), anyOrNull())
    }

    // onOptionsSelected - addToQueue //////////////////////////////////////////////////////////////
    @Test
    fun testOnOptionsSelectedAddToQueue_productTypeNull_verifyIllegalStateException() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            null,
            null
        )

        Assert.assertThrows(IllegalStateException::class.java) {
            bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_QUEUE)
        }
    }

    @Test
    fun testOnOptionsSelectedAddToQueue_containsProductType_verifyAddToQueue() {
        val mockTrack = mockTrack(true, null)
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack,
            ProductPickerType.SONG
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_QUEUE)

        verify(view, times(1)).addToQueue(listOf(mockTrack), false)
    }

    @Test
    fun testOnOptionsSelectedAddToQueue_productTypeAlbumAndProductNull_notVerifyGetTracksForProductReturnListNotEmpty() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            null,
            ProductPickerType.ALBUM
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_QUEUE)

        verify(bottomSheetProductFacade, times(0)).getTracksForProduct(anyInt(), anyOrNull())
    }

    @Test
    fun testOnOptionsSelectedAddToQueue_productTypeAlbum_verifyGetTracksForProductReturnListNotEmpty() {
        val mockTrack = mockTrack(true, null)
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack,
            ProductPickerType.ALBUM
        )
        whenever(
            bottomSheetProductFacade.getTracksForProduct(anyInt(), anyOrNull())
        ).thenReturn(Single.just(listOf(mockTrack)))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_QUEUE)

        verify(bottomSheetProductFacade, times(1)).getTracksForProduct(anyInt(), anyOrNull())
    }

    @Test
    fun testOnOptionsSelectedAddToQueue_productTypeAlbumAllowStreamTrue_verifyGetTracksForProductReturnListNotEmpty() {
        val mockTrack = mockTrack(true, null)
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack,
            ProductPickerType.ALBUM
        )
        whenever(
            bottomSheetProductFacade.getTracksForProduct(anyInt(), anyOrNull())
        ).thenReturn(
            Single.just(listOf(mockTrack.apply { allowStream = true }))
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_QUEUE)

        verify(bottomSheetProductFacade, times(1)).getTracksForProduct(anyInt(), anyOrNull())
    }

    @Test
    fun testOnOptionsSelectedAddToQueue_productTypeAlbum_verifyGetTracksForProductReturnListEmpty() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack(true, null),
            ProductPickerType.ALBUM
        )
        whenever(
            bottomSheetProductFacade.getTracksForProduct(anyInt(), anyOrNull())
        ).thenReturn(Single.just(listOf()))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_QUEUE)

        verify(bottomSheetProductFacade, times(1)).getTracksForProduct(anyInt(), anyOrNull())
    }

    @Test
    fun testOnOptionsSelectedAddToQueue_productTypeAlbum_verifyGetTracksForProductReturnError() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack(true, null),
            ProductPickerType.ALBUM
        )
        whenever(
            bottomSheetProductFacade.getTracksForProduct(anyInt(), anyOrNull())
        ).thenReturn(Single.error(Throwable()))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_QUEUE)

        verify(bottomSheetProductFacade, times(1)).getTracksForProduct(anyInt(), anyOrNull())
    }

    @Test
    fun testOnOptionsSelectedAddToQueue_productTypePlaylist_verifyGetTracksForProduct() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack(true, null),
            ProductPickerType.PLAYLIST
        )
        whenever(
            bottomSheetProductFacade.getTracksForProduct(anyInt(), anyOrNull())
        ).thenReturn(Single.just(listOf()))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_QUEUE)

        verify(bottomSheetProductFacade, times(1)).getTracksForProduct(anyInt(), anyOrNull())
    }

    @Test
    fun testOnOptionsSelectedAddToQueue_productTypePlaylistOwner_verifyGetTracksForProduct() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack(true, null),
            ProductPickerType.PLAYLIST_OWNER
        )
        whenever(
            bottomSheetProductFacade.getTracksForProduct(anyInt(), anyOrNull())
        ).thenReturn(Single.just(listOf()))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_QUEUE)

        verify(bottomSheetProductFacade, times(1)).getTracksForProduct(anyInt(), anyOrNull())
    }

    @Test
    fun testOnOptionsSelectedAddToQueue_productTypePlaylistVerifiedOwner_verifyGetTracksForProduct() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack(true, null),
            ProductPickerType.PLAYLIST_VERIFIED_OWNER
        )
        whenever(
            bottomSheetProductFacade.getTracksForProduct(anyInt(), anyOrNull())
        ).thenReturn(Single.just(listOf()))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_QUEUE)

        verify(bottomSheetProductFacade, times(1)).getTracksForProduct(anyInt(), anyOrNull())
    }

    // onOptionsSelected - removeFromQueue /////////////////////////////////////////////////////////
    @Test
    fun testOnOptionsSelectedRemoveFromQueue_verifyRemoveFromQueue() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            null,
            null
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.REMOVE_FROM_QUEUE)

        verify(view, times(1)).removeFromQueue()
    }

    // onOptionsSelected - showArtist //////////////////////////////////////////////////////////////
    @Test
    fun testOnOptionsSelectedShowArtist_productTypeNull_verifyIllegalStateException() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            null,
            null
        )

        Assert.assertThrows(IllegalStateException::class.java) {
            bottomSheetProductPresenter.onOptionsSelected(PickerOptions.SHOW_ARTIST)
        }
    }

    @Test
    fun testOnOptionsSelectedShowArtist_containsProductTypeAndArtistsEmpty_notVerifyShowArtist() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack(true, null),
            ProductPickerType.SONG
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.SHOW_ARTIST)

        verify(router, times(0)).showArtist(anyInt())
    }

    @Test
    fun testOnOptionsSelectedShowArtist_containsProductTypeAndArtistsNotEmpty_verifyShowArtist() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack(true, null)
                .apply {
                    artists = listOf(
                        ArtistInfo(id = 1, name = "name1"),
                        ArtistInfo(id = 2, name = "name2")
                    )
                },
            ProductPickerType.SONG
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.SHOW_ARTIST)

        verify(router, times(1)).showArtist(1)
    }

    @Test
    fun testOnOptionsSelectedShowArtist_productTypeAlbumAndPrimaryReleaseNull_notVerifyShowArtist() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockAlbum,
            ProductPickerType.ALBUM
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.SHOW_ARTIST)

        verify(router, times(0)).showArtist(1)
    }

    @Test
    fun testOnOptionsSelectedShowArtist_productTypeAlbumAndProductIsReleaseArtistsEmpty_notVerifyShowArtist() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockRelease(listOf()),
            ProductPickerType.ALBUM
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.SHOW_ARTIST)

        verify(router, times(0)).showArtist(1)
    }

    @Test
    fun testOnOptionsSelectedShowArtist_productTypeAlbumAndProductIsReleaseArtistsNotEmpty_verifyShowArtist() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockRelease(
                listOf(
                    ArtistInfo(id = 1, name = "name1"),
                    ArtistInfo(id = 2, name = "name2")
                )
            ),
            ProductPickerType.ALBUM
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.SHOW_ARTIST)

        verify(router, times(1)).showArtist(1)
    }

    // onOptionsSelected - showAlbum ///////////////////////////////////////////////////////////////
    @Test
    fun testOnOptionsSelectedShowAlbum_productTypeNull_verifyIllegalStateException() {
        bottomSheetProductPresenter.onInject(view, router, null, null)

        Assert.assertThrows(IllegalStateException::class.java) {
            bottomSheetProductPresenter.onOptionsSelected(PickerOptions.SHOW_ALBUM)
        }
    }

    @Test
    fun testOnOptionsSelectedShowAlbum_containsProductType_verifyRouteToShowAlbum() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack(true, null),
            ProductPickerType.ALBUM_SONG
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.SHOW_ALBUM)

        verify(router, times(1)).showAlbum(1)
    }

    // onOptionsSelected - playerSetting ///////////////////////////////////////////////////////////
    @Test
    fun testOnOptionsSelectedPlayerSetting_verifyRouteToShowPlayerSetting() {
        bottomSheetProductPresenter.onInject(view, router, null, null)

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.PLAYER_SETTINGS)

        verify(router, times(1)).showPlayerSetting()
    }

    // onOptionsSelected - playVideo ///////////////////////////////////////////////////////////////
    @Test
    fun testOnOptionsSelectedPlayVideo_isVideoTrue_verifyPlayVideo() {
        val mockTrack = mockTrack(true, null)
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack,
            ProductPickerType.VIDEO_PLAYER
        )
        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.PLAY_VIDEO)

        verify(view, times(1)).playVideo(mockTrack)
    }

    @Test
    fun testOnOptionsSelectedPlayVideo_videoNotNull_verifyPlayVideo() {
        val mockTrack = mockTrack(false, null)
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack(false, mockTrack),
            ProductPickerType.VIDEO_PLAYER
        )
        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.PLAY_VIDEO)

        verify(view, times(1)).playVideo(mockTrack)
    }

    @Test
    fun testOnOptionsSelectedPlayVideo_videoNull_verifyIllegalStateException() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack(false, null),
            ProductPickerType.VIDEO_PLAYER
        )

        Assert.assertThrows(IllegalStateException::class.java) {
            bottomSheetProductPresenter.onOptionsSelected(PickerOptions.PLAY_VIDEO)
        }
    }

    @Test
    fun testOnOptionsSelectedPlayVideo_productTypeNotMatch_verifyIllegalStateException() {
        val mockTrack = mockTrack(false, null)
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack(true, mockTrack),
            ProductPickerType.ALBUM
        )

        Assert.assertThrows(IllegalStateException::class.java) {
            bottomSheetProductPresenter.onOptionsSelected(PickerOptions.PLAY_VIDEO)
        }
    }

    // onOptionsSelected - onClearVotes ////////////////////////////////////////////////////////////
    @Test
    fun testOnOptionsSelectedClearVote_productTypeNotMatch_verifyIllegalStateException() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockTrack(false, null),
            ProductPickerType.VIDEO_PLAYER
        )

        Assert.assertThrows(IllegalStateException::class.java) {
            bottomSheetProductPresenter.onOptionsSelected(PickerOptions.CLEAR_VOTE)
        }
    }

    @Test
    fun testOnOptionsSelectedClearVote_productTypeArtist_verifyClearVotes() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockArtist,
            ProductPickerType.ARTIST
        )
        whenever(
            bottomSheetProductFacade.clearVotes(
                anyInt(),
                anyOrNull(),
                anyString()
            )
        ).thenReturn(Single.just(Any()))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.CLEAR_VOTE)

        verify(bottomSheetProductFacade, times(1)).clearVotes(
            anyInt(),
            anyOrNull(),
            anyString()
        )
    }

    @Test
    fun testOnOptionsSelectedClearVote_productTypeMix_verifyClearVotes() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockArtist,
            ProductPickerType.MIX
        )
        whenever(
            bottomSheetProductFacade.clearVotes(
                anyInt(),
                anyOrNull(),
                anyString()
            )
        ).thenReturn(Single.just(Any()))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.CLEAR_VOTE)

        verify(bottomSheetProductFacade, times(1)).clearVotes(
            anyInt(),
            anyOrNull(),
            anyString()
        )
    }

    @Test
    fun testOnOptionsSelectedClearVote_productNull_notVerifyClearVotes() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            null,
            ProductPickerType.ARTIST
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.CLEAR_VOTE)

        verify(bottomSheetProductFacade, times(0)).clearVotes(
            anyInt(),
            anyOrNull(),
            anyString()
        )
    }

    @Test
    fun testOnOptionsSelectedClearVote_clearVotesApiException_verifyClearVotes() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockArtist,
            ProductPickerType.MIX
        )
        whenever(
            bottomSheetProductFacade.clearVotes(
                anyInt(),
                anyOrNull(),
                anyString()
            )
        ).thenReturn(Single.error(Exception()))

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.CLEAR_VOTE)

        verify(bottomSheetProductFacade, times(1)).clearVotes(
            anyInt(),
            anyOrNull(),
            anyString()
        )
    }

    @Test
    fun testOnOptionsSelectedClearVote_clearVotesApiHttpExceptionCode404_verifyClearVotes() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockArtist,
            ProductPickerType.MIX
        )
        whenever(
            bottomSheetProductFacade.clearVotes(
                anyInt(),
                anyOrNull(),
                anyString()
            )
        ).thenReturn(
            Single.error(
                HttpException(
                    Response.error<Any>(
                        NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND,
                        "".toResponseBody("application/json".toMediaTypeOrNull())
                    )
                )
            )
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.CLEAR_VOTE)

        verify(bottomSheetProductFacade, times(1)).clearVotes(
            anyInt(),
            anyOrNull(),
            anyString()
        )
    }

    @Test
    fun testOnOptionsSelectedClearVote_clearVotesApiHttpExceptionCode500_verifyClearVotes() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockArtist,
            ProductPickerType.MIX
        )
        whenever(
            bottomSheetProductFacade.clearVotes(
                anyInt(),
                anyOrNull(),
                anyString()
            )
        ).thenReturn(
            Single.error(
                HttpException(
                    Response.error<Any>(
                        500,
                        "".toResponseBody("application/json".toMediaTypeOrNull())
                    )
                )
            )
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.CLEAR_VOTE)

        verify(bottomSheetProductFacade, times(1)).clearVotes(
            anyInt(),
            anyOrNull(),
            anyString()
        )
    }

    @Test
    fun onOptionSelected_itemTypeIsAddToMyPlaylist_showAddToMyPlaylist() {
        bottomSheetProductPresenter.onInject(
            view,
            router,
            mockArtist,
            ProductPickerType.MIX
        )

        bottomSheetProductPresenter.onOptionsSelected(PickerOptions.ADD_TO_MY_PLAYLIST)

        verify(view, times(1)).showAddToMyPlaylist()
    }
}
