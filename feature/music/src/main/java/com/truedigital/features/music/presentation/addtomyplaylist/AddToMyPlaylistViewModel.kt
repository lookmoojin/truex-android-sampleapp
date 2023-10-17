package com.truedigital.features.music.presentation.addtomyplaylist

import androidx.lifecycle.LiveData
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.extensions.launchSafe
import com.truedigital.features.music.domain.addsong.usecase.AddSongUseCase
import com.truedigital.features.music.domain.myplaylist.model.MusicMyPlaylistModel
import com.truedigital.features.music.domain.myplaylist.usecase.GetMyPlaylistShelfUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class AddToMyPlaylistViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val addSongUseCase: AddSongUseCase,
    private val getMyPlaylistShelfUseCase: GetMyPlaylistShelfUseCase
) : ScopedViewModel() {

    private val showMyPlaylist = SingleLiveEvent<List<MusicMyPlaylistModel>>()
    private val showLoading = SingleLiveEvent<Unit>()
    private val hideLoading = SingleLiveEvent<Unit>()
    private val showAddSongSuccessMessage = SingleLiveEvent<Unit>()
    private val showAddSongFailMessage = SingleLiveEvent<Unit>()

    fun onShowMyPlaylist(): LiveData<List<MusicMyPlaylistModel>> = showMyPlaylist
    fun onShowLoading(): LiveData<Unit> = showLoading
    fun onHideLoading(): LiveData<Unit> = hideLoading
    fun onShowAddSongSuccessMessage(): LiveData<Unit> = showAddSongSuccessMessage
    fun onShowAddSongFailMessage(): LiveData<Unit> = showAddSongFailMessage

    fun handlerReloadMyPlaylist(isMyPlaylistWasCreated: Boolean) {
        if (isMyPlaylistWasCreated) {
            loadMyPlaylist()
        }
    }

    fun loadMyPlaylist() = launchSafe {
        val myPlaylistData = mutableListOf<MusicMyPlaylistModel>()
        myPlaylistData.add(MusicMyPlaylistModel.CreateMyPlaylistModel(-1))

        getMyPlaylistShelfUseCase.execute()
            .flowOn(coroutineDispatcher.io())
            .onStart {
                showLoading.value = Unit
            }
            .onCompletion {
                hideLoading.value = Unit
            }
            .catch {
                showMyPlaylist.value = myPlaylistData
            }
            .collectSafe {
                myPlaylistData.addAll(it)
                showMyPlaylist.value = myPlaylistData
            }
    }

    fun addSongToMyPlaylist(playlistId: String?, songId: Int?) = launchSafe {
        val songIdList = if (songId != null) listOf(songId) else listOf()

        addSongUseCase.execute(playlistId.orEmpty(), songIdList)
            .flowOn(coroutineDispatcher.io())
            .onStart {
                showLoading.value = Unit
            }
            .onCompletion {
                hideLoading.value = Unit
            }
            .catch {
                showAddSongFailMessage.value = Unit
            }
            .collectSafe {
                showAddSongSuccessMessage.value = Unit
            }
    }
}
