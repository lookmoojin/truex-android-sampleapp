package com.truedigital.features.music.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.truedigital.features.music.domain.track.usecase.GetTrackUseCase
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.foundation.extension.SingleLiveEvent
import com.truedigital.foundation.extension.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MusicTrackViewModel @Inject constructor(
    private val getTrackUseCase: GetTrackUseCase
) : ViewModel() {

    private var compositeDispatcher = CompositeDisposable()

    private val playTrack = SingleLiveEvent<Track>()
    private val errorPlayTrack = SingleLiveEvent<Unit>()

    fun onPlayTrack(): LiveData<Track> = playTrack
    fun onErrorPlayTrack(): LiveData<Unit> = errorPlayTrack

    override fun onCleared() {
        super.onCleared()
        compositeDispatcher.clear()
    }

    fun getTrack(id: Int) {
        getTrackUseCase.execute(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { track ->
                    playTrack.value = track
                },
                {
                    errorPlayTrack.value = Unit
                }
            ).addTo(compositeDispatcher)
    }
}
