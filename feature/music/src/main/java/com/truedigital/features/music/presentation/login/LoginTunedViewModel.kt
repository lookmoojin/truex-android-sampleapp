package com.truedigital.features.music.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truedigital.features.tuned.common.extensions.cacheOnMainThread
import com.truedigital.features.tuned.common.extensions.tunedSubscribe
import com.truedigital.features.tuned.domain.usecase.data.DeleteRoomDataUseCase
import com.truedigital.features.tuned.presentation.main.facade.MusicAuthenticationFacade
import com.truedigital.foundation.extension.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginTunedViewModel @Inject constructor(
    private val deleteRoomDataUseCase: DeleteRoomDataUseCase,
    private val musicAuthenticationFacade: MusicAuthenticationFacade
) : ViewModel() {

    private var compositeDispatcher = CompositeDisposable()

    private val loginTunedSuccess = SingleLiveEvent<Unit>()
    private val loginTunedError = SingleLiveEvent<Unit>()

    fun onLoginTunedSuccess(): LiveData<Unit> = loginTunedSuccess
    fun onLoginTunedError(): LiveData<Unit> = loginTunedError

    override fun onCleared() {
        super.onCleared()
        compositeDispatcher.clear()
    }

    fun loginTunedMusic(userId: Int, accessToken: String) {
        val isLogout = musicAuthenticationFacade.isLogout()
        if (musicAuthenticationFacade.getTrueUserId() != userId || isLogout) {
            musicAuthenticationFacade.logout()
            deleteRoomData()
        }
        musicAuthenticationFacade.loginJwt(userId, accessToken)
            .cacheOnMainThread()
            .tunedSubscribe(
                {
                    loginTunedSuccess.value = Unit
                },
                {
                    loginTunedError.value = Unit
                }
            )
    }

    private fun deleteRoomData() = viewModelScope.launch {
        deleteRoomDataUseCase.execute()
    }
}
