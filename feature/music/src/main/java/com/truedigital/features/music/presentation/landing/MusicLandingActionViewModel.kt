package com.truedigital.features.music.presentation.landing

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject

class MusicLandingActionViewModel @Inject constructor() : ViewModel() {

    private val activeTopNavBySlug = SingleLiveEvent<String>()
    fun onActiveTopNavBySlug(): LiveData<String> = activeTopNavBySlug

    fun setActiveTopNav(slug: String) {
        activeTopNavBySlug.value = slug
    }
}
