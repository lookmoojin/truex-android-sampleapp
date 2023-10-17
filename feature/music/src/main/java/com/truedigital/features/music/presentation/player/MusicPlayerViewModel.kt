package com.truedigital.features.music.presentation.player

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.truedigital.features.music.domain.player.usecase.GetLandingOnListenScopeUseCase
import com.truedigital.features.music.domain.player.usecase.SetLandingOnListenScopeUseCase
import com.truedigital.features.music.domain.player.usecase.SetMusicPlayerVisibleUseCase
import com.truedigital.features.music.manager.player.MusicPlayerActionManager
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject

class MusicPlayerViewModel @Inject constructor(
    private val musicPlayerActionManager: MusicPlayerActionManager,
    private val getLandingOnListenScopeUseCase: GetLandingOnListenScopeUseCase,
    private val setLandingOnListenScopeUseCase: SetLandingOnListenScopeUseCase,
    private val setMusicPlayerVisibleUseCase: SetMusicPlayerVisibleUseCase
) : ViewModel() {

    private var miniPlayerVisible = true

    private val addMarginBottom = SingleLiveEvent<Unit>()
    private val hideMusicPlayer = SingleLiveEvent<Unit>()
    private val isListenScope = SingleLiveEvent<Unit>()
    private val collapsePlayer = SingleLiveEvent<Unit>()
    private val pausePlayer = SingleLiveEvent<Unit>()
    private val loadTunedBackgroundImage = SingleLiveEvent<String>()
    private val loadTDGBackgroundImage = SingleLiveEvent<String>()
    private val favoriteAddSuccess = SingleLiveEvent<Unit>()
    private val favoriteAddError = SingleLiveEvent<Unit>()
    private val favoriteRemoveSuccess = SingleLiveEvent<Unit>()
    private val favoriteRemoveError = SingleLiveEvent<Unit>()

    fun onAddMarginBottom(): LiveData<Unit> = addMarginBottom
    fun onHideMusicPlayer(): LiveData<Unit> = hideMusicPlayer
    fun onListenScope(): LiveData<Unit> = isListenScope
    fun onCollapsePlayer(): LiveData<Unit> = collapsePlayer
    fun onPausePlayer(): LiveData<Unit> = pausePlayer
    fun onLoadTunedBackgroundImage(): LiveData<String> = loadTunedBackgroundImage
    fun onLoadTDGBackgroundImage(): LiveData<String> = loadTDGBackgroundImage
    fun onFavoriteAddSuccess(): LiveData<Unit> = favoriteAddSuccess
    fun onFavoriteAddError(): LiveData<Unit> = favoriteAddError
    fun onFavoriteRemoveSuccess(): LiveData<Unit> = favoriteRemoveSuccess
    fun onFavoriteRemoveError(): LiveData<Unit> = favoriteRemoveError

    fun setOnAddMarginBottom(isBottomNavVisible: Boolean = false) {
        if (isBottomNavVisible) {
            miniPlayerVisible = true
            addMarginBottom.value = Unit
        }
    }

    fun setOnHideMusicPlayer() {
        miniPlayerVisible = false
        hideMusicPlayer.value = Unit
    }

    fun setIsListenScope() {
        miniPlayerVisible = true
        isListenScope.value = Unit
        setLandingOnListenScopeUseCase.execute(true)
    }

    fun setCollapsePlayer() {
        collapsePlayer.value = Unit
    }

    fun handlerBottomMarginMiniPlayer(isCollapsed: Boolean) {
        val isLandingOnListenScope = getLandingOnListenScopeUseCase.execute()
        if (isCollapsed && isLandingOnListenScope) {
            setIsListenScope()
        }
    }

    fun pausePlayer() {
        pausePlayer.value = Unit
    }

    fun handlerLoadBackgroundImage(imageUri: String?, mediaType: MediaType?) {
        if (mediaType == MediaType.RADIO) {
            loadTDGBackgroundImage.value = imageUri.orEmpty()
        } else {
            loadTunedBackgroundImage.value = imageUri.orEmpty()
        }
    }

    fun setVisibilityPlayerStatus(isVisible: Boolean) {
        setMusicPlayerVisibleUseCase.execute(isVisible)
    }

    fun getMiniPlayerVisible() = miniPlayerVisible
    fun actionPrevious() = musicPlayerActionManager.actionPrevious()
    fun actionNext() = musicPlayerActionManager.actionNext()
    fun actionClose() = musicPlayerActionManager.actionClose()

    fun actionPause(isPlayerActiveState: Boolean = false) {
        if (isPlayerActiveState) {
            musicPlayerActionManager.actionPause()
        }
    }

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

    @VisibleForTesting
    fun setPrivateData(miniPlayerVisible: Boolean) {
        this.miniPlayerVisible = miniPlayerVisible
    }
}
