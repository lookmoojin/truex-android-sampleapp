package com.truedigital.common.share.currentdate.data

import com.truedigital.common.share.currentdate.ServerDateTimeModel
import com.truedigital.common.share.currentdate.repository.DateTimeRepository
import com.truedigital.common.share.currentdate.repository.DateTimeRepositoryImpl
import com.truedigital.common.share.currentdate.repository.dmp.DmpDateTimeInterface
import com.truedigital.common.share.currentdate.repository.nondmp.NonDmpCurrentDateTimeInterface
import com.truedigital.core.extensions.collectSafe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.apache.commons.net.ntp.NTPUDPClient
import org.apache.commons.net.ntp.NtpV3Packet
import org.apache.commons.net.ntp.TimeInfo
import org.apache.commons.net.ntp.TimeStamp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import java.net.InetAddress
import java.util.Calendar
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

interface DateTimeRepositoryTestCase {
    fun `Given successful When getFirebaseDateTime Then return date time`()
    fun `Given successful body null When getFirebaseDateTime Then return 0L`()
    fun `Given isNotSuccessful When getFirebaseDateTime Then throw error`()
    fun `Given successful When getGoogleDateTime Then return date time`()
    fun `Given successful When getGoogleDateTime is null Then return default time stamp`()
    fun `Given successful message null When getFirebaseDateTime Then return 0L`()
    fun `Given isNotSuccessful When getGoogleDateTime Then throw error`()
    fun `Given body null When getServerDateTime Then 0L`()
    fun `Given isNotSuccessful When getServerDateTime Then assert error`()
    fun `Given isSuccessful When getServerDateTimeThen date`()
    fun `Given isSuccessful When get calendarTime Then time`()
    fun `Given isSuccessful When get calendarTime Then time in millis`()
}

class DateTimeRepositoryTest : DateTimeRepositoryTestCase {
    private val timeClient: NTPUDPClient = mockk()
    private val dmpAPIInterface: DmpDateTimeInterface = mockk()
    private val nonDmpAPIInterface: NonDmpCurrentDateTimeInterface = mockk()
    private lateinit var dateTimeRepository: DateTimeRepository

    @BeforeEach
    fun setup() {
        dateTimeRepository =
            DateTimeRepositoryImpl(timeClient, dmpAPIInterface, nonDmpAPIInterface)
    }

    @Test
    override fun `Given successful When getFirebaseDateTime Then return date time`() = runTest {
        val response = Response.success(ServerDateTimeModel(10L))
        coEvery { nonDmpAPIInterface.getCurrentDateTime() } returns response

        dateTimeRepository.getFirebaseDateTime()
            .collectSafe { date ->
                assertEquals(10L, date)
            }
    }

    @Test
    override fun `Given successful body null When getFirebaseDateTime Then return 0L`() = runTest {
        val response = Response.success<ServerDateTimeModel>(null)
        coEvery { nonDmpAPIInterface.getCurrentDateTime() } returns response

        dateTimeRepository.getFirebaseDateTime()
            .collectSafe { date ->
                assertEquals(0L, date)
            }
    }

    @Test
    override fun `Given isNotSuccessful When getFirebaseDateTime Then throw error`() = runTest {
        val response = Response.error<ServerDateTimeModel>(404, "{}".toResponseBody())
        coEvery { nonDmpAPIInterface.getCurrentDateTime() } returns response

        dateTimeRepository.getFirebaseDateTime()
            .catch { error ->
                assertNotNull(error)
            }
            .collect()
    }

    @Test
    override fun `Given successful When getGoogleDateTime Then return date time`() = runTest {
        // arrange
        val timeInfo = mockk<TimeInfo>()
        val ntpV3Packet = mockk<NtpV3Packet>()
        val timeStamp = mockk<TimeStamp>()
        mockkStatic(InetAddress::class)
        every { InetAddress.getByName(any()) } returns mockk()
        every { timeClient.getTime(any()) } returns timeInfo
        every { timeInfo.message } returns ntpV3Packet
        every { ntpV3Packet.transmitTimeStamp } returns timeStamp
        every { timeStamp.time } returns 1678269856L

        // act
        dateTimeRepository.getGoogleDateTime()
            .collectSafe { timestamp ->
                assertEquals(1678269856L, timestamp)
            }
    }

    @Test
    override fun `Given successful When getGoogleDateTime is null Then return default time stamp`() =
        runTest {
            // arrange
            val timeInfo = null
            val ntpV3Packet = mockk<NtpV3Packet>()
            val timeStamp = mockk<TimeStamp>()
            mockkStatic(InetAddress::class)
            every { InetAddress.getByName(any()) } returns mockk()
            every { timeClient.getTime(any()) } returns timeInfo
            every { ntpV3Packet.transmitTimeStamp } returns timeStamp
            every { timeStamp.time } returns 1678269856L

            // act
            dateTimeRepository.getGoogleDateTime()
                .collectSafe { timestamp ->
                    assertEquals(0L, timestamp)
                }
        }

    override fun `Given successful message null When getFirebaseDateTime Then return 0L`() =
        runTest {
            // arrange
            val timeInfo = mockk<TimeInfo>()
            val ntpV3Packet = mockk<NtpV3Packet>()
            mockkStatic(InetAddress::class)
            every { InetAddress.getByName(any()) } returns mockk()
            every { timeClient.getTime(any()) } returns timeInfo
            every { timeInfo.message } returns ntpV3Packet
            every { ntpV3Packet.transmitTimeStamp } returns null

            // assert
            dateTimeRepository.getGoogleDateTime()
                .collectSafe {
                    assertEquals(0, it)
                }
        }

    override fun `Given isNotSuccessful When getGoogleDateTime Then throw error`() = runTest {
        // arrange
        val timeInfo = mockk<TimeInfo>()
        mockkStatic(InetAddress::class)
        every { InetAddress.getByName(any()) } returns mockk()
        every { timeClient.getTime(any()) } returns timeInfo
        every { timeInfo.message } returns null

        // act
        dateTimeRepository.getGoogleDateTime()
            .collectSafe {
                assertEquals(0, it)
            }
    }

    @Test
    override fun `Given isSuccessful When getServerDateTimeThen date`() = runTest {
        val response = Response.success(ServerDateTimeModel(10L))
        coEvery { dmpAPIInterface.getServerDateTime() } returns response
        dateTimeRepository.getServerDateTime()
            .collectSafe { date ->
                assertEquals(10L, date)
            }
    }

    @Test
    override fun `Given body null When getServerDateTime Then 0L`() = runTest {
        val response = Response.success<ServerDateTimeModel>(null)
        coEvery { dmpAPIInterface.getServerDateTime() } returns response
        dateTimeRepository.getServerDateTime()
            .collectSafe { date ->
                assertEquals(0L, date)
            }
    }

    @Test
    override fun `Given isNotSuccessful When getServerDateTime Then assert error`() = runTest {
        val response = Response.error<ServerDateTimeModel>(404, "{}".toResponseBody())
        coEvery { dmpAPIInterface.getServerDateTime() } returns response
        dateTimeRepository.getServerDateTime()
            .catch { error ->
                assertNotNull(error)
            }
            .collect()
    }

    @Test
    override fun `Given isSuccessful When get calendarTime Then time`() = runTest {

        val mocCalendar = mockk<Calendar>()
        val mockDate = mockk<Date>()
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns mocCalendar
        every { mocCalendar.time } returns mockDate
        every { mockDate.time } returns 1678184786000

        val date = dateTimeRepository.getLocalDate()

        assertEquals(date.time, 1678184786000)
    }

    @Test
    override fun `Given isSuccessful When get calendarTime Then time in millis`() = runTest {

        val mocCalendar = mockk<Calendar>()
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns mocCalendar
        every { mocCalendar.timeInMillis } returns 1678184786000
        val timestamp = dateTimeRepository.getLocalDateTime()

        assertEquals(timestamp, 1678184786000)
    }
}
