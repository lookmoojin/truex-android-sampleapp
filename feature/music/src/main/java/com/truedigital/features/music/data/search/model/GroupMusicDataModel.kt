package com.truedigital.features.music.data.search.model

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.truedigital.features.music.domain.search.model.MusicSearchModel

class GroupMusicDataModel(
    val pagedList: LiveData<PagedList<MusicSearchModel>>,
    val showLoading: LiveData<Unit>? = null,
    val hideLoading: LiveData<Unit>? = null,
    val showError: LiveData<Unit>? = null,
    val hideError: LiveData<Unit>? = null
)
