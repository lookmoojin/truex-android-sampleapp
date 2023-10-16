package com.truedigital.common.share.currentdate.domain

import android.content.Context
import android.provider.Settings
import com.truedigital.common.share.currentdate.repository.DateTimeRepository
import com.truedigital.common.share.currentdate.usecase.GetCurrentDateTimeUseCase
import com.truedigital.common.share.currentdate.usecase.GetCurrentDateTimeUseCaseImpl
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.provider.ContextDataProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface GetCurrentDateTimeUseCaseTestCase {
    fun `Given successful When get current time Then return date time`()
    fun `Given successful When get google datetime Then return date time`()
    fun `Given successful When get server datetime Then return date time`()
    fun `Given successful When get firebase datetime Then return date time`()
    fun `Given successful When get firebase datetime error Then return local date time`()
    fun `Given successful When get server datetime less 0 Then return firebase date time`()
    fun `Given error When get current time Then return throw`()
    fun `Given error When get firebase datetime error and not require local Then return throw`()
}

class GetCurrentDateTimeUseCaseTest : GetCurrentDateTimeUseCaseTestCase {
    private val contextDataProvider: ContextDataProvider = mockk()
    private val dateTimeRepository: DateTimeRepository = mockk()
    private lateinit var getCurrentDateTimeUseCase: GetCurrentDateTimeUseCase

    @BeforeEach
    fun setup() {
        getCurrentDateTimeUseCase =
            GetCurrentDateTimeUseCaseImpl(contextDataProvider, dateTimeRepository)
    }

    @Test
    override fun `Given successful When get current time Then return date time`() = runTest {
        mockSettingGlobal(1)
        every { dateTimeRepository.getLocalDateTime() } returns 1633503379751L
        getCurrentDateTimeUseCase.execute()
            .collectSafe { result ->
                assertEquals(1633503379751L, result)
            }
    }

    @Test
    override fun `Given successful When get google datetime Then return date time`() =
        runTest {
            mockSettingGlobal(0)
            coEvery { dateTimeRepository.getGoogleDateTime() } returns flowOf(1633503379751L)

            val result = getCurrentDateTimeUseCase.execute()

            result.collectSafe {
                assertEquals(1633503379751L, it)
            }

            verify(exactly = 1) { dateTimeRepository.getGoogleDateTime() }
            verify(exactly = 0) { dateTimeRepository.getServerDateTime() }
            verify(exactly = 0) { dateTimeRepository.getFirebaseDateTime() }
        }

    @Test
    override fun `Given successful When get server datetime Then return date time`() = runTest {
        mockSettingGlobal(0)
        coEvery { dateTimeRepository.getGoogleDateTime() } returns flow { error("error_google") }
        coEvery { dateTimeRepository.getServerDateTime() } returns flow { emit(1633503379751L) }

        val result = getCurrentDateTimeUseCase.execute()

        result.collectSafe {
            assertEquals(1633503379751L, it)
        }
        verify(exactly = 1) { dateTimeRepository.getGoogleDateTime() }
        verify(exactly = 1) { dateTimeRepository.getServerDateTime() }
        verify(exactly = 0) { dateTimeRepository.getFirebaseDateTime() }
    }

    @Test
    override fun `Given successful When get firebase datetime Then return date time`() = runTest {
        mockSettingGlobal(0)
        every { dateTimeRepository.getGoogleDateTime() } returns flow { error("error_google") }
        every { dateTimeRepository.getServerDateTime() } returns flow { error("error_server") }
        every { dateTimeRepository.getFirebaseDateTime() } returns flowOf(1633503379751L)

        getCurrentDateTimeUseCase.execute()
            .collectSafe { result ->
                assertEquals(1633503379751L, result)
            }
        verify(exactly = 1) { dateTimeRepository.getGoogleDateTime() }
        verify(exactly = 1) { dateTimeRepository.getServerDateTime() }
        verify(exactly = 1) { dateTimeRepository.getFirebaseDateTime() }
    }

    @Test
    override fun `Given successful When get firebase datetime error Then return local date time`() =
        runTest {
            mockSettingGlobal(0)
            coEvery { dateTimeRepository.getGoogleDateTime() } returns flow { error("error_google") }
            coEvery { dateTimeRepository.getServerDateTime() } returns flow { error("error_server") }
            coEvery { dateTimeRepository.getFirebaseDateTime() } returns flow { error("error_firebase") }
            every { dateTimeRepository.getLocalDateTime() } returns 1678259405L

            getCurrentDateTimeUseCase.execute()
                .collectSafe { result ->
                    assertEquals(1678259405L, result)
                }
            verify(exactly = 1) { dateTimeRepository.getGoogleDateTime() }
            verify(exactly = 1) { dateTimeRepository.getServerDateTime() }
            verify(exactly = 1) { dateTimeRepository.getFirebaseDateTime() }
            verify(exactly = 1) { dateTimeRepository.getLocalDateTime() }
        }

    @Test
    override fun `Given successful When get server datetime less 0 Then return firebase date time`() =
        runTest {
            mockSettingGlobal(0)
            coEvery { dateTimeRepository.getGoogleDateTime() } returns flow { error("error_google") }
            coEvery { dateTimeRepository.getServerDateTime() } returns flowOf(-1L)
            coEvery { dateTimeRepository.getFirebaseDateTime() } returns flowOf(1678259405L)

            getCurrentDateTimeUseCase.execute()
                .collectSafe { result ->
                    assertEquals(1678259405L, result)
                }
            verify(exactly = 1) { dateTimeRepository.getGoogleDateTime() }
            verify(exactly = 1) { dateTimeRepository.getServerDateTime() }
            verify(exactly = 1) { dateTimeRepository.getFirebaseDateTime() }
            verify(exactly = 0) { dateTimeRepository.getLocalDateTime() }
        }

    @Test
    override fun `Given error When get current time Then return throw`() = runTest {
        mockSettingGlobal(1)
        every { dateTimeRepository.getLocalDateTime() }.throws(Throwable("error_current"))

        val result = runCatching { getCurrentDateTimeUseCase.execute().collect() }
        val exception = result.exceptionOrNull()

        assertTrue(result.isFailure)
        assertTrue { exception is Throwable }
        if (exception is Throwable) {
            assertEquals("error_current", exception.message)
        }
    }

    @Test
    override fun `Given error When get firebase datetime error and not require local Then return throw`() =
        runTest {
            mockSettingGlobal(0)
            coEvery { dateTimeRepository.getGoogleDateTime() } returns flow { error("error_google") }
            coEvery { dateTimeRepository.getServerDateTime() } returns flow { error("error_server") }
            coEvery { dateTimeRepository.getFirebaseDateTime() } returns flow { error("error_firebase") }
            every { dateTimeRepository.getLocalDateTime() } returns 1678259405L

            val result = runCatching { getCurrentDateTimeUseCase.execute(false).collect() }
            val exception = result.exceptionOrNull()

            assertTrue(result.isFailure)
            assertTrue { exception is IllegalStateException }
            if (exception is IllegalStateException) {
                val expectationException = IllegalStateException("error_firebase").toString()
                assertEquals(expectationException, exception.cause?.message)
            }

            verify(exactly = 1) { dateTimeRepository.getGoogleDateTime() }
            verify(exactly = 1) { dateTimeRepository.getServerDateTime() }
            verify(exactly = 1) { dateTimeRepository.getFirebaseDateTime() }
            verify(exactly = 0) { dateTimeRepository.getLocalDateTime() }
        }

    private fun mockSettingGlobal(returnValue: Int) {
        val context = mockk<Context>()
        every { contextDataProvider.getDataContext() } returns context
        every { context.contentResolver } returns mockk()
        mockkStatic(Settings.Global::class)
        every { Settings.Global.getInt(any(), any(), any()) } returns returnValue
    }
}
