package com.truedigital.navigation.domain.usecase

import com.truedigital.core.constant.FireBaseConstant
import com.truedigital.core.utils.SharedPrefsInterface
import javax.inject.Inject

interface GetTodayPersonaSegmentEnableUseCase {
    fun execute(): Boolean
}

class GetTodayPersonaSegmentEnableUseCaseImpl @Inject constructor(
    private val sharedPrefsInterface: SharedPrefsInterface
) : GetTodayPersonaSegmentEnableUseCase {
    override fun execute(): Boolean {
        return sharedPrefsInterface.get(
            FireBaseConstant.TODAY_PERSONA_SEGMENT_ENABLE,
            Boolean::class
        )
            ?: false
    }
}
