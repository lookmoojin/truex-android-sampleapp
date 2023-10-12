package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.repository.TrashRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3RestoreTrashDataUseCaseTest {
    private var trashRepository: TrashRepository = mockk(relaxed = true)
    private lateinit var restoreTrashUseCase: RestoreTrashDataUseCase

    @BeforeEach
    fun setup() {
        restoreTrashUseCase = RestoreTrashDataUseCaseImpl(
            trashRepository = trashRepository
        )
    }

    @Test
    fun `test restoreTrash success`() = runTest {
        // arrange
        val fileList = arrayListOf<String>("aaa")
        val status = 201
        coEvery {
            trashRepository.restoreFile(any())
        } returns flowOf(status)
        // act
        val flow = restoreTrashUseCase.execute(fileList)

        // assert
        flow.collectSafe { response ->
            assertEquals(status, response)
        }
    }
}
