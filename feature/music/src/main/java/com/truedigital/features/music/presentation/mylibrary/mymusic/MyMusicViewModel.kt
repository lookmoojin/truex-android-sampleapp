package com.truedigital.features.music.presentation.mylibrary.mymusic

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.domain.myplaylist.model.MusicMyPlaylistModel
import com.truedigital.features.music.domain.myplaylist.usecase.GetMyPlaylistShelfUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MyMusicViewModel @Inject constructor(
    private val getMyPlaylistShelfUseCase: GetMyPlaylistShelfUseCase,
    private val analyticManager: AnalyticManager
) : ViewModel() {

    private val myPlaylist = SingleLiveEvent<List<MusicMyPlaylistModel>>()
    private val pullRefresh = SingleLiveEvent<Boolean>()

    fun onGetMyPlaylist(): LiveData<List<MusicMyPlaylistModel>> = myPlaylist
    fun onPullRefresh(): LiveData<Boolean> = pullRefresh

    fun fetchMyPlaylist() = viewModelScope.launch {
        getMyPlaylistShelfUseCase.execute()
            .flowOn(Dispatchers.IO)
            .catch {
                Timber.e(it)
            }
            .onCompletion {
                pullRefresh.value = false
            }
            .collect {
                myPlaylist.value = it
            }
    }

    fun trackFASelectContent(musicMyPlaylistModel: MusicMyPlaylistModel) {
        val hashMap = hashMapOf<String, Any>(
            MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SELECT_CONTENT,
            MeasurementConstant.Music.Key.MEASUREMENT_MUSIC_MY_PLAYLIST to MusicConstant.MyPlaylist.MY_PLAYLIST
        ).apply {
            when (musicMyPlaylistModel) {
                is MusicMyPlaylistModel.CreateMyPlaylistModel -> {
                    put(
                        MeasurementConstant.Key.KEY_TITLE,
                        MusicConstant.MyPlaylist.CREATE_A_NEW_LIST
                    )
                }
                is MusicMyPlaylistModel.MyPlaylistModel -> {
                    put(MeasurementConstant.Key.KEY_TITLE, MusicConstant.MyPlaylist.MY_PLAYLIST)
                    put(MeasurementConstant.Key.KEY_ITEM_INDEX, musicMyPlaylistModel.index)
                }
            }
        }
        analyticManager.trackEvent(hashMap)
    }
}
