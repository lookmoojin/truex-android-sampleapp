package com.truedigital.features.truecloudv3.presentation.viewmodel

import androidx.annotation.ColorInt
import com.truedigital.core.base.ScopedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject

class TrueCloudV3PhotoEditorFocusViewModel @Inject constructor() : ScopedViewModel() {

    companion object {
        private const val SIZE_DEBOUNCE = 200L
    }

    private val _brushColor = MutableStateFlow<Int?>(null)
    private val _brushSize = MutableStateFlow(0.0)
    val currentBrush =
        combine(_brushColor, _brushSize.debounce(SIZE_DEBOUNCE)) { color, size ->
            color to size
        }

    fun setBrushColor(@ColorInt color: Int) {
        _brushColor.value = color
    }

    fun setBrushSize(size: Double) {
        _brushSize.value = size
    }
}
