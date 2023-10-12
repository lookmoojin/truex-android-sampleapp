package com.truedigital.core.domain.usecase

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetCountViewFormatUseCaseImplTest {

    private lateinit var getCountViewFormatUseCase: GetCountViewFormatUseCase

    @BeforeEach
    fun setUp() {
        getCountViewFormatUseCase = GetCountViewFormatUseCaseImpl()
    }

    @Test
    fun execute_countViewLessThan1000_returnCorrectCountViewFormat() {
        val resultValue01 = getCountViewFormatUseCase.execute(0.0)
        val resultValue02 = getCountViewFormatUseCase.execute(999.0)

        assertEquals(resultValue01, "0")
        assertEquals(resultValue02, "999")
    }

    @Test
    fun execute_countViewBetween1000and99999_returnCorrectCountViewFormat() {
        val resultValue01 = getCountViewFormatUseCase.execute(1000.0)
        val resultValue02 = getCountViewFormatUseCase.execute(99999.0)
        val resultValue03 = getCountViewFormatUseCase.execute(1393.0)
        val resultValue04 = getCountViewFormatUseCase.execute(52014.0)

        assertEquals(resultValue01, "1K")
        assertEquals(resultValue02, "99.9K")
        assertEquals(resultValue03, "1.3K")
        assertEquals(resultValue04, "52K")
    }

    @Test
    fun execute_countViewBetween100000and999999_returnCorrectCountViewFormat() {
        val resultValue01 = getCountViewFormatUseCase.execute(100000.0)
        val resultValue02 = getCountViewFormatUseCase.execute(999999.0)
        val resultValue03 = getCountViewFormatUseCase.execute(151924.0)

        assertEquals(resultValue01, "100K")
        assertEquals(resultValue02, "999K")
        assertEquals(resultValue03, "151K")
    }

    @Test
    fun execute_countViewBetween1000000and9999999_returnCorrectCountViewFormat() {
        val resultValue01 = getCountViewFormatUseCase.execute(1000000.0)
        val resultValue02 = getCountViewFormatUseCase.execute(9999999.0)
        val resultValue03 = getCountViewFormatUseCase.execute(8751924.0)
        val resultValue04 = getCountViewFormatUseCase.execute(3089493.0)

        assertEquals(resultValue01, "1M")
        assertEquals(resultValue02, "9.9M")
        assertEquals(resultValue03, "8.7M")
        assertEquals(resultValue04, "3M")
    }

    @Test
    fun execute_countViewMoreThan10000000_returnCorrectCountViewFormat() {
        val resultValue01 = getCountViewFormatUseCase.execute(10000000.0)
        val resultValue02 = getCountViewFormatUseCase.execute(10900000.0)

        assertEquals(resultValue01, "10M")
        assertEquals(resultValue02, "10M")
    }
}
