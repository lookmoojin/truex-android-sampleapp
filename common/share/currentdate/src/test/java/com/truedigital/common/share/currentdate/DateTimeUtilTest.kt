package com.truedigital.common.share.currentdate

import com.truedigital.core.constant.DateFormatConstant.E_d_MMM_yyyy
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.device.repository.LocalizationRepositoryImpl
import com.truedigital.core.domain.usecase.GetLocalizationUseCaseImpl
import com.truedigital.core.utils.SharedPrefsUtils
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.spyk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

interface DateTimeUtilTestCase {
    fun `Test getDate`()
    fun `Given currentTimeInMills not null When calculateRemainTime Then 10`()
    fun `Given currentTimeInMills null When calculateRemainTime Then 10`()
    fun `Given parse success When convertToMilliSeconds Then 10`()
    fun `Given parse null When convertToMilliSeconds Then 0`()
    fun `Given parse error When convertToMilliSeconds Then 0`()
    fun `Given parse success When convertToMilliSecondsForCountdown`()
    fun `Given parse null When convertToMilliSecondsForCountdown`()
    fun `Given parse error When convertToMilliSecondsForCountdown`()
    fun `Given EN When toShortDate Then EN`()
    fun `Given TH with dateFormat When toShortDate Then TH`()
    fun `Given EN with dateFormat When toShortDate Then EN`()
    fun `Test getCurrentDateTime`()
    fun `Test getCurrentDateInMillis`()
    fun `Given dateString not null When isToday Then false`()
    fun `Given dateString null When isToday Then false`()
    fun `Test isToday`()
    fun `Test isTomorrow`()
    fun `Test isYesterday`()
    fun `Test getYesterday with locale`()
    fun `Test getFutureFromToday`()
    fun `Test getPastFromMinute`()
    fun `Given matches When getTimeInLong Then matches`()
    fun `Given not matches When getTimeInLong Then not matches`()
    fun `Given appLanguageCode TH When convertTimeMillisToDate`()
    fun `Given appLanguageCode other When convertTimeMillisToDate`()
    fun `Given appLanguageCode TH When convertTimeMillisToBuddhistYear`()
    fun `Given appLanguageCode asean When convertTimeMillisToBuddhistYear`()
    fun `Given appLanguageCode other When convertTimeMillisToBuddhistYear`()
    fun `Test convertTimeMillisToDateStrIgnoreBuddhistYear`()
    fun `Given locale th When convertTimeFormatForNewCMS`()
    fun `Given locale other When convertTimeFormatForNewCMS`()
    fun `Given empty dateTime When convertTimeFormatForNewCMS`()
    fun `Test convertTimeFormatForNewCMS thrown ParseException`()
    fun `Test convertTimeFormatForNewCMS thrown Exception`()
    fun `Given locale th when getFutureFromToday `()
    fun `Given TH with dateFormat When toShortDate milliSeconds`()
    fun `Test convertTimeFormatForNewCMS thrown IllegalArgumentException`()
    fun `Test isLastTimeAfterCurrentTime plus 10 day`()
}

class DateTimeUtilTest : DateTimeUtilTestCase {

    private lateinit var sharedPrefsUtils: SharedPrefsUtils
    private lateinit var localizationRepository: LocalizationRepository
    private lateinit var getLocalizationUseCase: GetLocalizationUseCaseImpl
    private lateinit var dateTimeUtil: DateTimeUtil
    private var dateTimeUtilController: DateTimeUtilController = mockk()

    @BeforeEach
    fun setup() {
        dateTimeUtil = spyk(DateTimeUtil(dateTimeUtilController))
        sharedPrefsUtils = spyk(SharedPrefsUtils(mockk()))
        localizationRepository = spyk(LocalizationRepositoryImpl(sharedPrefsUtils))
        getLocalizationUseCase = spyk(GetLocalizationUseCaseImpl(localizationRepository))
    }

    @AfterEach
    fun tearDown() {
        clearMocks(localizationRepository)
    }

    @Test
    override fun `Test getDate`() {
        every { dateTimeUtilController.getDate() } returns Date()
        val actual = dateTimeUtil.getDate()
        assertNotNull(actual)
    }

    @Test
    override fun `Given currentTimeInMills not null When calculateRemainTime Then 10`() {
        every { dateTimeUtilController.calculateRemainTime(any(), any()) } returns 10
        val actual = dateTimeUtil.calculateRemainTime(20, 10)
        assertEquals(10, actual)
    }

    @Test
    override fun `Given currentTimeInMills null When calculateRemainTime Then 10`() {
        every { dateTimeUtilController.calculateRemainTime(any()) } returns 10
        mockkStatic(Calendar::class)
        every { Calendar.getInstance(TimeZone.getDefault()).timeInMillis } returns 10
        val actual = dateTimeUtil.calculateRemainTime(20, null)
        assertEquals(10, actual)
    }

    @Test
    override fun `Given parse success When convertToMilliSeconds Then 10`() {
        // dateTime 2022-04-04T05:00:00.000Z 1649048400000
        every { dateTimeUtilController.convertToMilliSeconds(any(), any()) } returns 1649048400000
        val actual =
            dateTimeUtil.convertToMilliSeconds("2022-04-04T05:00:00.000Z", Locale.getDefault())
        assertEquals(1649048400000, actual)
    }

    @Test
    override fun `Given parse null When convertToMilliSeconds Then 0`() {
        every { dateTimeUtilController.convertToMilliSeconds(any(), any()) } returns 0L
        mockkConstructor(SimpleDateFormat::class)
        every { anyConstructed<SimpleDateFormat>().parse(any()) } returns null
        val actual =
            dateTimeUtil.convertToMilliSeconds("2022-04-04T05:00:00.000Z", Locale.getDefault())
        assertEquals(0L, actual)
    }

    @Test
    override fun `Given parse error When convertToMilliSeconds Then 0`() {
        every { dateTimeUtilController.convertToMilliSeconds(any(), any()) } returns 0L
        mockkConstructor(SimpleDateFormat::class)
        every { anyConstructed<SimpleDateFormat>().parse(any()) } throws ParseException("test", 1)
        val actual =
            dateTimeUtil.convertToMilliSeconds("2022-04-04T05:00:00.000Z", Locale.getDefault())
        assertEquals(0L, actual)
    }

    @Test
    override fun `Given parse success When convertToMilliSecondsForCountdown`() {
        // dateTime 2022-04-04T05:00:00Z 1649048400
        every {
            dateTimeUtilController.convertToMilliSecondsForCountDown(
                any(),
                any(),
            )
        } returns 1649048400000
        val actual =
            dateTimeUtil.convertToMilliSecondsForCountDown(
                "2022-04-04T05:00:00Z",
                Locale.getDefault(),
            )
        assertEquals(1649048400000, actual)
    }

    @Test
    override fun `Given parse null When convertToMilliSecondsForCountdown`() {
        every { dateTimeUtilController.convertToMilliSecondsForCountDown(any(), any()) } returns 0L
        mockkConstructor(SimpleDateFormat::class)
        every { anyConstructed<SimpleDateFormat>().parse(any()) } returns null
        val actual =
            dateTimeUtil.convertToMilliSecondsForCountDown(
                "2022-04-04T05:00:00Z",
                Locale.getDefault(),
            )
        assertEquals(0L, actual)
    }

    @Test
    override fun `Given parse error When convertToMilliSecondsForCountdown`() {
        every { dateTimeUtilController.convertToMilliSecondsForCountDown(any(), any()) } returns 0L
        mockkConstructor(SimpleDateFormat::class)
        every { anyConstructed<SimpleDateFormat>().parse(any()) } throws ParseException("test", 1)
        val actual =
            dateTimeUtil.convertToMilliSecondsForCountDown(
                "2022-04-04T05:00:00Z",
                Locale.getDefault(),
            )
        assertEquals(0L, actual)
    }

    @Test
    override fun `Given EN When toShortDate Then EN`() {
        // time 1649098800000 Tue 5 Apr 2022
        every { dateTimeUtilController.toShortDate(any()) } returns "Tue 5 Apr 2022"
        every { localizationRepository.getAppLocale() } returns Locale("en", "EN")
        every { localizationRepository.getAppLocalization() } returns LocalizationRepository.Localization.EN
        val actual = dateTimeUtil.toShortDate(1649098800000)
        assertEquals("Tue 5 Apr 2022", actual)
    }

    @Test
    override fun `Given TH with dateFormat When toShortDate milliSeconds`() {
        every { dateTimeUtilController.toShortDate(any()) } returns "อ. 5 เม.ย. 3108"
        every { localizationRepository.getAppLocale() } returns Locale("th", "TH")
        every { localizationRepository.getAppLocalization() } returns LocalizationRepository.Localization.TH
        val actual = dateTimeUtil.toShortDate(1649098800000)

        assertEquals("อ. 5 เม.ย. 3108", actual)
    }

    @Test
    override fun `Given TH with dateFormat When toShortDate Then TH`() {
        // time 1649098800000 อ. 5 เม.ย. 2565
        every { dateTimeUtilController.toShortDate(any(), any()) } returns "อ. 5 เม.ย. 2565"
        val actual = dateTimeUtil.toShortDate(1649098800000, Locale("th", "EN"))
        assertEquals("อ. 5 เม.ย. 2565", actual)
    }

    @Test
    override fun `Given EN with dateFormat When toShortDate Then EN`() {
        // time 1649098800000 Tue 5 Apr 2022
        every { dateTimeUtilController.toShortDate(any(), any()) } returns "Tue 5 Apr 2022"
        val actual = dateTimeUtil.toShortDate(1649098800000, Locale("en", "EN"))
        assertEquals("Tue 5 Apr 2022", actual)
    }

    @Test
    override fun `Test getCurrentDateTime`() {
        every { dateTimeUtilController.getCurrentDateTime(any()) } returns "Tue 5 Apr 2022"
        mockkStatic(Calendar::class)
        every { Calendar.getInstance(TimeZone.getDefault()).timeInMillis } returns 1649098800000
        val actual = dateTimeUtil.getCurrentDateTime(E_d_MMM_yyyy)
        assertEquals("Tue 5 Apr 2022", actual)
    }

    @Test
    override fun `Test getCurrentDateInMillis`() {
        every { dateTimeUtilController.getCurrentDateInMillis() } returns 1649098800000
        mockkStatic(Calendar::class)
        every { Calendar.getInstance(TimeZone.getDefault()).timeInMillis } returns 1649098800000
        val actual = dateTimeUtil.getCurrentDateInMillis()
        assertEquals(1649098800000, actual)
    }

    @Test
    override fun `Given dateString not null When isToday Then false`() {
        every { dateTimeUtilController.isToday(any(), any()) } returns false
        val now = System.currentTimeMillis()
        val sdf = SimpleDateFormat(E_d_MMM_yyyy, Locale.getDefault())
        val format = sdf.format(Date(now))
        val actual = dateTimeUtil.isToday(format, E_d_MMM_yyyy)
        assertFalse(actual)
    }

    @Test
    override fun `Given dateString null When isToday Then false`() {
        every { dateTimeUtilController.isToday(dateString = any()) } returns false
        val actual = dateTimeUtil.isToday(null)
        assertFalse(actual)
    }

    @Test
    override fun `Test isToday`() {
        every { dateTimeUtilController.isToday(timeInMills = any()) } returns false
        val now = System.currentTimeMillis()
        assertFalse(dateTimeUtil.isToday(now))
    }

    @Test
    override fun `Test isTomorrow`() {
        every { dateTimeUtilController.isTomorrow(timeInMills = any()) } returns false
        val now = System.currentTimeMillis()
        assertFalse(dateTimeUtil.isTomorrow(now))
    }

    @Test
    override fun `Test isYesterday`() {
        every { dateTimeUtilController.isYesterday(timeInMills = any()) } returns false
        val now = System.currentTimeMillis()
        assertFalse(dateTimeUtil.isYesterday(now))
    }

    @Test
    override fun `Test getYesterday with locale`() {
        every { dateTimeUtilController.getYesterday(any(), any()) } returns "จ. 4 เม.ย. 2565"
        mockkStatic(Calendar::class)
        every { Calendar.getInstance(TimeZone.getDefault()).timeInMillis } returns 1649098800000
        val actual = dateTimeUtil.getYesterday(E_d_MMM_yyyy, Locale("th", "TH"))
        assertEquals("จ. 4 เม.ย. 2565", actual)
    }

    @Test
    override fun `Test getFutureFromToday`() {
        every { dateTimeUtilController.getFutureFromToday(any(), any()) } returns "2022-04-04"
        mockkStatic(Calendar::class)
        every { Calendar.getInstance(TimeZone.getDefault()).timeInMillis } returns 1649098800000
        every { Calendar.getInstance(TimeZone.getDefault()).get(any()) } returns 95
        justRun { Calendar.getInstance(TimeZone.getDefault()).set(any(), any()) }
        val actual = dateTimeUtil.getFutureFromToday(1)
        assertEquals("2022-04-04", actual)
    }

    @Test
    override fun `Given locale th when getFutureFromToday `() {
        every { dateTimeUtilController.getFutureFromToday(any(), any()) } returns "2565-04-04"
        mockkStatic(Calendar::class)
        every { Calendar.getInstance(TimeZone.getDefault()).timeInMillis } returns 1649098800000
        every { Calendar.getInstance(TimeZone.getDefault()).get(any()) } returns 95
        every { localizationRepository.getAppLanguageCode() } returns "th"
        justRun { Calendar.getInstance(TimeZone.getDefault()).set(any(), any()) }
        val actual = dateTimeUtil.getFutureFromToday(1)
        assertEquals("2565-04-04", actual)
    }

    @Test
    override fun `Test getPastFromMinute`() {
        every { dateTimeUtilController.getPastFromMinute(any()) } returns "2022-04-04 02:00:00"
        mockkStatic(Calendar::class)
        every { Calendar.getInstance(TimeZone.getDefault()).timeInMillis } returns 1649098800000
        every { Calendar.getInstance(TimeZone.getDefault()).get(any()) } returns 95
        justRun { Calendar.getInstance(TimeZone.getDefault()).set(any(), any()) }
        val actual = dateTimeUtil.getPastFromMinute(60)
        assertEquals("2022-04-04 02:00:00", actual)
    }

    @Test
    override fun `Given matches When getTimeInLong Then matches`() {
        every { dateTimeUtilController.getTimeInLong(any()) } returns 29410000
        val actual = dateTimeUtil.getTimeInLong("08:10:10")
        assertEquals(29410000, actual)
    }

    @Test
    override fun `Given not matches When getTimeInLong Then not matches`() {
        every { dateTimeUtilController.getTimeInLong(any()) } returns -1
        val actual = dateTimeUtil.getTimeInLong("one:two:three")
        assertEquals(-1, actual)
    }

    @Test
    override fun `Given appLanguageCode TH When convertTimeMillisToDate`() {
        every {
            dateTimeUtilController.convertTimeMillisToDate(
                any(),
                any(),
            )
        } returns "อา. 1 ม.ค. 3056"
        every { localizationRepository.getAppLanguageCode() } returns "th"
        val actual = dateTimeUtil.convertTimeMillisToDate(0L, E_d_MMM_yyyy)
        assertEquals("อา. 1 ม.ค. 3056", actual)
    }

    @Test
    override fun `Given appLanguageCode other When convertTimeMillisToDate`() {
        every {
            dateTimeUtilController.convertTimeMillisToDate(
                any(),
                any(),
            )
        } returns "Thu 1 Jan 1970"
        every { localizationRepository.getAppLanguageCode() } returns "en"
        val actual = dateTimeUtil.convertTimeMillisToDate(0L, E_d_MMM_yyyy)
        assertEquals("Thu 1 Jan 1970", actual)
    }

    @Test
    override fun `Given appLanguageCode TH When convertTimeMillisToBuddhistYear`() {
        every {
            dateTimeUtilController.convertTimeMillisToBuddhistYear(
                any(),
                any(),
            )
        } returns "อา. 1 ม.ค. 3056"
        every { localizationRepository.getAppLanguageCode() } returns "th"
        val actual = dateTimeUtil.convertTimeMillisToBuddhistYear(0L, E_d_MMM_yyyy)
        assertEquals("อา. 1 ม.ค. 3056", actual)
    }

    @Test
    override fun `Given appLanguageCode asean When convertTimeMillisToBuddhistYear`() {
        every {
            dateTimeUtilController.convertTimeMillisToBuddhistYear(
                any(),
                any(),
            )
        } returns "1/1/70"
        every { localizationRepository.getAppLanguageCode() } returns "id"
        val actual = dateTimeUtil.convertTimeMillisToBuddhistYear(0L, E_d_MMM_yyyy)
        assertEquals("1/1/70", actual)
    }

    @Test
    override fun `Given appLanguageCode other When convertTimeMillisToBuddhistYear`() {
        every {
            dateTimeUtilController.convertTimeMillisToBuddhistYear(
                any(),
                any(),
            )
        } returns "Thu 1 Jan 1970"
        every { localizationRepository.getAppLanguageCode() } returns "en"
        val actual = dateTimeUtil.convertTimeMillisToBuddhistYear(0L, E_d_MMM_yyyy)
        assertEquals("Thu 1 Jan 1970", actual)
    }

    @Test
    override fun `Test convertTimeMillisToDateStrIgnoreBuddhistYear`() {
        every {
            dateTimeUtilController.convertTimeMillisToDateStrIgnoreBuddhistYear(
                any(),
                any(),
                any(),
            )
        } returns "Fri 2 Jan 1970"
        val actual = dateTimeUtil.convertTimeMillisToDateStrIgnoreBuddhistYear(0L, E_d_MMM_yyyy, 1)
        assertEquals("Fri 2 Jan 1970", actual)
    }

    @Test
    override fun `Given locale th When convertTimeFormatForNewCMS`() {
        every { dateTimeUtilController.convertTimeFormatForNewCMS(any()) } returns "04 เม.ย. 2565 "
        every { localizationRepository.getAppLocale() } returns Locale("th", "TH")
        every { localizationRepository.getAppLocalization() } returns LocalizationRepository.Localization.TH
        val actual = dateTimeUtil.convertTimeFormatForNewCMS("2022-04-04T02:00:00")
        assertEquals("04 เม.ย. 2565 ", actual)
    }

    @Test
    override fun `Given locale other When convertTimeFormatForNewCMS`() {
        every { dateTimeUtilController.convertTimeFormatForNewCMS(any()) } returns "04 Apr 2022"
        val actual = dateTimeUtil.convertTimeFormatForNewCMS("2022-04-04T02:00:00")
        assertEquals("04 Apr 2022", actual)
    }

    @Test
    override fun `Given empty dateTime When convertTimeFormatForNewCMS`() {
        every { dateTimeUtilController.convertTimeFormatForNewCMS(any()) } returns ""
        val actual = dateTimeUtil.convertTimeFormatForNewCMS("")
        assertEquals("", actual)
    }

    @Throws(ParseException::class)
    @Test
    override fun `Test convertTimeFormatForNewCMS thrown ParseException`() {
        every { dateTimeUtilController.convertTimeFormatForNewCMS(any()) } returns ""
        val actual = dateTimeUtil.convertTimeFormatForNewCMS("test")
        assertEquals("", actual)
    }

    @Throws(Exception::class)
    @Test
    override fun `Test convertTimeFormatForNewCMS thrown Exception`() {
        every { dateTimeUtilController.convertTimeFormatForNewCMS(any()) } returns ""
        mockkStatic(Calendar::class)
        every { Calendar.getInstance().time } returns null
        val actual = dateTimeUtil.convertTimeFormatForNewCMS("2022-04-04T02:00:00")
        assertEquals("", actual)
    }

    @Throws(IllegalArgumentException::class)
    @Test
    override fun `Test convertTimeFormatForNewCMS thrown IllegalArgumentException`() {
        every { dateTimeUtilController.convertTimeFormatForNewCMS(any()) } returns ""
        mockkConstructor(SimpleDateFormat::class)
        every { anyConstructed<SimpleDateFormat>().parse(eq("test")) } throws IllegalArgumentException(
            "test",
        )
        val actual = dateTimeUtil.convertTimeFormatForNewCMS("test")
        assertEquals("", actual)
    }

    @Test
    override fun `Test isLastTimeAfterCurrentTime plus 10 day`() {
        every {
            dateTimeUtilController.isLastTimeAfterCurrentTime(any(), any(), any())
        } returns true

        val actual = dateTimeUtil.isLastTimeAfterCurrentTime(
            1675874624000,
            1675702747093,
            10,
        )
        assertEquals(true, actual)
    }
}
