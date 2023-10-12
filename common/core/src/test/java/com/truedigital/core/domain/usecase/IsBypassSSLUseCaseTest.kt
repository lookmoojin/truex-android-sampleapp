package com.truedigital.core.domain.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.BuildConfig
import com.truedigital.core.data.repository.DeviceRepository
import com.truedigital.core.domain.usecase.model.DataAnimalModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IsBypassSSLUseCaseTest {

    private val getAnimalUseCase: GetAnimalUseCase = mock()
    private val deviceRepository: DeviceRepository = mock()
    private lateinit var isBypassSSLUseCase: IsBypassSSLUseCase
    private val deviceID = "test"

    @BeforeEach
    fun setUp() {
        isBypassSSLUseCase = IsBypassSSLUseCaseImpl(getAnimalUseCase, deviceRepository)
    }

    @Test
    fun execute_cat_true_and_whale_is_empty_then_return_false() {
        whenever(getAnimalUseCase.execute()).thenReturn(
            DataAnimalModel(
                cat = true
            )
        )
        whenever(deviceRepository.getAndroidId()).thenReturn(deviceID)
        val resultValue01 = isBypassSSLUseCase.execute()

        assertEquals(false, resultValue01)
    }

    @Test
    fun execute_cat_true_and_whale_is_not_empty_then_return_true() {
        whenever(getAnimalUseCase.execute()).thenReturn(
            DataAnimalModel(
                cat = true,
                whale = arrayListOf("test")
            )
        )
        whenever(deviceRepository.getAndroidId()).thenReturn(deviceID)
        val resultValue01 = isBypassSSLUseCase.execute()

        assertEquals(true, resultValue01)
    }

    @Test
    fun execute_cat_false_and_whale_is_empty_then_return_true() {
        whenever(getAnimalUseCase.execute()).thenReturn(
            DataAnimalModel(
                cat = false
            )
        )
        whenever(deviceRepository.getAndroidId()).thenReturn(deviceID)
        val resultValue01 = isBypassSSLUseCase.execute()

        assertEquals(true, resultValue01)
    }

    @Test
    fun execute_cat_false_and_whale_is_not_empty_then_return_true() {
        whenever(getAnimalUseCase.execute()).thenReturn(
            DataAnimalModel(
                cat = false,
                whale = arrayListOf("test")
            )
        )
        whenever(deviceRepository.getAndroidId()).thenReturn(deviceID)
        val resultValue01 = isBypassSSLUseCase.execute()

        assertEquals(true, resultValue01)
    }

    @Test
    fun execute_buildconfig_debug_cat_false_and_whale_is_not_empty_then_return_true() {
        whenever(getAnimalUseCase.execute()).thenReturn(
            DataAnimalModel(
                cat = false,
                whale = arrayListOf("test")
            )
        )
        whenever(deviceRepository.getAndroidId()).thenReturn(deviceID)
        val resultValue01 = isBypassSSLUseCase.execute(BuildConfig.DEBUG)

        assertEquals(true, resultValue01)
    }
}
