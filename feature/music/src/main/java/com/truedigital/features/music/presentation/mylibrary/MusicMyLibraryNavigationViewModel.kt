package com.truedigital.features.music.presentation.mylibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject

class MusicMyLibraryNavigationViewModel @Inject constructor() : ViewModel() {

    private val showMyPlaylist = SingleLiveEvent<Int>()
    private val createNewPlaylist = SingleLiveEvent<Unit>()

    fun onShowMyPlaylist(): LiveData<Int> = showMyPlaylist
    fun onCreateNewPlaylist(): LiveData<Unit> = createNewPlaylist

    fun showMyPlaylist(playlistId: Int) {
        showMyPlaylist.value = playlistId
    }

    fun createNewPlaylist() {
        createNewPlaylist.value = Unit
    }
}
