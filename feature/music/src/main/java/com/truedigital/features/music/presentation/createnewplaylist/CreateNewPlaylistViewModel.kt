package com.truedigital.features.music.presentation.createnewplaylist

import android.os.Bundle
import androidx.lifecycle.LiveData
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.extensions.launchSafe
import com.truedigital.features.music.domain.myplaylist.usecase.CreateNewPlaylistUseCase
import com.truedigital.features.music.domain.usecase.router.MusicRouterUseCase
import com.truedigital.features.music.navigation.router.MusicCreateNewPlaylistToMyPlaylist
import com.truedigital.features.music.presentation.myplaylist.MyPlaylistFragment
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class CreateNewPlaylistViewModel @Inject constructor(
    private val router: MusicRouterUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val createNewPlaylistUseCase: CreateNewPlaylistUseCase
) : ScopedViewModel() {

    private val createNewPlaylistSuccess = SingleLiveEvent<Int>()
    private val showErrorDialog = SingleLiveEvent<Unit>()
    private val showLoading = SingleLiveEvent<Unit>()
    private val hideLoading = SingleLiveEvent<Unit>()

    fun onCreateNewPlayListSuccess(): LiveData<Int> = createNewPlaylistSuccess
    fun onShowErrorDialog(): LiveData<Unit> = showErrorDialog
    fun onShowLoading(): LiveData<Unit> = showLoading
    fun onHideLoading(): LiveData<Unit> = hideLoading

    fun createNewPlaylist(playlistName: String) = launchSafe {
        createNewPlaylistUseCase.execute(playlistName)
            .flowOn(coroutineDispatcher.io())
            .onStart {
                showLoading.value = Unit
            }
            .onCompletion {
                hideLoading.value = Unit
            }
            .catch {
                showErrorDialog.value = Unit
            }
            .collectSafe {
                hideLoading.value = Unit
                createNewPlaylistSuccess.value = it
            }
    }

    fun navigateToPlaylist(playlistId: Int) {
        val mBundle = Bundle().apply {
            putInt(MyPlaylistFragment.PLAYLIST_ID_SLUG, playlistId)
        }
        router.execute(MusicCreateNewPlaylistToMyPlaylist, mBundle)
    }
}
