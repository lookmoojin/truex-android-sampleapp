package com.truedigital.features.music.data.search.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.features.music.domain.search.model.MusicSearchModel
import com.truedigital.features.music.domain.search.model.MusicType
import com.truedigital.features.music.domain.search.model.MusicType.ALBUM
import com.truedigital.features.music.domain.search.model.MusicType.ALL
import com.truedigital.features.music.domain.search.model.MusicType.ARTIST
import com.truedigital.features.music.domain.search.model.MusicType.PLAYLIST
import com.truedigital.features.music.domain.search.model.MusicType.SONG
import com.truedigital.features.music.domain.search.model.ThemeType
import com.truedigital.features.music.domain.search.usecase.GetSearchAlbumUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchAllUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchArtistUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchPlaylistUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchSongUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach

class MusicSearchDataSource(
    private val getSearchAllUseCase: GetSearchAllUseCase,
    private val getSearchAlbumUseCase: GetSearchAlbumUseCase,
    private val getSearchArtistUseCase: GetSearchArtistUseCase,
    private val getSearchPlaylistUseCase: GetSearchPlaylistUseCase,
    private val getSearchSongUseCase: GetSearchSongUseCase,
    private val keyword: String,
    private val theme: ThemeType,
    private val type: MusicType
) : PageKeyedDataSource<Int, MusicSearchModel>() {

    val showLoading = MutableLiveData<Unit>()
    val hideLoading = MutableLiveData<Unit>()
    val showError = MutableLiveData<Unit>()
    val hideError = MutableLiveData<Unit>()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, MusicSearchModel>
    ) {
        showLoading.postValue(Unit)
        hideError.postValue(Unit)
        load(page = 0, pageSize = params.requestedLoadSize, loadInitialCallback = callback)
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, MusicSearchModel>
    ) {
        Unit
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MusicSearchModel>) {
        load(page = params.key, pageSize = params.requestedLoadSize, loadCallback = callback)
    }

    private fun load(
        page: Int,
        pageSize: Int,
        loadInitialCallback: LoadInitialCallback<Int, MusicSearchModel>? = null,
        loadCallback: LoadCallback<Int, MusicSearchModel>? = null
    ) {
        CoroutineScope(Dispatchers.IO).launchSafe {

            val nextPage = page + 1
            val offset = getComputeOffset(page, nextPage, pageSize)

            getSongListFlowUseCase(type, offset)
                .onEach { models ->

                    handleHideLoading(page)
                    handleShowError(page, models)

                    when (type) {
                        ALL -> {
                            loadMusicWithoutLoadMore(models, loadInitialCallback, loadCallback)
                        }
                        else -> {
                            loadMusicWithLoadMore(
                                models,
                                nextPage,
                                loadInitialCallback,
                                loadCallback
                            )
                        }
                    }
                }.catch {

                    handleLoadMusicError(page)
                    loadMusicErrorOrEmpty(loadInitialCallback, loadCallback)
                }.launchSafeIn(this)
        }
    }

    private fun getComputeOffset(page: Int, nextPage: Int, pageSize: Int): String {
        return if (nextPage >= 2) {
            page * pageSize + 1
        } else {
            1
        }.toString()
    }

    private fun getSongListFlowUseCase(
        type: MusicType,
        offset: String
    ): Flow<List<MusicSearchModel>> {
        return when (type) {
            ALL -> {
                getSearchAllUseCase.execute(keyword, theme)
            }
            SONG -> {
                getSearchSongUseCase.execute(keyword, theme, offset)
            }
            PLAYLIST -> {
                getSearchPlaylistUseCase.execute(keyword, theme, offset)
            }
            ALBUM -> {
                getSearchAlbumUseCase.execute(keyword, theme, offset)
            }
            ARTIST -> {
                getSearchArtistUseCase.execute(keyword, theme, offset)
            }
        }
    }

    private fun handleLoadMusicError(page: Int) {
        if (page == 0) {
            showError.postValue(Unit)
            hideLoading.postValue(Unit)
        }
    }

    private fun handleShowError(page: Int, models: List<MusicSearchModel>) {
        if (page == 0 && models.isEmpty()) {
            showError.postValue(Unit)
        }
    }

    private fun handleHideLoading(page: Int) {
        if (page == 0) {
            hideLoading.postValue(Unit)
        }
    }

    private fun loadMusicWithLoadMore(
        models: List<MusicSearchModel>,
        nextPage: Int,
        loadInitialCallback: LoadInitialCallback<Int, MusicSearchModel>?,
        loadCallback: LoadCallback<Int, MusicSearchModel>?
    ) {
        when (models.isEmpty()) {
            true -> {
                loadMusicErrorOrEmpty(loadInitialCallback, loadCallback)
            }
            false -> {
                loadInitialCallback?.onResult(models, null, nextPage)
                loadCallback?.onResult(models, nextPage)
            }
        }
    }

    private fun loadMusicWithoutLoadMore(
        models: List<MusicSearchModel>,
        loadInitialCallback: LoadInitialCallback<Int, MusicSearchModel>?,
        loadCallback: LoadCallback<Int, MusicSearchModel>?
    ) {
        loadInitialCallback?.onResult(models, 0, models.size, null, null)
        loadCallback?.onResult(models, null)
    }

    private fun loadMusicErrorOrEmpty(
        loadInitialCallback: LoadInitialCallback<Int, MusicSearchModel>?,
        loadCallback: LoadCallback<Int, MusicSearchModel>?
    ) {
        loadInitialCallback?.onResult(emptyList(), 0, 0, null, null)
        loadCallback?.onResult(emptyList(), null)
    }
}
