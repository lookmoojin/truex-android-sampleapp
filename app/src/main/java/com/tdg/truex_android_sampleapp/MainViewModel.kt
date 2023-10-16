package com.tdg.truex_android_sampleapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tdg.login.base.CoroutineDispatcherProvider
import com.tdg.login.base.collectSafe
import com.tdg.login.base.launchSafe
import com.tdg.login.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider,
) : ViewModel() {

    private val _onLoginSuccess = MutableLiveData<String>()
    val onLoginSuccess: LiveData<String>
        get() = _onLoginSuccess

    fun login() {
        viewModelScope.launchSafe {
            loginUseCase.login("", "")
                .flowOn(coroutineDispatcher.io())
                .catch { error ->
                    Log.d("MainViewModel","error ${error.message}")
                }
                .collectSafe {
                    _onLoginSuccess.value = it
                }
        }
    }
}