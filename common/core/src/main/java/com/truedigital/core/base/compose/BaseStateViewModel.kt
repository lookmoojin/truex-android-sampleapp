package com.truedigital.core.base.compose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.truedigital.core.base.compose.listenable.Disposable
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.coroutines.DefaultCoroutineDispatcherProvider
import com.truedigital.foundation.extension.SingleLiveEvent

abstract class BaseStateViewModel<VS : Any, VE : Any>(
    protected val dispatcherProvider: CoroutineDispatcherProvider = DefaultCoroutineDispatcherProvider(),
    initialState: VS
) : ViewModel() {

    protected val compositeDisposable: MutableList<Disposable> = mutableListOf()

    protected val mutableState = MutableLiveData(initialState)
    val state: LiveData<VS> = mutableState
    val currentState get() = mutableState.value!!

    private val mutableEvent: SingleLiveEvent<VE> = SingleLiveEvent()
    val event: LiveData<VE> = mutableEvent

    protected fun postState(value: VS) {
        mutableState.postValue(value)
    }

    protected inline fun postState(builder: VS.() -> VS) {
        postState(currentState.builder())
    }

    protected fun getState(): VS? {
        return mutableState.value
    }

    protected fun setState(value: VS) {
        mutableState.value = value
    }

    protected inline fun setState(builder: VS.() -> VS) {
        mutableState.value = currentState.builder()
    }

    protected fun postEvent(value: VE) {
        mutableEvent.postValue(value)
    }

    protected fun setEvent(value: VE) {
        mutableEvent.value = value
    }

    override fun onCleared() {
        compositeDisposable.forEach {
            it.dispose()
        }
        super.onCleared()
    }
}
