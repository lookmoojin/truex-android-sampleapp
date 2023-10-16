package com.truedigital.common.share.currentdate.datetime.usecase

import android.content.Context
import android.content.res.Configuration
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.currentdate.DateTimeInterface
import com.truedigital.common.share.currentdate.model.TimeResourceUnitModel
import com.truedigital.common.share.currentdate.usecase.ConvertToDateFormatUseCase
import com.truedigital.common.share.currentdate.usecase.ConvertToDateFormatUseCaseImpl
import com.truedigital.core.constant.DateFormatConstant
import com.truedigital.core.data.device.repository.LocalizationRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ConvertToDateFormatUseCaseTest {
    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var localizationRepository: LocalizationRepository

    @MockK
    private lateinit var dateTimeUtil: DateTimeInterface

    @MockK
    private lateinit var mockConfiguration: Configuration

    private lateinit var convertToDateFormatUseCase: ConvertToDateFormatUseCase
    private lateinit var mockTimeResourceUnitModel: TimeResourceUnitModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        convertToDateFormatUseCase =
            ConvertToDateFormatUseCaseImpl(context, localizationRepository, dateTimeUtil)
        every { context.createConfigurationContext(any()) } returns context
        every { context.resources.configuration } returns mockConfiguration
        every { localizationRepository.getAppLocale() } returns Locale.ENGLISH
        every { localizationRepository.getAppLanguageCode() } returns "EN"
        mockTimeResourceUnitModel = TimeResourceUnitModel().apply {
            justNow = R.string.moment_ago
            yesterday = R.string.one_day_ago
        }
    }

    @Test
    fun convertToDateFormatUseCase_returnHour_success() {
        val mockDate = "2021-11-09T05:15:00.000Z"
        val mockDateFormat = DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z

        val expect = "1 hour ago"

        every { dateTimeUtil.convertToMilliSeconds(any(), any()) } returns 1636434900000
        every { context.resources.getString(any()) } returns "hour ago"

        val result =
            convertToDateFormatUseCase.execute(
                mockDate,
                mockDateFormat,
                mockTimeResourceUnitModel,
                Date(1636441200000)
            )

        assertEquals(expect, result)
    }

    @Test
    fun convertToDateFormatUseCase_returnHours_success() {
        val mockDate = "2021-11-09T05:00:00.000Z"
        val mockDateFormat = DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z

        val expect = "2 hours ago"

        every { dateTimeUtil.convertToMilliSeconds(any(), any()) } returns 1636434000000
        every { context.resources.getString(any()) } returns "hours ago"

        val result =
            convertToDateFormatUseCase.execute(
                mockDate,
                mockDateFormat,
                mockTimeResourceUnitModel,
                Date(1636441200000)
            )

        assertEquals(expect, result)
    }

    @Test
    fun convertToDateFormatUseCase_returnHoursIsEmpty_success() {
        val mockDate = "2021-11-09T15:00:00.000Z"
        val mockDateFormat = DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z

        every { dateTimeUtil.convertToMilliSeconds(any(), any()) } returns 1636444800000
        every { context.resources.getString(any()) } returns ""

        val result =
            convertToDateFormatUseCase.execute(
                mockDate,
                mockDateFormat,
                mockTimeResourceUnitModel,
                Date(1636441200000)
            )
        assertTrue(result.isEmpty())
        assertEquals("", result)
    }

    @Test
    fun convertToDateFormatUseCase_returnMinute_success() {
        val mockDate = "2021-11-09T06:59:00.000Z"
        val mockDateFormat = DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z

        val expect = "1 minute ago"

        every { dateTimeUtil.convertToMilliSeconds(any(), any()) } returns 1636441140000
        every { context.resources.getString(any()) } returns "minute ago"

        val result =
            convertToDateFormatUseCase.execute(
                mockDate,
                mockDateFormat,
                mockTimeResourceUnitModel,
                Date(1636441200000)
            )

        assertEquals(expect, result)
    }

    @Test
    fun convertToDateFormatUseCase_returnMinutes_success() {
        val mockDate = "2021-11-09T06:15:00.000Z"
        val mockDateFormat = DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z

        val expect = "45 minutes ago"

        every { dateTimeUtil.convertToMilliSeconds(any(), any()) } returns 1636438500000
        every { context.resources.getString(any()) } returns "minutes ago"

        val result =
            convertToDateFormatUseCase.execute(
                mockDate,
                mockDateFormat,
                mockTimeResourceUnitModel,
                Date(1636441200000)
            )

        assertEquals(expect, result)
    }

    @Test
    fun convertToDateFormatUseCase_returnMinutesIsEmpty_success() {
        val mockDate = "2021-11-09T14:15:00.000Z"
        val mockDateFormat = DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z

        every { dateTimeUtil.convertToMilliSeconds(any(), any()) } returns 1636442100000
        every { context.resources.getString(any()) } returns ""

        val result =
            convertToDateFormatUseCase.execute(
                mockDate,
                mockDateFormat,
                mockTimeResourceUnitModel,
                Date(1636441200000)
            )

        assertEquals("", result)
    }

    @Test
    fun convertToDateFormatUseCase_returnMinutesIsJustNow_success() {
        val mockDate = "2021-11-09T14:00:00.000Z"
        val mockDateFormat = DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z

        val expect = "Just now"

        every { dateTimeUtil.convertToMilliSeconds(any(), any()) } returns 1636441200000
        every { context.resources.getString(any()) } returns "Just now"

        val result =
            convertToDateFormatUseCase.execute(
                mockDate,
                mockDateFormat,
                mockTimeResourceUnitModel,
                Date(1636441200000)
            )

        assertEquals(expect, result)
    }

    @Test
    fun convertToDateFormatUseCase_returnDay_success() {
        val mockDate = "2021-11-09T14:00:00.000Z"
        val mockDateFormat = DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z

        val expect = "1 day ago"

        every { dateTimeUtil.convertToMilliSeconds(any(), any()) } returns 1636441200000
        every { context.resources.getString(any()) } returns "1 day ago"

        val result =
            convertToDateFormatUseCase.execute(
                mockDate,
                mockDateFormat,
                mockTimeResourceUnitModel,
                Date(1636528500000)
            )

        assertEquals(expect, result)
    }

    @Test
    fun convertToDateFormatUseCase_returnDays_success() {
        val mockDate = "2021-11-09T14:00:00.000Z"
        val mockDateFormat = DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z

        val expect = "2 days ago"

        every { dateTimeUtil.convertToMilliSeconds(any(), any()) } returns 1636441200000
        every { context.resources.getString(any()) } returns "days ago"

        val result =
            convertToDateFormatUseCase.execute(
                mockDate,
                mockDateFormat,
                mockTimeResourceUnitModel,
                Date(1636614900000)
            )

        assertEquals(expect, result)
    }

    @Test
    fun convertToDateFormatUseCase_returnDaysIsEmpty_success() {
        val mockDate = "2021-11-11T14:00:00.000Z"
        val mockDateFormat = DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z

        every { dateTimeUtil.convertToMilliSeconds(any(), any()) } returns 1636614900000
        every { context.resources.getString(any()) } returns ""

        val result =
            convertToDateFormatUseCase.execute(
                mockDate,
                mockDateFormat,
                mockTimeResourceUnitModel,
                Date(1636355700000)
            )

        assertEquals("", result)
    }

    @Test
    fun convertToDateFormatUseCase_returnDaysIsBuddhistYear_success() {
        val mockDate = "2021-11-08T14:00:00.000Z"
        val mockDateFormat = DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z

        val expect = "16/11/21"

        every { dateTimeUtil.convertToMilliSeconds(any(), any()) } returns 1636355700000
        every { dateTimeUtil.convertTimeMillisToBuddhistYear(any(), any()) } returns "16/11/21"

        val result =
            convertToDateFormatUseCase.execute(
                mockDate,
                mockDateFormat,
                mockTimeResourceUnitModel,
                Date(1637046900000)
            )

        assertEquals(expect, result)
    }

    @Test
    fun convertToDateFormatUseCase_dateMilliSecondsIsZero_returnError() {
        val mockDate = "2021-11-08T14:00:00.000Z"
        val mockDateFormat = DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z

        every { dateTimeUtil.convertToMilliSeconds(any(), any()) } returns 0
        every { dateTimeUtil.convertTimeMillisToBuddhistYear(any(), any()) } returns "16/11/21"

        val result = convertToDateFormatUseCase.execute(
            mockDate,
            mockDateFormat,
            mockTimeResourceUnitModel,
            Date(1637046900000)
        )
        assertEquals("", result)
    }
}
