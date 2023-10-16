package com.truedigital.navigation.usecase

import com.truedigital.core.constant.FireBaseConstant
import com.truedigital.core.utils.SharedPrefsUtils
import com.truedigital.navigation.domain.usecase.GetTodayPersonaSegmentEnableUseCase
import com.truedigital.navigation.domain.usecase.GetTodayPersonaSegmentEnableUseCaseImpl
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetTodayPersonaSegmentEnableUseCaseTest {
    private lateinit var getTodayPersonaSegmentEnableUseCase: GetTodayPersonaSegmentEnableUseCase
    private val sharedPref: SharedPrefsUtils = mockk()

    @BeforeEach
    fun setup() {
        getTodayPersonaSegmentEnableUseCase = GetTodayPersonaSegmentEnableUseCaseImpl(
            sharedPrefsInterface = sharedPref
        )
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun testGetConfig_TodayPersonalSegmentEnable_True() {
        every {
            sharedPref.get<Any>(
                FireBaseConstant.TODAY_PERSONA_SEGMENT_ENABLE,
                any()
            )
        } returns true
        val test = getTodayPersonaSegmentEnableUseCase.execute()
        assertTrue { test }
    }

    @Test
    fun testGetConfig_TodayPersonalSegmentEnable_False() {
        every {
            sharedPref.get<Any>(
                FireBaseConstant.TODAY_PERSONA_SEGMENT_ENABLE,
                any()
            )
        } returns false
        val test = getTodayPersonaSegmentEnableUseCase.execute()
        assertFalse { test }
    }

    @Test
    fun testGetConfig_TodayPersonalSegmentEnable_Null() {
        every {
            sharedPref.get<Any>(
                FireBaseConstant.TODAY_PERSONA_SEGMENT_ENABLE,
                any()
            )
        } returns null
        val test = getTodayPersonaSegmentEnableUseCase.execute()
        assertFalse { test }
    }
}
