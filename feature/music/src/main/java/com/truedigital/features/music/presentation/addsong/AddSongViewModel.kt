package com.truedigital.features.music.presentation.addsong

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import com.truedigital.features.music.domain.addsong.model.MusicSearchResultModel
import com.truedigital.features.music.domain.addsong.usecase.AddSongUseCase
import com.truedigital.features.music.domain.addsong.usecase.GetSearchSongPagingUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class AddSongViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val addSongUseCase: AddSongUseCase,
    private val getSearchSongPagingUseCase: GetSearchSongPagingUseCase
) : ScopedViewModel() {

    private var searchJob: Job? = null
    private var currentQueryValue: String? = null
    private val trackIds: MutableList<Int> = mutableListOf()

    private val searchSongResult = MutableLiveData<PagingData<MusicSearchResultModel>>()
    private val showEmptyResult = SingleLiveEvent<Unit>()
    private val hideEmptyResult = SingleLiveEvent<Unit>()
    private val errorAddSong = SingleLiveEvent<Unit>()
    private val successAddSong = SingleLiveEvent<Unit>()
    private val showProgressAddSong = SingleLiveEvent<Unit>()
    private val hideProgressAddSong = SingleLiveEvent<Unit>()
    private val songSelected = SingleLiveEvent<List<Int>>()
    fun onSearchSongResult(): LiveData<PagingData<MusicSearchResultModel>> = searchSongResult
    fun onShowEmptyResult(): LiveData<Unit> = showEmptyResult
    fun onHideEmptyResult(): LiveData<Unit> = hideEmptyResult
    fun onErrorAddSong(): LiveData<Unit> = errorAddSong
    fun onSuccessAddSong(): LiveData<Unit> = successAddSong
    fun onShowProgressAddSong(): LiveData<Unit> = showProgressAddSong
    fun onHideProgressAddSong(): LiveData<Unit> = hideProgressAddSong
    fun onSongSelected(): LiveData<List<Int>> = songSelected

    fun searchSong(query: String) {
        if (query != currentQueryValue && query.isNotEmpty()) {
            currentQueryValue = query

            searchJob?.cancel()
            searchJob = launchSafe {
                getSearchSongPagingUseCase.execute(query.trim())
                    .flowOn(coroutineDispatcher.io())
                    .catch {
                        showEmptyResult.value = Unit
                    }
                    .cachedIn(this)
                    .collectLatest {
                        searchSongResult.value = it.map { item ->
                            item.copy(
                                isSelected = trackIds.contains(item.id)
                            )
                        }
                    }
            }
        }
    }

    fun handlerShowEmptyResult(loadState: LoadState, isItemCountEmpty: Boolean) {
        val isListEmpty = loadState is LoadState.NotLoading && isItemCountEmpty
        if (isListEmpty) {
            showEmptyResult.value = Unit
        } else {
            hideEmptyResult.value = Unit
        }
    }

    fun onSelectedSong(trackId: Int?) = launchSafe {
        trackId?.let {
            if (trackIds.contains(it)) trackIds.remove(it) else trackIds.add(it)
            songSelected.value = trackIds
            searchSongResult.value = searchSongResult.value?.map { item ->
                item.copy(
                    isSelected = trackIds.contains(item.id ?: 0)
                )
            }
        }
    }

    fun addSong(playlistId: Int) = launchSafe {
        addSongUseCase.execute(playlistId.toString(), trackIds.toList())
            .flowOn(coroutineDispatcher.io())
            .onStart { showProgressAddSong.value = Unit }
            .onCompletion { hideProgressAddSong.value = Unit }
            .catch { errorAddSong.value = Unit }
            .collect {
                successAddSong.value = Unit
            }
    }

    @VisibleForTesting
    fun setPrivateData(currentQueryValue: String? = null) {
        this.currentQueryValue = currentQueryValue
    }
}
