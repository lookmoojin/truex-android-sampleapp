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

class TrueCloudV3MoveToTrashUseCaseTest {
    private var fileRepository: FileRepository = mockk(relaxed = true)
    private lateinit var moveToTrashUseCase: MoveToTrashUseCase

    @BeforeEach
    fun setup() {
        moveToTrashUseCase = MoveToTrashUseCaseImpl(
            fileRepository = fileRepository
        )
    }

    @Test
    fun `test moveToTrash success`() = runTest {
        // arrange
        val fileList = arrayListOf<String>("aaa")
        val status = 201
        coEvery {
            fileRepository.moveToTrash(any())
        } returns flowOf(status)
        // act
        val flow = moveToTrashUseCase.execute(fileList)

        // assert
        flow.collectSafe { response ->
            assertEquals(status, response)
        }
    }
}
