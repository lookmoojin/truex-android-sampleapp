package com.truedigital.features.truecloudv3.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.features.truecloudv3.domain.usecase.PatchMigrateStatusUseCase
import com.truedigital.features.truecloudv3.navigation.IntroMigrateToMigrating
import com.truedigital.features.truecloudv3.navigation.router.IntroMigrateDataRouterUseCase
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class IntroMigrateDataViewModel @Inject constructor(
    private val router: IntroMigrateDataRouterUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val patchMigrateStatusUseCase: PatchMigrateStatusUseCase
) : ScopedViewModel() {
    val onLaterClicked = MutableLiveData<Boolean>()
    fun onClickLater() {
        patchMigrateStatusUseCase.execute()
            .flowOn(coroutineDispatcher.io())
            .onEach {
                onLaterClicked.value = true
            }
            .launchSafeIn(viewModelScope)
    }

    fun onClickMigrate() {
        router.execute(IntroMigrateToMigrating)
    }
}
