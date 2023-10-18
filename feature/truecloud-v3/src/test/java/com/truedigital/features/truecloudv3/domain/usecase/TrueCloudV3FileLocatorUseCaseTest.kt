package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.repository.FileRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3FileLocatorUseCaseTest {
    private var fileRepository: FileRepository = mockk(relaxed = true)
    private lateinit var fileLocatorUseCase: FileLocatorUseCase

    @BeforeEach
    fun setup() {
        fileLocatorUseCase = FileLocatorUseCaseImpl(
            fileRepository = fileRepository
        )
    }

    @Test
    fun `test move success`() = runTest {
        // arrange
        val parentId = "1"
        val fileList = arrayListOf<String>("aaa")
        val type = "move"
        val status = 201
        coEvery {
            fileRepository.locateFile(any(), any(), any())
        } returns flowOf(status)
        // act
        val flow = fileLocatorUseCase.execute(parentId, fileList, type)

        // assert
        flow.collectSafe { response ->
            assertEquals(status, response)
        }
    }

    @Test
    fun `test copy success`() = runTest {
        // arrange
        val parentId = "1"
        val fileList = arrayListOf<String>("aaa")
        val type = "copy"
        val status = 201
        coEvery {
            fileRepository.locateFile(any(), any(), any())
        } returns flowOf(status)
        // act
        val flow = fileLocatorUseCase.execute(parentId, fileList, type)

        // assert
        flow.collectSafe { response ->
            assertEquals(status, response)
        }
    }
}
