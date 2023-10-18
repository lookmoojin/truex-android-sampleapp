package com.truedigital.features.music.presentation.myplaylist

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.domain.myplaylist.model.MyPlaylistItemType
import com.truedigital.features.music.domain.myplaylist.model.MyPlaylistModel
import com.truedigital.features.music.domain.myplaylist.usecase.DeleteMyPlaylistUseCase
import com.truedigital.features.music.domain.myplaylist.usecase.GetMyPlaylistTrackUseCase
import com.truedigital.features.music.domain.myplaylist.usecase.GetMyPlaylistUseCase
import com.truedigital.features.music.domain.track.usecase.RemoveTrackUseCase
import com.truedigital.features.music.domain.usecase.router.MusicRouterUseCase
import com.truedigital.features.music.domain.warning.model.MusicWarningType
import com.truedigital.features.music.navigation.router.MusicAddSong
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.features.utils.MockDataModel
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertNull

@ExtendWith(InstantTaskExecutorExtension::class)
internal class MyPlaylistViewModelTest {

    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    private lateinit var myPlaylistViewModel: MyPlaylistViewModel
    private val router: MusicRouterUseCase = mock()
    private val getMyPlaylistUseCase: GetMyPlaylistUseCase = mock()
    private val getMyPlaylistTrackUseCase: GetMyPlaylistTrackUseCase = mock()
    private val deleteMyPlaylistUseCase: DeleteMyPlaylistUseCase = mock()
    private val removeTrackUseCase: RemoveTrackUseCase = mock()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)

    private val mockTrack1 = MockDataModel.mockTrack.copy(id = 1)
    private val mockTrack2 = MockDataModel.mockTrack.copy(id = 2)
    private val mockTrack3 = MockDataModel.mockTrack.copy(id = 3)
    private val mockTrack4 = MockDataModel.mockTrack.copy(id = 4)
    private val mockTrack5 = MockDataModel.mockTrack.copy(id = 5)

    private val mockMyPlaylistModel = MyPlaylistModel(
        id = 10,
        coverImage = "coverImage",
        playlistName = "playlistName",
        count = 10,
        itemType = MyPlaylistItemType.HEADER
    )

    @BeforeEach
    fun setUp() {
        myPlaylistViewModel = MyPlaylistViewModel(
            router = router,
            coroutineDispatcher = coroutineDispatcher,
            getMyPlaylistUseCase = getMyPlaylistUseCase,
            getMyPlaylistTrackUseCase = getMyPlaylistTrackUseCase,
            deleteMyPlaylistUseCase = deleteMyPlaylistUseCase,
            removeTrackUseCase = removeTrackUseCase
        )
    }

    @Test
    fun enableLoading_isEnableTrue_returnTrue() {
        myPlaylistViewModel.enableLoading(true)
        assertTrue(myPlaylistViewModel.getIsLoadingState())
    }

    @Test
    fun loadMyPlaylist_getMyPlaylistSuccess_getMyPlaylistTrackSuccess_trackIsNotEmpty_renderMyPlaylistAndMyPlaylistTrack() =
        runTest {
            // Given
            whenever(getMyPlaylistUseCase.execute(any())).thenReturn(flowOf(mockMyPlaylistModel))
            whenever(getMyPlaylistTrackUseCase.execute(any(), any())).thenReturn(
                flowOf(listOf(MockDataModel.mockTrack))
            )

            // When
            myPlaylistViewModel.loadMyPlaylist(1)

            // Then
            assertEquals(myPlaylistViewModel.onShowLoading().getOrAwaitValue(), Unit)
            assertEquals(myPlaylistViewModel.onHideLoading().getOrAwaitValue(), Unit)
            assertEquals(
                myPlaylistViewModel.onRenderMyPlaylistTrack().getOrAwaitValue().first(),
                MockDataModel.mockTrack
            )
            assertEquals(
                myPlaylistViewModel.onRenderMyPlaylist().getOrAwaitValue().first(),
                mockMyPlaylistModel.copy(isTrackNotEmpty = true)
            )
            verify(getMyPlaylistUseCase, times(1)).execute(any())
            verify(getMyPlaylistTrackUseCase, times(1)).execute(any(), any())
        }

    @Test
    fun loadMyPlaylist_getMyPlaylistSuccess_getMyPlaylistTrackSuccess_trackIsEmpty_renderMyPlaylist() =
        runTest {
            // Given
            whenever(getMyPlaylistUseCase.execute(any())).thenReturn(flowOf(mockMyPlaylistModel))
            whenever(getMyPlaylistTrackUseCase.execute(any(), any())).thenReturn(
                flowOf(listOf())
            )

            // When
            myPlaylistViewModel.loadMyPlaylist(1)

            // Then
            assertEquals(myPlaylistViewModel.onShowLoading().getOrAwaitValue(), Unit)
            assertEquals(myPlaylistViewModel.onHideLoading().getOrAwaitValue(), Unit)
            assertEquals(
                myPlaylistViewModel.onRenderMyPlaylist().getOrAwaitValue().getOrNull(0),
                mockMyPlaylistModel
            )
            myPlaylistViewModel.onRenderMyPlaylist().getOrAwaitValue().getOrNull(1).also {
                assertEquals(it?.itemId, 2)
                assertEquals(it?.itemType, MyPlaylistItemType.PLAYLIST_EMPTY)
            }
            verify(getMyPlaylistUseCase, times(1)).execute(any())
            verify(getMyPlaylistTrackUseCase, times(1)).execute(any(), any())
        }

    @Test
    fun loadMyPlaylist_getMyPlaylistFail_showErrorDialog() = runTest {
        // When
        myPlaylistViewModel.loadMyPlaylist(1)

        // Then
        assertEquals(myPlaylistViewModel.onShowLoading().getOrAwaitValue(), Unit)
        assertEquals(myPlaylistViewModel.onHideLoading().getOrAwaitValue(), Unit)
        assertEquals(myPlaylistViewModel.onShowPlaylistErrorDialog().getOrAwaitValue(), Unit)
        verify(getMyPlaylistUseCase, times(1)).execute(any())
    }

    @Test
    fun reloadMyPlaylistTrack_success_previousTrackListIsNotEqualsTrackList_doNothing() {
        // Given
        val mockTrackList = listOf(MockDataModel.mockTrack.copy(id = 111))
        myPlaylistViewModel.setPrivateData(previousTrackList = mockTrackList)
        whenever(getMyPlaylistUseCase.execute(any())).thenReturn(flowOf(mockMyPlaylistModel))
        whenever(getMyPlaylistTrackUseCase.execute(any(), any())).thenReturn(
            flowOf(
                listOf(
                    MockDataModel.mockTrack
                )
            )
        )

        // When
        myPlaylistViewModel.reloadMyPlaylistTrack(1)

        // Then
        verify(getMyPlaylistTrackUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun reloadMyPlaylistTrack_success_previousTrackListIsEqualsTrackList_doNothing() {
        // Given
        myPlaylistViewModel.setPrivateData(previousTrackList = emptyList())
        whenever(getMyPlaylistUseCase.execute(any())).thenReturn(flowOf(mockMyPlaylistModel))
        whenever(getMyPlaylistTrackUseCase.execute(any(), any())).thenReturn(flowOf(emptyList()))

        // When
        myPlaylistViewModel.reloadMyPlaylistTrack(1)

        // Then
        verify(getMyPlaylistTrackUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun reloadMyPlaylistTrack_success_previousTrackListNotEqualsTrackList_renderMyPlaylistTrackAndUploadCoverImage() {
        // Given
        val mockTrackList = listOf(MockDataModel.mockTrack)
        myPlaylistViewModel.setPrivateData(previousTrackList = emptyList())
        whenever(getMyPlaylistUseCase.execute(any())).thenReturn(flowOf(mockMyPlaylistModel))
        whenever(getMyPlaylistTrackUseCase.execute(any(), any())).thenReturn(
            flowOf(mockTrackList)
        )

        // When
        myPlaylistViewModel.reloadMyPlaylistTrack(1)

        // Then
        assertEquals(myPlaylistViewModel.onShowLoading().getOrAwaitValue(), Unit)
        assertEquals(myPlaylistViewModel.onHideLoading().getOrAwaitValue(), Unit)
        assertEquals(
            myPlaylistViewModel.onRenderMyPlaylistTrack().getOrAwaitValue().first(),
            MockDataModel.mockTrack
        )
        verify(getMyPlaylistTrackUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun reloadMyPlaylistTrack_success_previousTrackIsNull_renderMyPlaylistTrackAndUploadCoverImage() {
        // Given
        val mockTrackList = listOf(MockDataModel.mockTrack)
        myPlaylistViewModel.setPrivateData(previousTrackList = null)
        whenever(getMyPlaylistUseCase.execute(any())).thenReturn(flowOf(mockMyPlaylistModel))
        whenever(getMyPlaylistTrackUseCase.execute(any(), any())).thenReturn(
            flowOf(mockTrackList)
        )

        // When
        myPlaylistViewModel.reloadMyPlaylistTrack(1)

        // Then
        assertEquals(myPlaylistViewModel.onShowLoading().getOrAwaitValue(), Unit)
        assertEquals(myPlaylistViewModel.onHideLoading().getOrAwaitValue(), Unit)
        assertEquals(
            myPlaylistViewModel.onRenderMyPlaylistTrack().getOrAwaitValue().first(),
            MockDataModel.mockTrack
        )
        verify(getMyPlaylistTrackUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun reloadMyPlaylistTrack_success_hasNewTrack_addTracksToQueue() {
        // Given
        myPlaylistViewModel.setPrivateData(
            trackList = listOf(MockDataModel.mockTrack),
            previousTrackList = null
        )
        whenever(getMyPlaylistUseCase.execute(any())).thenReturn(flowOf(mockMyPlaylistModel))
        whenever(getMyPlaylistTrackUseCase.execute(any(), any())).thenReturn(
            flowOf(
                listOf(
                    MockDataModel.mockTrack,
                    MockDataModel.mockTrack.copy(id = 2)
                )
            )
        )

        // When
        myPlaylistViewModel.reloadMyPlaylistTrack(1)

        // Then
        assertEquals(myPlaylistViewModel.onShowLoading().getOrAwaitValue(), Unit)
        assertEquals(myPlaylistViewModel.onHideLoading().getOrAwaitValue(), Unit)
        assertEquals(
            myPlaylistViewModel.onRenderMyPlaylistTrack().getOrAwaitValue().first(),
            MockDataModel.mockTrack
        )
        assertEquals(
            listOf(MockDataModel.mockTrack.copy(id = 2)),
            myPlaylistViewModel.onNewTracks().getOrAwaitValue()
        )
        verify(getMyPlaylistTrackUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun renderMyPlaylist_trackIsNotEmpty_renderMyPlaylistOnlyHeader() {
        // Given
        val mockImageUrl = "imageUrl"
        val expectValue = mockMyPlaylistModel.copy(
            coverImage = mockImageUrl,
            isTrackNotEmpty = true
        )
        myPlaylistViewModel.setPrivateData(
            trackList = listOf(MockDataModel.mockTrack),
            myPlaylist = listOf(mockMyPlaylistModel)
        )

        // When
        myPlaylistViewModel.renderMyPlaylist(mockImageUrl)

        // Then
        myPlaylistViewModel.onRenderMyPlaylist().getOrAwaitValue().also {
            assertEquals(it.size, 1)
            assertEquals(it.first(), expectValue)
        }
    }

    @Test
    fun renderMyPlaylist_imageUrlIsNull_trackIsNotEmpty_renderMyPlaylistOnlyHeader() {
        // Given
        val expectValue = mockMyPlaylistModel.copy(
            isTrackNotEmpty = true
        )
        myPlaylistViewModel.setPrivateData(
            trackList = listOf(MockDataModel.mockTrack),
            myPlaylist = listOf(mockMyPlaylistModel)
        )

        // When
        myPlaylistViewModel.renderMyPlaylist()

        // Then
        myPlaylistViewModel.onRenderMyPlaylist().getOrAwaitValue().also {
            assertEquals(it.size, 1)
            assertEquals(it.first(), expectValue)
        }
    }

    @Test
    fun renderMyPlaylist_trackIsEmpty_renderMyPlaylistBothHeaderAndEmptyTrack() {
        // Given
        val mockImageUrl = "imageUrl"
        val expectValue = mockMyPlaylistModel.copy(
            coverImage = mockImageUrl,
            isTrackNotEmpty = false
        )
        myPlaylistViewModel.setPrivateData(
            trackList = emptyList(),
            myPlaylist = listOf(mockMyPlaylistModel)
        )

        // When
        myPlaylistViewModel.renderMyPlaylist(mockImageUrl)

        // Then
        myPlaylistViewModel.onRenderMyPlaylist().getOrAwaitValue().also {
            assertEquals(it.size, 2)
            assertEquals(it[0], expectValue)
            assertEquals(it[1].itemType, MyPlaylistItemType.PLAYLIST_EMPTY)
        }
    }

    @Test
    fun renderMyPlaylist_trackIsNull_renderMyPlaylistBothHeaderAndEmptyTrack() {
        // Given
        myPlaylistViewModel.setPrivateData(trackList = null, myPlaylist = null)

        // When
        myPlaylistViewModel.renderMyPlaylist("imageUrl")

        // Then
        myPlaylistViewModel.onRenderMyPlaylist().getOrAwaitValue().also {
            assertEquals(it.size, 2)
            assertEquals(it[0].itemType, MyPlaylistItemType.HEADER)
            assertEquals(it[1].itemType, MyPlaylistItemType.PLAYLIST_EMPTY)
        }
    }

    @Test
    fun playTracks_myPlaylistTrackIsNotNull_positionIsNotNull_isShuffleIsTrue_playTrack() {
        // Given
        myPlaylistViewModel.setPrivateData(trackList = listOf(MockDataModel.mockTrack))

        // When
        myPlaylistViewModel.playTracks(position = 1, isShuffle = true)

        // Then
        myPlaylistViewModel.onPlayTracks().getOrAwaitValue().also {
            assertEquals(it.first.first(), MockDataModel.mockTrack)
            assertEquals(it.second, 1)
            assertTrue(it.third)
        }
    }

    @Test
    fun playTracks_myPlaylistTrackIsNotNull_positionIsNull_isShuffleIsFalse_playTrack() {
        // Given
        myPlaylistViewModel.setPrivateData(trackList = listOf(MockDataModel.mockTrack))

        // When
        myPlaylistViewModel.playTracks(isShuffle = false)

        // Then
        myPlaylistViewModel.onPlayTracks().getOrAwaitValue().also {
            assertEquals(it.first.first(), MockDataModel.mockTrack)
            assertNull(it.second)
            assertFalse(it.third)
        }
    }

    @Test
    fun navigateToAddSong_routeToMusicAddSong() {
        // Given
        whenever(router.getLastDestination()).thenReturn(MusicAddSong)

        // When
        myPlaylistViewModel.navigateToAddSong(123)

        // Then
        assertEquals(MusicAddSong, router.getLastDestination())
        verify(router, times(1)).execute(destination = any(), bundle = anyOrNull())
    }

    @Test
    fun selectOptionMore_removePlaylist_navigateToWarningDialog() {
        // When
        myPlaylistViewModel.selectOptionMore(
            PickerOptions.REMOVE_PLAYLIST,
            ProductPickerType.PLAYLIST_OWNER
        )

        // Then
        myPlaylistViewModel.onShowConfirmDialog().getOrAwaitValue().also { musicWarningModel ->
            assertEquals(R.string.delete_my_playlist, musicWarningModel.title)
            assertEquals(R.string.delete_my_playlist_detail, musicWarningModel.description)
            assertEquals(R.string.delete, musicWarningModel.confirmText)
            assertEquals(R.string.cancel, musicWarningModel.cancelText)
            assertEquals(MusicWarningType.CHOICE_ANSWER, musicWarningModel.type)
        }
    }

    @Test
    fun selectOptionMore_addToQueue_playlistOwnerType_trackListEmpty_showToast() {
        // Given
        myPlaylistViewModel.setPrivateData(
            trackList = emptyList()
        )

        // When
        myPlaylistViewModel.selectOptionMore(
            PickerOptions.ADD_TO_QUEUE,
            ProductPickerType.PLAYLIST_OWNER
        )

        // Then
        assertEquals(myPlaylistViewModel.onDismissMoreDialog().getOrAwaitValue(), Unit)
        assertEquals(myPlaylistViewModel.onShowAddToQueueFailToast().getOrAwaitValue(), Unit)
    }

    @Test
    fun selectOptionMore_addToQueue_playlistOwnerType_trackListNotEmpty_addToQueue() {
        // Given
        val mockTrackList = listOf(MockDataModel.mockTrack)
        myPlaylistViewModel.setPrivateData(trackList = mockTrackList)

        // When
        myPlaylistViewModel.selectOptionMore(
            PickerOptions.ADD_TO_QUEUE,
            ProductPickerType.PLAYLIST_OWNER
        )

        // Then
        assertEquals(myPlaylistViewModel.onDismissMoreDialog().getOrAwaitValue(), Unit)
        assertEquals(myPlaylistViewModel.onShowAddToQueueSuccessToast().getOrAwaitValue(), Unit)
        assertEquals(myPlaylistViewModel.onAddToQueue().getOrAwaitValue(), mockTrackList)
    }

    @Test
    fun selectOptionMore_addToQueue_myPlaylistSongType_trackIsNotNull_dismissMoreDialogAndAddToQueue() {
        // When
        myPlaylistViewModel.selectOptionMore(
            option = PickerOptions.ADD_TO_QUEUE,
            type = ProductPickerType.MY_PLAYLIST_SONG,
            track = MockDataModel.mockTrack
        )

        // Then
        assertEquals(myPlaylistViewModel.onDismissMoreDialog().getOrAwaitValue(), Unit)
        assertEquals(myPlaylistViewModel.onShowAddToQueueSuccessToast().getOrAwaitValue(), Unit)
        assertEquals(
            myPlaylistViewModel.onAddToQueue().getOrAwaitValue().first(),
            MockDataModel.mockTrack
        )
    }

    @Test
    fun selectOptionMore_addToQueue_myPlaylistSongType_trackIsNull_dismissMoreDialog() {
        // When
        myPlaylistViewModel.selectOptionMore(
            option = PickerOptions.ADD_TO_QUEUE,
            type = ProductPickerType.MY_PLAYLIST_SONG,
            track = null
        )

        // Then
        assertEquals(myPlaylistViewModel.onDismissMoreDialog().getOrAwaitValue(), Unit)
    }

    @Test
    fun selectOptionMore_removeFromPlaylist_playlistIdIsNotNull_trackIsNotNull_dismissMoreDialogAndRemoveTrack() {
        // Given
        whenever(removeTrackUseCase.execute(any(), any())).thenReturn(flowOf(true))

        // When
        myPlaylistViewModel.selectOptionMore(
            option = PickerOptions.REMOVE_FROM_PLAYLIST,
            type = ProductPickerType.MY_PLAYLIST_SONG,
            track = MockDataModel.mockTrack,
            playlistId = 1
        )

        // Then
        assertEquals(myPlaylistViewModel.onDismissMoreDialog().getOrAwaitValue(), Unit)
        assertEquals(myPlaylistViewModel.onShowRemoveTrackSuccess().getOrAwaitValue(), Unit)
        verify(removeTrackUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun selectOptionMore_removeFromPlaylist_playlistIdIsNotNull_trackIsNull_doNothing() {
        // When
        myPlaylistViewModel.selectOptionMore(
            option = PickerOptions.REMOVE_FROM_PLAYLIST,
            type = ProductPickerType.MY_PLAYLIST_SONG,
            track = MockDataModel.mockTrack,
            playlistId = null
        )

        // Then
        verify(removeTrackUseCase, times(0)).execute(any(), any())
    }

    @Test
    fun selectOptionMore_removeFromPlaylist_playlistIdIsNull_trackIsNotNull_doNothing() {
        // When
        myPlaylistViewModel.selectOptionMore(
            option = PickerOptions.REMOVE_FROM_PLAYLIST,
            type = ProductPickerType.MY_PLAYLIST_SONG,
            track = null,
            playlistId = 1
        )

        // Then
        verify(removeTrackUseCase, times(0)).execute(any(), any())
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsTrue_isSuccessIsTrue_thenShowFavoriteAddSuccess() {
        // When
        myPlaylistViewModel.onFavouriteSelect(isFavourited = true, isSuccess = true)

        // Then
        assertEquals(myPlaylistViewModel.onFavoriteAddSuccess().getOrAwaitValue(), Unit)
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsFalse_isSuccessIsTrue_thenShowFavoriteRemoveSuccess() {
        // When
        myPlaylistViewModel.onFavouriteSelect(isFavourited = false, isSuccess = true)

        // Then
        assertEquals(myPlaylistViewModel.onFavoriteRemoveSuccess().getOrAwaitValue(), Unit)
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsTrue_isSuccessIsFalse_thenShowFavoriteAddError() {
        // When
        myPlaylistViewModel.onFavouriteSelect(isFavourited = true, isSuccess = false)

        // Then
        assertEquals(myPlaylistViewModel.onFavoriteAddError().getOrAwaitValue(), Unit)
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsFalse_isSuccessIsFalse_thenShowFavoriteAddError() {
        // When
        myPlaylistViewModel.onFavouriteSelect(isFavourited = false, isSuccess = false)

        // Then
        assertEquals(myPlaylistViewModel.onFavoriteRemoveError().getOrAwaitValue(), Unit)
    }

    @Test
    fun selectOptionMore_otherOption_dismissMoreDialog() {
        // Given
        val mockTrackList = listOf(MockDataModel.mockTrack)
        myPlaylistViewModel.setPrivateData(trackList = mockTrackList)

        // When
        myPlaylistViewModel.selectOptionMore(PickerOptions.FOLLOW, null)

        // Then
        assertEquals(myPlaylistViewModel.onDismissMoreDialog().getOrAwaitValue(), Unit)
    }

    @Test
    fun removeTrack_success_responseDataIsTrue_showRemoveTrackSuccessAndRemoveMyPlaylistTrack() {
        // Given
        myPlaylistViewModel.setPrivateData(trackList = listOf(mockTrack1, mockTrack2, mockTrack3))
        whenever(removeTrackUseCase.execute(any(), any())).thenReturn(flowOf(true))

        // When
        myPlaylistViewModel.selectOptionMore(
            option = PickerOptions.REMOVE_FROM_PLAYLIST,
            type = ProductPickerType.MY_PLAYLIST_SONG,
            track = mockTrack1,
            playlistId = 1
        )

        // Then
        assertEquals(myPlaylistViewModel.onShowRemoveTrackSuccess().getOrAwaitValue(), Unit)
        myPlaylistViewModel.onRenderMyPlaylistTrack().getOrAwaitValue().also { trackList ->
            assertEquals(2, trackList.size)
            assertEquals(trackList[0], mockTrack2)
            assertEquals(trackList[1], mockTrack3)
        }
        verify(removeTrackUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun removeTrack_success_responseDataIsFalse_showRemoveTrackFail() {
        // Given
        whenever(removeTrackUseCase.execute(any(), any())).thenReturn(flowOf(false))

        // When
        myPlaylistViewModel.selectOptionMore(
            option = PickerOptions.REMOVE_FROM_PLAYLIST,
            type = ProductPickerType.MY_PLAYLIST_SONG,
            track = MockDataModel.mockTrack,
            playlistId = 1
        )

        // Then
        assertEquals(myPlaylistViewModel.onShowRemoveTrackFail().getOrAwaitValue(), Unit)
        verify(removeTrackUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun removeTrack_fail_showRemoveTrackFail() {
        // Given
        whenever(removeTrackUseCase.execute(any(), any())).thenReturn(
            flow { error("error") }
        )

        // When
        myPlaylistViewModel.selectOptionMore(
            option = PickerOptions.REMOVE_FROM_PLAYLIST,
            type = ProductPickerType.MY_PLAYLIST_SONG,
            track = MockDataModel.mockTrack,
            playlistId = 1
        )

        // Then
        assertEquals(myPlaylistViewModel.onShowRemoveTrackFail().getOrAwaitValue(), Unit)
        verify(removeTrackUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun compareTrackList_listIsNotEqual_currentTrackListIsEqualsNumberImage_generateGridImage() {
        // Given
        val mockPreviousTrackList =
            listOf(mockTrack1, mockTrack2, mockTrack3, mockTrack4, mockTrack5)
        myPlaylistViewModel.setPrivateData(
            trackList = mockPreviousTrackList,
            previousTrackList = mockPreviousTrackList
        )
        whenever(removeTrackUseCase.execute(any(), any())).thenReturn(flowOf(true))

        // When
        myPlaylistViewModel.selectOptionMore(
            option = PickerOptions.REMOVE_FROM_PLAYLIST,
            type = ProductPickerType.MY_PLAYLIST_SONG,
            track = mockTrack2,
            playlistId = 1
        )

        // Then
        assertEquals(myPlaylistViewModel.onGenerateGridImage().getOrAwaitValue().size, 4)
    }

    @Test
    fun compareTrackList_previousTrackListSizeIsEqualNumberImages_currentTrackListLessThanNumberImage_generateGridImage() {
        // Given
        val mockPreviousTrackList = listOf(mockTrack1, mockTrack2, mockTrack3, mockTrack4)
        myPlaylistViewModel.setPrivateData(
            trackList = mockPreviousTrackList,
            previousTrackList = mockPreviousTrackList
        )
        whenever(removeTrackUseCase.execute(any(), any())).thenReturn(flowOf(true))

        // When
        myPlaylistViewModel.selectOptionMore(
            option = PickerOptions.REMOVE_FROM_PLAYLIST,
            type = ProductPickerType.MY_PLAYLIST_SONG,
            track = mockTrack2,
            playlistId = 1
        )

        // Then
        assertEquals(myPlaylistViewModel.onGenerateGridImage().getOrAwaitValue().size, 3)
    }

    @Test
    fun deletePlaylist_playlistIdNull_notVerifyDeletePlaylist() {
        myPlaylistViewModel.deletePlaylist(null)
        verify(deleteMyPlaylistUseCase, times(0)).execute(any())
    }

    @Test
    fun deletePlaylist_playlistIdNotNull_success_closeMyPlaylist() {
        // Given
        whenever(deleteMyPlaylistUseCase.execute(any())).thenReturn(flowOf(Any()))

        // When
        myPlaylistViewModel.deletePlaylist(1)

        // Then
        assertEquals(myPlaylistViewModel.onShowLoadingDialog().getOrAwaitValue(), Unit)
        assertEquals(myPlaylistViewModel.onHideLoadingDialog().getOrAwaitValue(), Unit)
        assertEquals(myPlaylistViewModel.onCloseMyPlaylist().getOrAwaitValue(), Unit)
        verify(deleteMyPlaylistUseCase, times(1)).execute(any())
    }

    @Test
    fun deletePlaylist_playlistIdNotNull_fail_showErrorDialog() {
        // Given
        whenever(deleteMyPlaylistUseCase.execute(any())).thenReturn(flow { error("something went wrong") })

        // When
        myPlaylistViewModel.deletePlaylist(1)

        // Then
        myPlaylistViewModel.onShowErrorDialog().getOrAwaitValue().also { musicWarningModel ->
            assertEquals(R.string.delete_my_playlist_error, musicWarningModel.title)
            assertEquals(R.string.delete_my_playlist_error_detail, musicWarningModel.description)
            assertEquals(R.string.ok, musicWarningModel.confirmText)
            assertEquals(MusicWarningType.FORCE_ANSWER, musicWarningModel.type)
        }
        assertEquals(myPlaylistViewModel.onShowLoadingDialog().getOrAwaitValue(), Unit)
        assertEquals(myPlaylistViewModel.onHideLoadingDialog().getOrAwaitValue(), Unit)
        verify(deleteMyPlaylistUseCase, times(1)).execute(any())
    }

    @Test
    fun testMapToPlaylist_returnPlaylistSource() {
        myPlaylistViewModel.mapToPlaylist(mockMyPlaylistModel)

        assertEquals(10, myPlaylistViewModel.getPlaylistPlayerSource()?.id)
        assertEquals(
            MusicConstant.MyPlaylist.MY_PLAYLIST,
            myPlaylistViewModel.getPlaylistPlayerSource()?.name?.firstOrNull()?.value
        )
    }

    @Test
    fun testMapToPlaylist_idNull_returnDefault() {
        myPlaylistViewModel.mapToPlaylist(MyPlaylistModel())

        assertEquals(0, myPlaylistViewModel.getPlaylistPlayerSource()?.id)
        assertEquals(
            MusicConstant.MyPlaylist.MY_PLAYLIST,
            myPlaylistViewModel.getPlaylistPlayerSource()?.name?.firstOrNull()?.value
        )
    }
}
