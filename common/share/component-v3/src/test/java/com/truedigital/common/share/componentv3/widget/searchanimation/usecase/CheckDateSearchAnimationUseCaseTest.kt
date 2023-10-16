package com.truedigital.common.share.componentv3.widget.searchanimation.usecase

import com.truedigital.common.share.componentv3.widget.searchanimation.model.SearchAnimationTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.text.ParseException

class CheckDateSearchAnimationUseCaseTest {

    private lateinit var checkDateSearchAnimationUseCase: CheckDateSearchAnimationUseCase

    @BeforeEach
    fun setup() {
        checkDateSearchAnimationUseCase = CheckDateSearchAnimationUseCaseImpl()
    }

    @Test
    fun checkDateSearchAnimationUseCaseWithStartDateAndEndDateAndValid() {
        val searchAnimationTime = SearchAnimationTime().apply {
            startDate = "2021-05-07 08:00:00"
            endDate = "2052-10-10 08:00:00"
        }
        val actualData = "true"

        val result = checkDateSearchAnimationUseCase.execute(searchAnimationTime)

        Assertions.assertEquals(actualData, result.toString())
    }

    @Test
    fun checkDateSearchAnimationUseCaseWithStartDateAndEndDateAndInvalid() {
        val searchAnimationTime = SearchAnimationTime().apply {
            startDate = "2021-05-07 08:00:00"
            endDate = "2020-10-10 08:00:00"
        }
        val actualData = "false"

        val result = checkDateSearchAnimationUseCase.execute(searchAnimationTime)

        Assertions.assertEquals(actualData, result.toString())
    }

    @Test
    fun checkDateSearchAnimationUseCaseWithInvalidStartDateAndValidEndDate() {
        val searchAnimationTime = SearchAnimationTime().apply {
            startDate = "2022-05-07 08:00:00"
            endDate = "2021-10-10 08:00:00"
        }
        val actualData = "false"

        val result = checkDateSearchAnimationUseCase.execute(searchAnimationTime)

        Assertions.assertEquals(actualData, result.toString())
    }

    @Test
    fun checkDateSearchAnimationUseCaseWithInvalidStartDateAndEndDate() {
        val searchAnimationTime = SearchAnimationTime().apply {
            startDate = "2022-05-07 08:00:00"
            endDate = "2020-10-10 08:00:00"
        }
        val actualData = "false"

        val result = checkDateSearchAnimationUseCase.execute(searchAnimationTime)

        Assertions.assertEquals(actualData, result.toString())
    }

    @Test
    fun checkDateSearchAnimationUseCaseWithSameStartDateAndEndDate() {
        val searchAnimationTime = SearchAnimationTime().apply {
            startDate = "2021-08-10 08:00:00"
            endDate = "2021-08-10 08:00:00"
        }
        val actualData = "false"

        val result = checkDateSearchAnimationUseCase.execute(searchAnimationTime)

        Assertions.assertEquals(actualData, result.toString())
    }

    @Test
    fun checkDateSearchAnimationUseCaseWithStartDateButNotHaveEndDate() {
        val searchAnimationTime = SearchAnimationTime().apply {
            startDate = "2021-05-07 08:00:00"
            endDate = ""
        }
        val actualData = "false"

        val result = checkDateSearchAnimationUseCase.execute(searchAnimationTime)

        Assertions.assertEquals(actualData, result.toString())
    }

    @Test
    fun checkDateSearchAnimationUseCaseWithoutStartDateButHaveEndDate() {
        val searchAnimationTime = SearchAnimationTime().apply {
            startDate = ""
            endDate = "2021-09-07 08:00:00"
        }
        val actualData = "false"

        val result = checkDateSearchAnimationUseCase.execute(searchAnimationTime)

        Assertions.assertEquals(actualData, result.toString())
    }

    @Test
    fun checkDateSearchAnimationUseCaseWithoutStartDateAndEndDate() {
        val searchAnimationTime = SearchAnimationTime().apply {
            startDate = ""
            endDate = ""
        }
        val actualData = "false"

        val result = checkDateSearchAnimationUseCase.execute(searchAnimationTime)

        Assertions.assertEquals(actualData, result.toString())
    }

    @Throws(ParseException::class)
    @Test
    fun checkDateSearchAnimationUseCaseWithParseException() {
        val searchAnimationTime = SearchAnimationTime().apply {
            startDate = "2021-05-07 08:00:00"
            endDate = "2021-09-07 08:00:00"
        }
        val actualData = "false"

        val result = checkDateSearchAnimationUseCase.execute(searchAnimationTime)

        Assertions.assertEquals(actualData, result.toString())
    }
}
