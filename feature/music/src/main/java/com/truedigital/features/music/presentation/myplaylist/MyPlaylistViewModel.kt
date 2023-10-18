package com.truedigital.features.music.presentation.myplaylist

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.extensions.launchSafe
import com.truedigital.features.music.domain.image.usecase.GenerateGridImageUseCaseImpl
import com.truedigital.features.music.domain.myplaylist.model.MyPlaylistItemType
import com.truedigital.features.music.domain.myplaylist.model.MyPlaylistModel
import com.truedigital.features.music.domain.myplaylist.usecase.DeleteMyPlaylistUseCase
import com.truedigital.features.music.domain.myplaylist.usecase.GetMyPlaylistTrackUseCase
import com.truedigital.features.music.domain.myplaylist.usecase.GetMyPlaylistUseCase
import com.truedigital.features.music.domain.track.usecase.RemoveTrackUseCase
import com.truedigital.features.music.domain.usecase.router.MusicRouterUseCase
import com.truedigital.features.music.domain.warning.model.MusicWarningModel
import com.truedigital.features.music.domain.warning.model.MusicWarningType
import com.truedigital.features.music.navigation.router.MusicAddSong
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.foundation.extension.SingleLiveEvent
import com.truedigital.foundation.extension.isEqual
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class MyPlaylistViewModel @Inject constructor(
    private val router: MusicRouterUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val getMyPlaylistUseCase: GetMyPlaylistUseCase,
    private val getMyPlaylistTrackUseCase: GetMyPlaylistTrackUseCase,
    private val deleteMyPlaylistUseCase: DeleteMyPlaylistUseCase,
    private val removeTrackUseCase: RemoveTrackUseCase
) : ScopedViewModel() {

    private var previousTrackList: List<Track>? = null
    private var isLoadingState: Boolean = false
    private var playlist: Playlist? = null

    private val renderMyPlaylist = SingleLiveEvent<MutableList<MyPlaylistModel>>()
    private val renderMyPlaylistTrack = SingleLiveEvent<MutableList<Track>>()
    private val newTracks = SingleLiveEvent<List<Track>>()
    private val showPlaylistErrorDialog = SingleLiveEvent<Unit>()
    private val showErrorDialog = SingleLiveEvent<MusicWarningModel>()
    private val showConfirmDialog = SingleLiveEvent<MusicWarningModel>()
    private val showLoading = SingleLiveEvent<Unit>()
    private val hideLoading = SingleLiveEvent<Unit>()
    private val showLoadingDialog = SingleLiveEvent<Unit>()
    private val hideLoadingDialog = SingleLiveEvent<Unit>()
    private val playTracks = SingleLiveEvent<Triple<List<Track>, Int?, Boolean>>()
    private val generateGridImage = SingleLiveEvent<List<Track>>()
    private val addToQueue = SingleLiveEvent<MutableList<Track>>()
    private val showAddToQueueSuccessToast = SingleLiveEvent<Unit>()
    private val showAddToQueueFailToast = SingleLiveEvent<Unit>()
    private val closeMyPlaylist = SingleLiveEvent<Unit>()
    private val dismissMoreDialog = SingleLiveEvent<Unit>()
    private val showRemoveTrackSuccess = SingleLiveEvent<Unit>()
    private val showRemoveTrackFail = SingleLiveEvent<Unit>()
    private val favoriteAddSuccess = SingleLiveEvent<Unit>()
    private val favoriteAddError = SingleLiveEvent<Unit>()
    private val favoriteRemoveSuccess = SingleLiveEvent<Unit>()
    private val favoriteRemoveError = SingleLiveEvent<Unit>()

    fun onRenderMyPlaylist(): LiveData<MutableList<MyPlaylistModel>> = renderMyPlaylist
    fun onRenderMyPlaylistTrack(): LiveData<MutableList<Track>> = renderMyPlaylistTrack
    fun onNewTracks(): LiveData<List<Track>> = newTracks
    fun onShowPlaylistErrorDialog(): LiveData<Unit> = showPlaylistErrorDialog
    fun onShowErrorDialog(): LiveData<MusicWarningModel> = showErrorDialog
    fun onShowConfirmDialog(): LiveData<MusicWarningModel> = showConfirmDialog
    fun onShowLoading(): LiveData<Unit> = showLoading
    fun onHideLoading(): LiveData<Unit> = hideLoading
    fun onShowLoadingDialog(): LiveData<Unit> = showLoadingDialog
    fun onHideLoadingDialog(): LiveData<Unit> = hideLoadingDialog
    fun onPlayTracks(): LiveData<Triple<List<Track>, Int?, Boolean>> = playTracks
    fun onGenerateGridImage(): LiveData<List<Track>> = generateGridImage
    fun onAddToQueue(): LiveData<MutableList<Track>> = addToQueue
    fun onShowAddToQueueSuccessToast(): LiveData<Unit> = showAddToQueueSuccessToast
    fun onShowAddToQueueFailToast(): LiveData<Unit> = showAddToQueueFailToast
    fun onCloseMyPlaylist(): LiveData<Unit> = closeMyPlaylist
    fun onDismissMoreDialog(): LiveData<Unit> = dismissMoreDialog
    fun onShowRemoveTrackSuccess(): LiveData<Unit> = showRemoveTrackSuccess
    fun onShowRemoveTrackFail(): LiveData<Unit> = showRemoveTrackFail
    fun onFavoriteAddSuccess(): LiveData<Unit> = favoriteAddSuccess
    fun onFavoriteAddError(): LiveData<Unit> = favoriteAddError
    fun onFavoriteRemoveSuccess(): LiveData<Unit> = favoriteRemoveSuccess
    fun onFavoriteRemoveError(): LiveData<Unit> = favoriteRemoveError

    fun enableLoading(isEnable: Boolean) {
        isLoadingState = isEnable
    }

    fun getIsLoadingState() = isLoadingState

    fun loadMyPlaylist(playlistId: Int) = launchSafe {
        getMyPlaylistUseCase.execute(playlistId.toString())
            .flatMapConcat { myPlaylist ->
                getMyPlaylistTrackUseCase.execute(playlistId, myPlaylist.count)
                    .map { trackList ->
                        Pair(myPlaylist, trackList)
                    }
            }
            .flowOn(coroutineDispatcher.io())
            .onStart {
                showLoading.value = Unit
            }
            .onCompletion {
                hideLoading.value = Unit
            }
            .catch {
                showPlaylistErrorDialog.value = Unit
            }
            .collectSafe { (myPlaylist, trackList) ->
                val myPlaylistList = mutableListOf<MyPlaylistModel>()
                myPlaylistList.add(myPlaylist.copy(isTrackNotEmpty = trackList.isNotEmpty()))
                previousTrackList = trackList.take(GenerateGridImageUseCaseImpl.NUMBER_IMAGES)

                if (trackList.isNotEmpty()) {
                    renderMyPlaylistTrack.value = trackList.toMutableList()
                } else {
                    myPlaylistList.add(getEmptyPlaylistItem())
                }
                renderMyPlaylist.value = myPlaylistList
                mapToPlaylist(myPlaylist)
            }
    }

    fun reloadMyPlaylistTrack(playlistId: Int) = launchSafe {
        getMyPlaylistUseCase.execute(playlistId.toString())
            .flatMapConcat {
                getMyPlaylistTrackUseCase.execute(playlistId, it.count)
            }
            .flowOn(coroutineDispatcher.io())
            .onStart {
                enableLoading(true)
                showLoading.value = Unit
            }
            .onCompletion {
                hideLoading.value = Unit
            }
            .collectSafe { trackList ->
                renderMyPlaylistTrack.value?.let { oldTrackList ->
                    newTracks.value = trackList - oldTrackList.toSet()
                }
                renderMyPlaylistTrack.value = trackList.toMutableList()
                compareTrackList(trackList)
            }
    }

    fun renderMyPlaylist(imageUrl: String? = null) {
        val isTrackNotEmpty = renderMyPlaylistTrack.value?.isNotEmpty() ?: false
        val renderMyPlaylistValue = renderMyPlaylist.value?.firstOrNull() ?: MyPlaylistModel()
        val myPlaylist = renderMyPlaylistValue.copy(
            isTrackNotEmpty = isTrackNotEmpty,
            coverImage = imageUrl ?: renderMyPlaylistValue.coverImage
        )

        val myPlaylistList = mutableListOf<MyPlaylistModel>()
        myPlaylistList.add(myPlaylist)

        if (isTrackNotEmpty.not()) {
            myPlaylistList.add(getEmptyPlaylistItem())
        }

        renderMyPlaylist.value = myPlaylistList
    }

    fun playTracks(position: Int? = null, isShuffle: Boolean) {
        renderMyPlaylistTrack.value?.let { trackList ->
            val trackValue = Triple(trackList, position, isShuffle)
            playTracks.value = trackValue
        }
    }

    fun navigateToAddSong(playlistId: Int) {
        val mBundle = Bundle().apply {
            putInt(MyPlaylistFragment.PLAYLIST_ID_SLUG, playlistId)
        }
        router.execute(MusicAddSong, mBundle)
    }

    fun selectOptionMore(
        option: PickerOptions,
        type: ProductPickerType?,
        track: Track? = null,
        playlistId: Int? = null
    ) {
        when (option) {
            PickerOptions.REMOVE_PLAYLIST -> {
                val musicWarningModel = MusicWarningModel(
                    title = R.string.delete_my_playlist,
                    description = R.string.delete_my_playlist_detail,
                    confirmText = R.string.delete,
                    cancelText = R.string.cancel,
                    type = MusicWarningType.CHOICE_ANSWER
                )
                showConfirmDialog.value = musicWarningModel
            }
            PickerOptions.ADD_TO_QUEUE -> {
                dismissMoreDialog.value = Unit
                addToQueueByType(type, track)
            }
            PickerOptions.REMOVE_FROM_PLAYLIST -> {
                if (playlistId != null && track != null) {
                    dismissMoreDialog.value = Unit
                    removeTrack(playlistId, track.playlistTrackId, track.id)
                }
            }
            else -> {
                dismissMoreDialog.value = Unit
            }
        }
    }

    fun deletePlaylist(playlistId: Int?) = launchSafe {
        playlistId?.let { id ->
            deleteMyPlaylistUseCase.execute(id)
                .flowOn(coroutineDispatcher.io())
                .onStart {
                    showLoadingDialog.value = Unit
                }
                .onCompletion {
                    hideLoadingDialog.value = Unit
                }
                .catch {
                    val musicWarningDialog = MusicWarningModel(
                        title = R.string.delete_my_playlist_error,
                        description = R.string.delete_my_playlist_error_detail,
                        confirmText = R.string.ok,
                        type = MusicWarningType.FORCE_ANSWER
                    )
                    showErrorDialog.value = musicWarningDialog
                }
                .collectSafe {
                    closeMyPlaylist.value = Unit
                }
        }
    }

    fun getPlaylistPlayerSource() = playlist

    fun onFavouriteSelect(isFavourited: Boolean, isSuccess: Boolean) {
        if (isSuccess) {
            if (isFavourited) {
                favoriteAddSuccess.value = Unit
            } else {
                favoriteRemoveSuccess.value = Unit
            }
        } else {
            if (isFavourited) {
                favoriteAddError.value = Unit
            } else {
                favoriteRemoveError.value = Unit
            }
        }
    }

    private fun removeTrack(playlistId: Int, playlistTrackId: Int, trackId: Int) = launchSafe {
        removeTrackUseCase.execute(playlistId, listOf(playlistTrackId))
            .flowOn(coroutineDispatcher.io())
            .catch {
                showRemoveTrackFail.value = Unit
            }
            .collectSafe { isSuccess ->
                if (isSuccess) {
                    showRemoveTrackSuccess.value = Unit
                    removeMyPlaylistTrack(trackId)
                } else {
                    showRemoveTrackFail.value = Unit
                }
            }
    }

    private fun removeMyPlaylistTrack(trackId: Int) {
        val trackList = renderMyPlaylistTrack.value ?: mutableListOf()
        trackList.removeAll { track ->
            track.id == trackId
        }

        renderMyPlaylistTrack.value = trackList
        compareTrackList(trackList)
        renderMyPlaylist()
    }

    private fun compareTrackList(trackList: List<Track>) {
        val currentTrackList = trackList.take(GenerateGridImageUseCaseImpl.NUMBER_IMAGES)

        previousTrackList?.let { _previousTrackList ->
            val listIsNotEqual = _previousTrackList.isEqual(currentTrackList).not()

            if (listIsNotEqual && currentTrackList.size == GenerateGridImageUseCaseImpl.NUMBER_IMAGES) {
                generateGridImage.value = currentTrackList
            } else if (currentTrackList.isNotEmpty() &&
                currentTrackList.size < GenerateGridImageUseCaseImpl.NUMBER_IMAGES &&
                currentTrackList.firstOrNull() != _previousTrackList.firstOrNull()
            ) {
                generateGridImage.value = currentTrackList
            } else if (_previousTrackList.size == GenerateGridImageUseCaseImpl.NUMBER_IMAGES &&
                currentTrackList.size < GenerateGridImageUseCaseImpl.NUMBER_IMAGES
            ) {
                generateGridImage.value = currentTrackList
            } else {
                enableLoading(false)
            }
        } ?: run {
            generateGridImage.value = currentTrackList
        }
        previousTrackList = currentTrackList
    }

    private fun getEmptyPlaylistItem(): MyPlaylistModel {
        return MyPlaylistModel(itemId = 2, itemType = MyPlaylistItemType.PLAYLIST_EMPTY)
    }

    private fun addToQueueByType(type: ProductPickerType?, track: Track?) {
        when (type) {
            ProductPickerType.PLAYLIST_OWNER -> {
                if (renderMyPlaylistTrack.value.isNullOrEmpty()) {
                    showAddToQueueFailToast.value = Unit
                } else {
                    addToQueue.value = renderMyPlaylistTrack.value
                    showAddToQueueSuccessToast.value = Unit
                }
            }
            ProductPickerType.MY_PLAYLIST_SONG -> {
                track?.let {
                    addToQueue.value = mutableListOf(it)
                    showAddToQueueSuccessToast.value = Unit
                }
            }
            else -> {
                // nothing
            }
        }
    }

    @VisibleForTesting
    fun mapToPlaylist(myPlaylist: MyPlaylistModel) {
        playlist = Playlist(
            id = myPlaylist.id ?: 0,
            name = listOf(
                LocalisedString(
                    language = "",
                    value = com.truedigital.features.listens.share.constant.MusicConstant.MyPlaylist.MY_PLAYLIST
                )
            )
        )
    }

    @VisibleForTesting
    fun setPrivateData(
        trackList: List<Track>? = null,
        myPlaylist: List<MyPlaylistModel>? = null,
        previousTrackList: List<Track>? = null
    ) {
        renderMyPlaylistTrack.value = trackList?.toMutableList()
        renderMyPlaylist.value = myPlaylist?.toMutableList()
        this.previousTrackList = previousTrackList
    }
}
