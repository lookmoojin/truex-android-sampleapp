package com.tdg.truexsampleapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tdg.login.base.CoroutineDispatcherProvider
import com.tdg.login.base.collectSafe
import com.tdg.login.base.launchSafe
import com.tdg.onboarding.domain.model.WhatNewData
import com.tdg.onboarding.domain.usecase.GetWhatNewConfigUseCase
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MenuViewModel @Inject constructor(
    private val getWhatNewConfigUseCase: GetWhatNewConfigUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider,
) : ViewModel() {

    private val _onDisplay = MutableLiveData<WhatNewData>()
    val onDisplay: LiveData<WhatNewData>
        get() = _onDisplay

    fun getWhatNewConfig() {
        viewModelScope.launchSafe {
            getWhatNewConfigUseCase.execute()
                .flowOn(coroutineDispatcher.io())
                .collectSafe {
                    it?.let {
                        _onDisplay.value = it
                    }
                }
        }
    }
}