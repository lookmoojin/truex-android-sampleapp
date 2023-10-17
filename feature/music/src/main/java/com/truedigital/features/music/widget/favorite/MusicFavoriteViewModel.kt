package com.truedigital.features.music.widget.favorite

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.truedigital.features.music.constant.FavoriteType
import com.truedigital.features.music.domain.favorite.AddMusicFavoriteUseCaseImpl
import com.truedigital.features.music.domain.favorite.FetchMusicFavoriteUseCaseImpl
import com.truedigital.features.music.domain.favorite.RemoveMusicFavoriteUseCaseImpl
import com.truedigital.features.tuned.common.extensions.tunedSubscribe
import com.truedigital.foundation.extension.SingleLiveEvent
import timber.log.Timber
import javax.inject.Inject

class MusicFavoriteViewModel @Inject constructor(
    private val addMusicFavoriteUseCase: AddMusicFavoriteUseCaseImpl,
    private val fetchMusicFavoriteUseCase: FetchMusicFavoriteUseCaseImpl,
    private val removeMusicFavoriteUseCase: RemoveMusicFavoriteUseCaseImpl
) : ViewModel() {

    private val favSong = SingleLiveEvent<Boolean>()
    private val addFav = SingleLiveEvent<Unit>()
    private val removeFav = SingleLiveEvent<Unit>()
    private val favError = SingleLiveEvent<Unit>()
    private val favAddError = SingleLiveEvent<Unit>()
    private val favRemoveError = SingleLiveEvent<Unit>()
    private var isFavorite = false

    fun onFavSong(): LiveData<Boolean> = favSong
    fun onAddFavToast(): LiveData<Unit> = addFav
    fun onRemoveFavToast(): LiveData<Unit> = removeFav
    fun onFavErrorToast(): LiveData<Unit> = favError
    fun onFavAddErrorToast(): LiveData<Unit> = favAddError
    fun onFavRemoveErrorToast(): LiveData<Unit> = favRemoveError

    fun fetchFavorite(id: Int, favType: FavoriteType) {
        fetchMusicFavoriteUseCase.execute(id, favType)
            .tunedSubscribe(
                onSuccess = {
                    isFavorite = it
                    favSong.value = it
                },
                onError = {
                    Timber.e(it)
                }
            )
    }

    fun toggleFavorite(id: Int, favType: FavoriteType) {
        val wasFavorite = isFavorite
        fetchMusicFavoriteUseCase.execute(id, favType).flatMap {
            when {
                it -> {
                    removeMusicFavoriteUseCase.execute(id, favType)
                        .map { false }
                }
                else -> {
                    addMusicFavoriteUseCase.execute(id, favType)
                        .map { true }
                }
            }
        }.tunedSubscribe(
            onSuccess = {
                favSong.value = it
                isFavorite = it
                when {
                    it -> addFav.value = Unit
                    else -> removeFav.value = Unit
                }
            },
            onError = {
                isFavorite = wasFavorite
                favError.value = Unit
                if (wasFavorite) {
                    favAddError.value = Unit
                } else {
                    favRemoveError.value = Unit
                }
            }
        )
    }

    @VisibleForTesting
    fun setPrivateData(isFavorite: Boolean) {
        this.isFavorite = isFavorite
    }
}
