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

class TrueCloudV3DeleteTrashDataUseCaseTest {
    private var trashRepository: TrashRepository = mockk(relaxed = true)
    private lateinit var deleteTrashData: DeleteTrashDataUseCase

    @BeforeEach
    fun setup() {
        deleteTrashData = DeleteTrashDataUseCaseImpl(
            trashRepository = trashRepository
        )
    }

    @Test
    fun `test deleteTrashData success`() = runTest {
        // arrange
        val fileList = arrayListOf<String>("aaa")
        val status = 201
        coEvery {
            trashRepository.deleteTrashFile(any())
        } returns flowOf(status)
        // act
        val flow = deleteTrashData.execute(fileList)

        // assert
        flow.collectSafe { response ->
            assertEquals(status, response)
        }
    }
}
