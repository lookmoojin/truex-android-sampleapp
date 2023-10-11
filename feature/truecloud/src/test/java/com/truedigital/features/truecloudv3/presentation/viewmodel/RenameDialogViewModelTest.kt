package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class RenameDialogViewModelTest {
    private lateinit var viewModel: RenameDialogViewModel

    @BeforeEach
    fun setUp() {
        viewModel = RenameDialogViewModel()
    }

    @Test
    fun `test RenameDialog onViewCreated success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetUpView)
        val fileModel = TrueCloudFilesModel.File()
        viewModel.trueCloudFilesModel = fileModel
        // act
        viewModel.onViewCreated(fileModel)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test RenameDialog onClickRename success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetFileName)
        // act
        viewModel.onClickRename("file name")

        // assert
        testObserver.assertHasValue()
    }
}
