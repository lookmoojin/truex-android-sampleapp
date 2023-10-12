package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.media.effect.EffectFactory
import com.truedigital.core.base.ScopedViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject

class TrueCloudV3PhotoEditorAdjustViewModel @Inject constructor() : ScopedViewModel() {

    companion object {
        private const val EFFECT_DEBOUNCE = 1000L
    }

    val currentEffect: Flow<Pair<UiEffect?, Double>>

    private val _currentEffect = MutableStateFlow<UiEffect?>(null)
    private val _effectScale = MutableStateFlow(0.0)

    init {
        currentEffect =
            combine(_currentEffect.debounce(EFFECT_DEBOUNCE), _effectScale) { effect, scale ->
                effect to scale
            }
    }
    fun setEffect(effect: UiEffect?) {
        _currentEffect.value = effect
    }

    fun setScale(scale: Double) {
        _effectScale.value = scale
    }

    enum class UiEffect(val factoryName: String, val parameterKey: String) {
        BRIGHTNESS(EffectFactory.EFFECT_BRIGHTNESS, "brightness"),
        CONTRAST(EffectFactory.EFFECT_CONTRAST, "contrast"),
        SATURATION(EffectFactory.EFFECT_SATURATE, "scale"),
        SHARPNESS(EffectFactory.EFFECT_SHARPEN, "scale"),
        TEMPERATURE(EffectFactory.EFFECT_TEMPERATURE, "scale")
    }
}
