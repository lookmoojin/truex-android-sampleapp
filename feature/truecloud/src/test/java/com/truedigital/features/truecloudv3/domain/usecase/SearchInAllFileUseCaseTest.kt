package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.data.model.DataListStorageResponse
import com.truedigital.features.truecloudv3.data.model.SearchFileResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import com.truedigital.features.truecloudv3.data.repository.SearchFileRepository
import com.truedigital.features.truecloudv3.extension.convertToListTrueCloudV3Model
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SearchInAllFileUseCaseTest {
    private lateinit var searchInAllFileUseCase: SearchInAllFilesUseCase
    private val searchFileRepository: SearchFileRepository = mockk()

    @BeforeEach
    fun setup() {
        searchInAllFileUseCase = SearchInAllFilesUseCaseImpl(
            searchFileRepository
        )
    }

    @Test
    fun `test get search result`() = runTest {
        val uploaded = mutableListOf(TrueCloudV3StorageData())
        val data = DataListStorageResponse(uploaded = uploaded)
        val expectedResult = SearchFileResponse(
            data = data
        )
        coEvery {
            searchFileRepository.searchFile(any(), any(), any(), any())
        } returns flowOf(expectedResult)

        val flow = searchInAllFileUseCase.execute("", SortType.SORT_DATE_ASC, 5)

        flow.collectSafe {
            assertEquals(expectedResult.data?.uploaded?.convertToListTrueCloudV3Model(), it)
        }
    }
}
