package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.truedigital.features.truecloudv3.domain.model.ContactPhoneNumberModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.navigation.ContactDetailToEditContactFragment
import com.truedigital.features.truecloudv3.navigation.router.ContactDetailRouterUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class CreateContactDetailBottomSheetDialogViewModelTest {
    private val router: ContactDetailRouterUseCase = mockk()
    private lateinit var viewModel: CreateContactDetailBottomSheetDialogViewModel

    @BeforeEach
    fun setUp() {
        viewModel = CreateContactDetailBottomSheetDialogViewModel(
            router = router
        )
    }

    @Test
    fun `test ContactDetailBottomSheet onViewCreated success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetupView)
        val ContactTrueCloudModel = ContactTrueCloudModel(
            firstName = "firstName test"
        )
        // act
        viewModel.onViewCreated(ContactTrueCloudModel)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test ContactDetailBottomSheet onEditClicked success`() {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.onEditClicked()

        // assert
        verify(exactly = 1) { router.execute(ContactDetailToEditContactFragment, any()) }
    }

    @Test
    fun `test onClickLabel success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onCallContact)
        val contactPhoneLabelModel = ContactPhoneNumberModel()
        // act
        viewModel.onCallClicked(contactPhoneLabelModel)

        // assert
        testObserver.assertHasValue()
    }
    @Test
    fun `test onDownloadClicked success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSaveContact)
        // act
        viewModel.onDownloadClicked()

        // assert
        testObserver.assertHasValue()
    }
    @Test
    fun `test onCopyLabel success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onCopyContact)
        val contactPhoneLabelModel = ContactPhoneNumberModel()
        // act
        viewModel.onCopyClicked(contactPhoneLabelModel)

        // assert
        testObserver.assertHasValue()
    }
}
