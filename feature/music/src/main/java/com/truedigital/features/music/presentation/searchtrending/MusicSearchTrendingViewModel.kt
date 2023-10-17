package com.truedigital.features.music.presentation.searchtrending

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.features.music.data.trending.repository.MusicTrendingAlbumCacheRepository
import com.truedigital.features.music.data.trending.repository.MusicTrendingArtistCacheRepository
import com.truedigital.features.music.data.trending.repository.MusicTrendingPlaylistCacheRepository
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingAlbumUseCase
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingArtistsUseCase
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingPlaylistUseCase
import com.truedigital.features.music.presentation.searchtrending.adapter.MusicSearchTrendingAdapter
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class MusicSearchTrendingViewModel @Inject constructor(
    private val musicTrendingArtistsUseCase: GetMusicTrendingArtistsUseCase,
    private val musicTrendingPlaylistUseCase: GetMusicTrendingPlaylistUseCase,
    private val musicTrendingAlbumUseCase: GetMusicTrendingAlbumUseCase,
    private val musicTrendingArtistCacheRepository: MusicTrendingArtistCacheRepository,
    private val musicTrendingPlaylistCacheRepository: MusicTrendingPlaylistCacheRepository,
    private val musicTrendingAlbumCacheRepository: MusicTrendingAlbumCacheRepository
) : ScopedViewModel() {

    private val trendingDataItems =
        MutableLiveData<ArrayList<MusicSearchTrendingAdapter.DataItem>>()
    private val showLoading = SingleLiveEvent<Unit>()
    private val hideLoading = SingleLiveEvent<Unit>()
    private val showError = SingleLiveEvent<Unit>()
    private val hideError = SingleLiveEvent<Unit>()

    fun onTrendingDataItems(): LiveData<ArrayList<MusicSearchTrendingAdapter.DataItem>> =
        trendingDataItems

    fun onShowLoading(): LiveData<Unit> = showLoading
    fun onHideLoading(): LiveData<Unit> = hideLoading
    fun onShowError(): LiveData<Unit> = showError
    fun onHideError(): LiveData<Unit> = hideError

    fun searchTrending() = viewModelScope.launchSafe {
        val artistDataItems = arrayListOf<MusicSearchTrendingAdapter.DataItem>()
        val playlistDataItems = arrayListOf<MusicSearchTrendingAdapter.DataItem>()
        val albumDataItems = arrayListOf<MusicSearchTrendingAdapter.DataItem>()

        flowOf(
            musicTrendingArtistsUseCase.execute(),
            musicTrendingPlaylistUseCase.execute(),
            musicTrendingAlbumUseCase.execute()
        )
            .flattenMerge()
            .onStart {
                showLoading.value = Unit
            }
            .onEach { data ->
                when (data?.getOrNull(1)) {
                    is MusicSearchTrendingAdapter.DataItem.TrendingArtistItem -> {
                        artistDataItems.addAll(data)
                    }
                    is MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem -> {
                        playlistDataItems.addAll(data)
                    }
                    is MusicSearchTrendingAdapter.DataItem.TrendingAlbumItem -> {
                        albumDataItems.addAll(data)
                    }
                    else -> Unit
                }
            }
            .onCompletion {
                val dataItems = arrayListOf<MusicSearchTrendingAdapter.DataItem>()
                dataItems.addAll(artistDataItems)
                dataItems.addAll(playlistDataItems)
                dataItems.addAll(albumDataItems)

                hideLoading.value = Unit
                if (!dataItems.isNullOrEmpty()) {
                    trendingDataItems.value = dataItems
                } else {
                    showError.value = Unit
                }
            }
            .catch {
                hideLoading.value = Unit
                showError.value = Unit
            }
            .launchSafeIn(this)
    }

    fun clearTrendingData() {
        trendingDataItems.value = arrayListOf()
    }

    fun clearCache() {
        musicTrendingArtistCacheRepository.clearCacheTrendingArtist()
        musicTrendingPlaylistCacheRepository.clearCacheTrendingPlaylist()
        musicTrendingAlbumCacheRepository.clearCacheTrendingAlbum()
    }
}
