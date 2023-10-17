package com.truedigital.features.music.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject

class MusicPlayerStateViewModel @Inject constructor() : ViewModel() {

    private val collapsedPlayerState = SingleLiveEvent<Unit>()
    private val expandedPlayerState = SingleLiveEvent<Unit>()
    private val visibilityPlayerChange = SingleLiveEvent<Boolean>()

    fun onCollapsedPlayerState(): LiveData<Unit> = collapsedPlayerState
    fun onExpandedPlayerState(): LiveData<Unit> = expandedPlayerState
    fun onVisibilityPlayerChange(): LiveData<Boolean> = visibilityPlayerChange

    fun setCollapsedPlayerState() {
        collapsedPlayerState.value = Unit
    }

    fun setExpandedPlayerState() {
        expandedPlayerState.value = Unit
    }

    fun onPlayerStateChange(isExpanded: Boolean) {
        if (isExpanded) {
            setExpandedPlayerState()
        } else {
            setCollapsedPlayerState()
        }
    }

    fun setVisibilityPlayerStatus(isVisible: Boolean) {
        visibilityPlayerChange.value = isVisible
    }
}
