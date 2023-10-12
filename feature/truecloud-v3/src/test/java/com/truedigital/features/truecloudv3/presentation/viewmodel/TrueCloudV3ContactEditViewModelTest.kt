package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.jraska.livedata.TestObserver
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.domain.model.ContactPhoneNumberModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudDisplayModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.domain.model.CustomPhoneLabelModel
import com.truedigital.features.truecloudv3.navigation.EditContactToContactSelectLabelBottomSheet
import com.truedigital.features.truecloudv3.navigation.router.ContactEditRouterUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertEquals

@ExtendWith(InstantTaskExecutorExtension::class)
class TrueCloudV3ContactEditViewModelTest {
    private lateinit var viewModel: TrueCloudV3ContactEditViewModel
    private val router = mockk<ContactEditRouterUseCase>()

    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)

    @BeforeEach
    fun setUp() {
        viewModel = TrueCloudV3ContactEditViewModel(
            router = router,
            coroutineDispatcher = coroutineDispatcher
        )
    }

    @Test
    fun `test setContactData success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetContactData)
        val contactData = ContactTrueCloudModel(
            picture = "",
            firstName = "test first",
            tel = listOf(
                ContactPhoneNumberModel(
                    type = "work",
                    number = "12345432"
                )
            )
        )
        val contactDisplayData = ContactTrueCloudDisplayModel(
            picture = null,
            firstName = "test first",
            tel = listOf(
                ContactPhoneNumberModel(
                    type = "work",
                    number = "12345432"
                )
            )
        )
        // act
        viewModel.setContactData(contactData)

        // assert
        testObserver.assertHasValue()
        testObserver.assertValue(contactDisplayData)
    }

    @Test
    fun `test setContactData picture not null success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetContactData)
        val bitmap: Bitmap = mockk()
        mockkStatic(Base64::class)
        mockkStatic(BitmapFactory::class)
        every { Base64.decode(any<String>(), any()) } returns "pictureBase64".toByteArray()
        every { BitmapFactory.decodeByteArray(any(), any(), any()) } returns bitmap
        val contactData = ContactTrueCloudModel(
            picture = "pictureBase64",
            firstName = "test first",
            tel = listOf(
                ContactPhoneNumberModel(
                    type = "work",
                    number = "12345432"
                )
            )
        )
        // act
        viewModel.setContactData(contactData)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickSave success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSaved)

        viewModel.customPhoneLabelList.add(
            CustomPhoneLabelModel(
                tagId = 123,
                label = "",
                number = "12345432"
            )
        )
        // act
        viewModel.onClickSave(null, "firstname", "lastname", "email")

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickSave success with picture`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSaved)
        val bitmap: Bitmap = mockk()
        mockkStatic(Base64::class)
        mockkStatic(BitmapFactory::class)
        every { Base64.encodeToString(ByteArray(0), 0) } returns "pictureBase64"
        every { BitmapFactory.decodeByteArray(any(), any(), any()) } returns bitmap
        val mockLabel = CustomPhoneLabelModel(
            tagId = 123,
            label = "",
            number = "12345432"
        )
        viewModel.customPhoneLabelList.add(
            mockLabel
        )
        // act
        viewModel.onClickSave(
            BitmapFactory.decodeByteArray(ByteArray(100), 0, 100),
            "firstname",
            "lastname",
            "email"
        )
        testObserver.assertNoValue()
    }

    @Test
    fun `test EditeOnClickSave success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onBackPressed)
        // act
        viewModel.onClickBack()

        // assert
        testObserver.assertHasValue()
        testObserver.assertValue(true)
    }

    @Test
    fun `test onClickRemove`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onRemoveCustomLabel)
        val customPhoneLabelModel = CustomPhoneLabelModel(
            tagId = 123,
            label = "",
            number = "12345432"
        )
        viewModel.customPhoneLabelList.add(customPhoneLabelModel)
        // act
        viewModel.onClickRemove(customPhoneLabelModel)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickLabel`() {
        // arrange
        val customPhoneLabelModel = CustomPhoneLabelModel(
            tagId = 123,
            label = "",
            number = "12345432"
        )
        every { router.execute(any(), any()) } just runs
        // act
        viewModel.onClickLabel(customPhoneLabelModel.tagId)

        // assert
        verify(exactly = 1) { router.execute(EditContactToContactSelectLabelBottomSheet, any()) }
    }

    @Test
    fun `test onClickAddCustomLabel`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.addCustomLabel)

        // act
        viewModel.onClickAddCustomLabel()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test updateCustomPhoneLabel`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onUpdatePhoneLabelView)
        val oldCustomPhoneLabelModel = CustomPhoneLabelModel(
            tagId = 123,
            label = "",
            number = "12345432"
        )
        val newCustomPhoneLabelModel = CustomPhoneLabelModel(
            tagId = 123,
            label = "",
            number = "00000"
        )
        viewModel.customPhoneLabelList.add(oldCustomPhoneLabelModel)
        // act
        viewModel.updateCustomPhoneLabel(newCustomPhoneLabelModel)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onEditTextNumberFocusOut`() {
        // arrange
        val oldCustomPhoneLabelModel = CustomPhoneLabelModel(
            tagId = 123,
            label = "",
            number = "12345432"
        )
        val newCustomPhoneLabelModel = CustomPhoneLabelModel(
            tagId = 123,
            label = "",
            number = "00000"
        )
        viewModel.customPhoneLabelList.add(oldCustomPhoneLabelModel)
        // act
        viewModel.onEditTextNumberFocusOut(newCustomPhoneLabelModel)

        // assert
        val response = viewModel.customPhoneLabelList.first().number
        assertEquals("00000", response)
    }

    @Test
    fun `test onClickDeleteContact`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.showConfirmDialogDelete)

        // act
        viewModel.onClickDeleteContact()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickConfirmDeleteContact`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onDeleteContact)

        // act
        viewModel.onClickConfirmDeleteContact()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test thumbnail imageview clicked`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onLaunchPickMedia)

        // act
        viewModel.onThumbnailImageViewClicked()

        // assert
        testObserver.assertHasValue()
    }
}
