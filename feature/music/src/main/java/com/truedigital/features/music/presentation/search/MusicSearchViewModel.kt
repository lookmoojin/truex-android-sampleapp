package com.truedigital.features.music.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.Config
import androidx.paging.toLiveData
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.extensions.launchSafe
import com.truedigital.features.music.data.search.datasource.MusicSearchDataSourceFactory
import com.truedigital.features.music.data.search.model.GroupMusicDataModel
import com.truedigital.features.music.domain.search.model.MusicType
import com.truedigital.features.music.domain.search.model.MusicType.ALBUM
import com.truedigital.features.music.domain.search.model.MusicType.ALL
import com.truedigital.features.music.domain.search.model.MusicType.ARTIST
import com.truedigital.features.music.domain.search.model.MusicType.PLAYLIST
import com.truedigital.features.music.domain.search.model.MusicType.SONG
import com.truedigital.features.music.domain.search.model.ThemeType
import com.truedigital.features.music.domain.search.model.ThemeType.Companion.getThemeType
import com.truedigital.features.music.domain.search.model.TopMenuModel
import com.truedigital.features.music.domain.search.usecase.GetSearchAlbumUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchAllUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchArtistUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchPlaylistUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchSongUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchTopMenuUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import javax.inject.Inject

class MusicSearchViewModel @Inject constructor(
    private val getSearchTopMenuUseCase: GetSearchTopMenuUseCase,
    private val getSearchAllUseCase: GetSearchAllUseCase,
    private val getSearchAlbumUseCase: GetSearchAlbumUseCase,
    private val getSearchArtistUseCase: GetSearchArtistUseCase,
    private val getSearchPlaylistUseCase: GetSearchPlaylistUseCase,
    private val getSearchSongUseCase: GetSearchSongUseCase
) : ScopedViewModel() {

    companion object {
        private const val SONG_POSITION = 1
        private const val ARTIST_POSITION = 2
        private const val ALBUM_POSITION = 3
        private const val PLAYLIST_POSITION = 4
        private const val DELAY_TIME = 300L
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 5
    }

    private var currentTopMenuType = ALL
    private var loadData = false

    private val musicSearchTopMenuList = SingleLiveEvent<List<TopMenuModel>>()
    private val themeType = SingleLiveEvent<ThemeType>()
    private val selectedTopMenu = SingleLiveEvent<Unit>()
    private val loadMusicSearchData = MutableLiveData<GroupMusicDataModel>()

    fun onMusicSearchTopMenuList(): LiveData<List<TopMenuModel>> = musicSearchTopMenuList
    fun onSelectedTopMenu(): LiveData<Unit> = selectedTopMenu
    fun isLoadData(): Boolean = loadData

    fun onRenderMusicList() =
        loadMusicSearchData.switchMap { groupMusicDataModel -> groupMusicDataModel.pagedList }

    fun onShowLoading() = loadMusicSearchData.switchMap { groupMusicDataModel ->
        groupMusicDataModel.showLoading
    }

    fun onHideLoading() = loadMusicSearchData.switchMap { groupMusicDataModel ->
        groupMusicDataModel.hideLoading
    }

    fun onShowError() = loadMusicSearchData.switchMap { groupMusicDataModel ->
        groupMusicDataModel.showError
    }

    fun onHideError() = loadMusicSearchData.switchMap { groupMusicDataModel ->
        groupMusicDataModel.hideError
    }

    fun setTheme(themeType: ThemeType) {
        this.themeType.value = themeType
    }

    fun getCurrentTheme(): ThemeType {
        return themeType.value?.name?.let {
            getThemeType(it)
        } ?: ThemeType.DARK
    }

    fun updateCurrentTopMenuType(type: MusicType?) {
        currentTopMenuType = type ?: ALL
    }

    fun getCurrentTopMenuPosition(): Int {
        return when (currentTopMenuType) {
            ALL -> 0
            SONG -> SONG_POSITION
            ARTIST -> ARTIST_POSITION
            ALBUM -> ALBUM_POSITION
            PLAYLIST -> PLAYLIST_POSITION
        }
    }

    fun searchWithCurrentTopMenu(keyword: String) {
        launchSafe {
            delay(DELAY_TIME)
            search(keyword, currentTopMenuType)
        }
    }

    fun fetchTopMenu() {
        launchSafe {
            val topMenuList = async { getTopMenuList() }
            musicSearchTopMenuList.value = topMenuList.await()
        }
    }

    fun onClickedTopMenu(musicType: MusicType?) {
        updateCurrentTopMenuType(musicType)
        selectedTopMenu.value = Unit
    }

    fun resetLoadData() {
        loadData = false
    }

    private suspend fun getTopMenuList(): List<TopMenuModel> {
        return getSearchTopMenuUseCase.execute(getCurrentTopMenuPosition(), getCurrentTheme())
    }

    private fun search(keyword: String, type: MusicType) {
        launchSafe {

            val musicSearchDataSourceFactory = MusicSearchDataSourceFactory(
                getSearchAllUseCase,
                getSearchAlbumUseCase,
                getSearchArtistUseCase,
                getSearchPlaylistUseCase,
                getSearchSongUseCase,
                keyword,
                getCurrentTheme(),
                type
            )

            val showLoading =
                musicSearchDataSourceFactory.getDatasource().switchMap { dataSource ->
                    dataSource.showLoading
                }

            val hideLoading =
                musicSearchDataSourceFactory.getDatasource().switchMap { dataSource ->
                    dataSource.hideLoading
                }

            val showError =
                musicSearchDataSourceFactory.getDatasource().switchMap { dataSource ->
                    dataSource.showError
                }

            val hideError =
                musicSearchDataSourceFactory.getDatasource().switchMap { dataSource ->
                    dataSource.hideError
                }

            val pagedList = musicSearchDataSourceFactory.toLiveData(
                config = Config(
                    pageSize = PAGE_SIZE,
                    enablePlaceholders = true,
                    prefetchDistance = PREFETCH_DISTANCE
                )
            )

            loadData = true

            loadMusicSearchData.value = GroupMusicDataModel(
                pagedList = pagedList,
                showLoading = showLoading,
                hideLoading = hideLoading,
                showError = showError,
                hideError = hideError
            )
        }
    }
}
