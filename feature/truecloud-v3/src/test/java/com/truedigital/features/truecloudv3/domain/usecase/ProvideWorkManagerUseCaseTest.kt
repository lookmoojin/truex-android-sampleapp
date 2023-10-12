package com.truedigital.features.truecloudv3.domain.usecase

import android.app.Application
import androidx.work.WorkManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class ProvideWorkManagerUseCaseTest {
    private lateinit var provideWorkManagerUseCase: ProvideWorkManagerUseCase
    private val mockContext = mockk<Application>()

    @BeforeEach
    fun setup() {
        provideWorkManagerUseCase = ProvideWorkManagerUseCaseImpl(
            context = mockContext
        )
    }

    @Test
    fun getWorkManager() = runTest {
        // arrange
        val mockWorkManager = mockk<WorkManager>()
        mockkStatic(WorkManager::class)
        every { WorkManager.getInstance(mockContext) } returns mockWorkManager
        every {
            mockContext.getApplicationContext()
        } returns mockContext
        // act
        val result = provideWorkManagerUseCase.execute()

        // assert
        assertNotNull(result)
        verify { WorkManager.getInstance(mockContext) }
        assertEquals(WorkManager.getInstance(mockContext), result)
    }
}
