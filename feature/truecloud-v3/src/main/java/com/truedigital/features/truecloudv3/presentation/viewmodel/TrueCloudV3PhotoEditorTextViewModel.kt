package com.truedigital.features.truecloudv3.presentation.viewmodel

import androidx.lifecycle.LiveData
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.foundation.extension.LiveEvent
import javax.inject.Inject

class TrueCloudV3PhotoEditorTextViewModel @Inject constructor() : ScopedViewModel() {

    companion object {
        private const val VIEW_FLIPPER_MAIN_INDEX = 0
    }

    private val _onSaveSetting = LiveEvent<Unit>()
    private val _onResetMenu = LiveEvent<Unit>()
    private val _onShowConfirm = LiveEvent<Unit>()
    val onSaveSetting: LiveData<Unit> get() = _onSaveSetting
    val onResetMenu: LiveData<Unit> get() = _onResetMenu
    val onShowConfirm: LiveData<Unit> get() = _onShowConfirm

    fun onConfirmClick(displayChild: Int) {
        when (displayChild) {
            VIEW_FLIPPER_MAIN_INDEX -> {
                _onSaveSetting.value = Unit
            }

            else -> {
                _onResetMenu.value = Unit
            }
        }
    }

    fun onBackClick(displayChild: Int) {
        when (displayChild) {
            VIEW_FLIPPER_MAIN_INDEX -> {
                _onShowConfirm.value = Unit
            }

            else -> {
                _onResetMenu.value = Unit
            }
        }
    }
}
