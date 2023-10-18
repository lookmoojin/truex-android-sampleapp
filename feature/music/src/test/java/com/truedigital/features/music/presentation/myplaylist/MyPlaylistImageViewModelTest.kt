package com.truedigital.features.music.presentation.myplaylist

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.music.domain.image.usecase.GenerateGridImageUseCase
import com.truedigital.features.music.domain.image.usecase.UploadCoverImageUseCase
import com.truedigital.features.utils.MockDataModel
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertEquals

@ExtendWith(InstantTaskExecutorExtension::class)
class MyPlaylistImageViewModelTest {
    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    private lateinit var myPlaylistImageViewModel: MyPlaylistImageViewModel
    private val generateGridImageUseCase: GenerateGridImageUseCase = mock()
    private val uploadCoverImageUseCase: UploadCoverImageUseCase = mock()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)

    @BeforeEach
    fun setUp() {
        myPlaylistImageViewModel = MyPlaylistImageViewModel(
            coroutineDispatcher = coroutineDispatcher,
            generateGridImageUseCase = generateGridImageUseCase,
            uploadCoverImageUseCase = uploadCoverImageUseCase
        )
    }

    @Test
    fun generateGridImage_returnPair() {
        val mockResult = Pair("url", listOf("filter1", "filter2"))
        // Given
        whenever(generateGridImageUseCase.execute(any())).thenReturn(mockResult)

        // When
        myPlaylistImageViewModel.generateGridImage(listOf(MockDataModel.mockTrack))

        // Then
        assertEquals(
            myPlaylistImageViewModel.onDisplayCoverImage().getOrAwaitValue(),
            mockResult
        )
        verify(generateGridImageUseCase, times(1)).execute(any())
    }

    @Test
    fun saveCoverImage_success_saveCompleted() = runTest {
        // Given
        whenever(uploadCoverImageUseCase.execute(any(), any())).thenReturn(flowOf(Any()))

        // When
        myPlaylistImageViewModel.saveCoverImage(1, "imageUrl")

        // Then
        assertEquals(myPlaylistImageViewModel.onSaveCompleted().getOrAwaitValue(), Unit)
        verify(uploadCoverImageUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun saveCoverImage_error_retryAndCompleted() = runTest {
        // Given
        whenever(uploadCoverImageUseCase.execute(any(), any())).thenReturn(flowOf(Throwable()))

        // When
        myPlaylistImageViewModel.saveCoverImage(1, "imageUrl")

        // Then
        assertEquals(myPlaylistImageViewModel.onSaveCompleted().getOrAwaitValue(), Unit)
        verify(uploadCoverImageUseCase, times(1)).execute(any(), any())
    }
}
