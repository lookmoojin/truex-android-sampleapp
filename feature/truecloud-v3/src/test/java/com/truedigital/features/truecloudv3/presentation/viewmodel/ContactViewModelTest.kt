package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.jraska.livedata.TestObserver
import com.nhaarman.mockitokotlin2.any
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import com.truedigital.features.truecloudv3.domain.model.AlphabetItemModel
import com.truedigital.features.truecloudv3.domain.model.ContactDataModel
import com.truedigital.features.truecloudv3.domain.model.ContactPhoneNumberModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import com.truedigital.features.truecloudv3.domain.usecase.CheckContactUpdateUseCase
import com.truedigital.features.truecloudv3.domain.usecase.CompleteUploadContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.DownloadContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.ExportContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetContactDataFromSelectorUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetContactListFromPathUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetGroupAlphabetContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetGroupContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetLastUpdateContactPathUseCase
import com.truedigital.features.truecloudv3.domain.usecase.HasContactSyncedUseCase
import com.truedigital.features.truecloudv3.domain.usecase.SetContactSyncedUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UploadContactUseCase
import com.truedigital.features.truecloudv3.navigation.ContactToContactDetailBottomSheet
import com.truedigital.features.truecloudv3.navigation.ContactToOptionContactBottomSheet
import com.truedigital.features.truecloudv3.navigation.ContactToPermission
import com.truedigital.features.truecloudv3.navigation.ContactToSyncContactBottomSheet
import com.truedigital.features.truecloudv3.navigation.router.ContactListRouterUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.Calendar

@ExtendWith(InstantTaskExecutorExtension::class)
class ContactViewModelTest {
    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private val router: ContactListRouterUseCase = mockk()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val getContactUseCase: GetContactUseCase = mockk()
    private val getGroupContactUseCase: GetGroupContactUseCase = mockk()
    private val getGroupAlphabetContactUseCase:
        GetGroupAlphabetContactUseCase = mockk()
    private val uploadContactUseCase: UploadContactUseCase = mockk(relaxed = true)
    private val completeUploadContactUseCase: CompleteUploadContactUseCase = mockk()
    private val contextDataProviderWrapper: ContextDataProviderWrapper = mockk(relaxed = true)
    private val analyticManagerInterface: AnalyticManagerInterface = mockk(relaxed = true)
    private val hasContactSyncedUseCase: HasContactSyncedUseCase = mockk(relaxed = true)
    private val setContactSyncedUseCase: SetContactSyncedUseCase = mockk(relaxed = true)
    private val checkContactUpdateUseCase: CheckContactUpdateUseCase = mockk(relaxed = true)
    private val exportContactUseCase: ExportContactUseCase = mockk(relaxed = true)
    private val downloadContactUseCase: DownloadContactUseCase = mockk(relaxed = true)
    private val getLastUpdateContactPathUseCase: GetLastUpdateContactPathUseCase =
        mockk(relaxed = true)
    private val getContactListFromPathUseCase: GetContactListFromPathUseCase = mockk(relaxed = true)
    private val getContactDataFromSelectorUseCase: GetContactDataFromSelectorUseCase =
        mockk(relaxed = true)
    private val file: File = mockk(relaxed = true)
    private lateinit var viewModel: ContactViewModel

    @BeforeEach
    fun setup() {
        viewModel = ContactViewModel(
            router = router,
            coroutineDispatcher = coroutineDispatcher,
            getContactUseCase = getContactUseCase,
            getGroupContactUseCase = getGroupContactUseCase,
            getGroupAlphabetContactUseCase = getGroupAlphabetContactUseCase,
            checkContactUpdateUseCase = checkContactUpdateUseCase,
            getLastUpdateContactPathUseCase = getLastUpdateContactPathUseCase,
            getContactListFromPathUseCase = getContactListFromPathUseCase,
            uploadContactUseCase = uploadContactUseCase,
            completeUploadContactUseCase = completeUploadContactUseCase,
            getContactDataFromSelectorUseCase = getContactDataFromSelectorUseCase,
            exportContactUseCase = exportContactUseCase,
            downloadContactUseCase = downloadContactUseCase,
            contextDataProviderWrapper = contextDataProviderWrapper,
            analyticManagerInterface = analyticManagerInterface,
            hasContactSyncedUseCase = hasContactSyncedUseCase,
            setContactSyncedUseCase = setContactSyncedUseCase
        )
    }

    @Test
    fun `test onSelectSyncAllContact case false`() {
        // arrange
        val contactData = ContactTrueCloudModel(
            firstName = "test first"
        )
        every {
            getContactUseCase.execute()
        } returns flowOf(mutableListOf(contactData))
        every {
            hasContactSyncedUseCase.execute()
        } returns flowOf(false)
        // act
        viewModel.onSelectSyncAllContact()

        // assert
        verify(exactly = 1) { getContactUseCase.execute() }
    }

    @Test
    fun `test onSelectSyncAllContact case true`() {
        // arrange
        val expected = Pair(
            contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_title_confirm_sync_all_contacts),
            contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_subtitle_confirm_sync_all_contacts)
        )
        val testObserver = TestObserver.test(viewModel.onShowDialogSyncAll)
        val contactData = ContactTrueCloudModel(
            firstName = "test first"
        )
        every {
            getContactUseCase.execute()
        } returns flowOf(mutableListOf(contactData))
        every {
            hasContactSyncedUseCase.execute()
        } returns flowOf(true)
        // act
        viewModel.onSelectSyncAllContact()

        // assert
        verify(exactly = 0) { getContactUseCase.execute() }
        verify(exactly = 1) { hasContactSyncedUseCase.execute() }
        testObserver.assertValue(expected)
    }

    @Test
    fun `test performClickIntroPermission case success`() {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.performClickIntroPermission(Bundle())

        // assert
        verify(exactly = 1) {
            router.execute(
                ContactToPermission,
                any()
            )
        }
    }

    @Test
    fun `test onClickConfirmDeleteAllDialog case success`() {
        // arrange
        val observer = TestObserver.test(viewModel.onShowSnackbarSuccess)
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            completeUploadContactUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)

        val contactTrueCloudModelA = ContactTrueCloudModel(
            firstName = "Atest firstname",
            lastName = "A",
            email = "A",
        )

        val contactTrueCloudModelB = ContactTrueCloudModel(
            firstName = "Ztest firstname",
            lastName = "Z",
            email = "Z",
        )
        viewModel.contactList.addAll(listOf(contactTrueCloudModelB, contactTrueCloudModelA))

        every { trueCloudV3TransferObserver.getState() } returns TrueCloudV3TransferState.COMPLETED
        every {
            uploadContactUseCase.execute(
                any(),
                any(),
                any()
            )
        } returns flowOf(trueCloudV3TransferObserver)
        every { analyticManagerInterface.trackEvent(any()) } just runs

        // act
        viewModel.onClickConfirmDeleteAllDialog()

        // assert
        observer.assertHasValue()
        verify(exactly = 1) {
            uploadContactUseCase.execute(
                any(),
                any(),
                any()
            )
        }
    }

    @Test
    fun `test onClickConfirmDeleteAllDialog case error`() {
        // arrange
        val observer = TestObserver.test(viewModel.onShowSnackbarError)

        val contactTrueCloudModelA = ContactTrueCloudModel(
            firstName = "Atest firstname",
            lastName = "A",
            email = "A",
        )

        val contactTrueCloudModelB = ContactTrueCloudModel(
            firstName = "Ztest firstname",
            lastName = "Z",
            email = "Z",
        )
        viewModel.contactList.addAll(listOf(contactTrueCloudModelB, contactTrueCloudModelA))

        every {
            uploadContactUseCase.execute(
                any(),
                any(),
                any()
            )
        } returns flow {
            error("test error")
        }
        every { analyticManagerInterface.trackEvent(any()) } just runs

        // act
        viewModel.onClickConfirmDeleteAllDialog()

        // assert
        observer.assertHasValue()
        verify(exactly = 1) {
            uploadContactUseCase.execute(
                any(),
                any(),
                any()
            )
        }
    }

    @Test
    fun `test getContactList case success`() = runTest {
        // arrange
        val contactData = ContactDataModel(
            id = "test id",
            size = "123",
            isUploaded = true
        )
        val trueCloudV3ContactData1 = ContactTrueCloudModel(
            firstName = "firstname"
        )
        val trueCloudV3ContactData2 = ContactTrueCloudModel(
            firstName = "firstname"
        )

        val alphabetItemModelList = listOf(
            AlphabetItemModel(
                alphabet = "a",
                index = 0
            ),
            AlphabetItemModel(
                alphabet = "b",
                index = 1
            )
        )
        val contactList = listOf(trueCloudV3ContactData1, trueCloudV3ContactData2)
        val testObserver = TestObserver.test(viewModel.updateContactData)
        val testAlphabetObserver = TestObserver.test(viewModel.groupAlphabetLiveData)
        every {
            checkContactUpdateUseCase.execute()
        } returns flowOf(contactData)
        coEvery {
            getLastUpdateContactPathUseCase.execute(any())
        } returns flowOf(Pair(true, ""))
        coEvery {
            getContactListFromPathUseCase.execute(any())
        } returns flowOf(contactList)
        coEvery {
            getGroupContactUseCase.execute(any())
        } returns flowOf(contactList)

        coEvery {
            getGroupAlphabetContactUseCase.execute(any())
        } returns flowOf(alphabetItemModelList)

        mockkConstructor(FileReader::class)
        every { anyConstructed<FileReader>().read() } returns 1
        mockkConstructor(BufferedReader::class)
        every { anyConstructed<BufferedReader>().readLine() } returns "abc"
        every { anyConstructed<BufferedReader>().close() } returns Unit

        // act
        viewModel.getContactList()

        // assert
        testObserver.assertHasValue()
        testAlphabetObserver.assertHasValue()
    }

    @Test
    fun `test getContactList case success empty contact`() = runTest {
        // arrange
        val contactData = ContactDataModel(
            id = "test id",
            size = "123",
            isUploaded = true
        )
        val trueCloudV3ContactData1 = ContactTrueCloudModel(
            firstName = "firstname"
        )
        val trueCloudV3ContactData2 = ContactTrueCloudModel(
            firstName = "firstname"
        )
        val contactList = listOf(trueCloudV3ContactData1, trueCloudV3ContactData2)
        val testObserver = TestObserver.test(viewModel.showEmptyContact)
        every {
            checkContactUpdateUseCase.execute()
        } returns flowOf(contactData)
        coEvery {
            getLastUpdateContactPathUseCase.execute(any())
        } returns flowOf(Pair(true, ""))
        coEvery {
            getContactListFromPathUseCase.execute(any())
        } returns flowOf(contactList)
        coEvery {
            getGroupContactUseCase.execute(any())
        } returns flowOf(listOf())

        coEvery {
            getGroupAlphabetContactUseCase.execute(any())
        } returns flowOf(listOf())

        mockkConstructor(FileReader::class)
        every { anyConstructed<FileReader>().read() } returns 1
        mockkConstructor(BufferedReader::class)
        every { anyConstructed<BufferedReader>().readLine() } returns "abc"
        every { anyConstructed<BufferedReader>().close() } returns Unit

        // act
        viewModel.getContactList()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test getContactList case download success`() = runTest {
        // arrange
        val contactData = ContactDataModel(
            id = "test id",
            size = "1234",
            isUploaded = true
        )
        val trueCloudV3ContactData1 = ContactTrueCloudModel(
            firstName = "firstname"
        )
        val trueCloudV3ContactData2 = ContactTrueCloudModel(
            firstName = "firstname"
        )
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        val contactList = listOf(trueCloudV3ContactData1, trueCloudV3ContactData2)
        every {
            checkContactUpdateUseCase.execute()
        } returns flowOf(contactData)
        every {
            downloadContactUseCase.execute(any(), any())
        } returns flowOf(trueCloudV3TransferObserver)
        every { trueCloudV3TransferObserver.getState() } returns TrueCloudV3TransferState.COMPLETED
        // Act
        coEvery {
            getLastUpdateContactPathUseCase.execute(any())
        } returns flowOf(Pair(false, ""))
        coEvery {
            getContactListFromPathUseCase.execute(any())
        } returns flowOf(contactList)
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.COMPLETED)
        }

        // act
        viewModel.getContactList()

        // assert
        verify(exactly = 1) {
            trueCloudV3TransferObserver.setTransferListener(any())
        }
    }

    @Test
    fun `test getContactList case fail`() {
        // arrange
        val contactData = ContactDataModel(
            id = "test id",
            isUploaded = true
        )
        val testObserver = TestObserver.test(viewModel.updateContactData)
        every {
            checkContactUpdateUseCase.execute()
        } returns flowOf(contactData)

        // act
        viewModel.getContactList()

        // assert
        testObserver.assertNoValue()
    }

    @Test
    fun `test getContactList case fail api`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onGetContactError)
        coEvery {
            checkContactUpdateUseCase.execute()
        } returns flow {
            error("")
        }

        // act
        viewModel.getContactList()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test getContactList first time case not found`() {
        // arrange
        val contactData = ContactDataModel(
            id = "test id",
            size = null,
            isUploaded = true
        )
        val testObserver = TestObserver.test(viewModel.onContactNotfound)
        every {
            checkContactUpdateUseCase.execute()
        } returns flowOf(contactData)

        // act
        viewModel.getContactList()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun testUpdateContact() {
        // arrange
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val contactTrueCloudModelA = ContactTrueCloudModel(
            firstName = "Atest firstname",
            lastName = "A",
            email = "A",
        )

        val contactTrueCloudModelB = ContactTrueCloudModel(
            firstName = "Ztest firstname",
            lastName = "Z",
            email = "Z",
        )

        val contactTrueCloudModelNew = ContactTrueCloudModel(
            firstName = "Ctest firstname",
            lastName = "C",
            email = "C",
        )
        every {
            uploadContactUseCase.execute(any(), any(), any())
        } returns flowOf(trueCloudV3TransferObserver)
        viewModel.contactList.addAll(listOf(contactTrueCloudModelB, contactTrueCloudModelA))

        // act
        viewModel.updateContact(contactTrueCloudModelNew)

        // assert
        verify(exactly = 1) { uploadContactUseCase.execute(any(), any(), any()) }
    }

    @Test
    fun testOnContactClicked() {
        // arrange
        val contactTrueCloudModel = ContactTrueCloudModel(
            firstName = "test firstname"
        )

        every { router.execute(any(), any()) } just runs

        // act
        viewModel.onContactClicked(contactTrueCloudModel)

        // assert
        verify(exactly = 1) { router.execute(ContactToContactDetailBottomSheet, any()) }
    }

    @Test
    fun testOnClickSync() {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.onClickSync()

        // assert
        verify(exactly = 1) { router.execute(ContactToSyncContactBottomSheet, any()) }
    }

    @Test
    fun testOnClickMoreOption() {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.onClickMoreOption()

        // assert
        verify(exactly = 1) { router.execute(ContactToOptionContactBottomSheet, any()) }
    }

    @Test
    fun `test selectContactPermissionAlready case fail`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onIntentActionGetContact)
        // act
        viewModel.selectContactPermissionAlready()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test completeUpload case success`() = runTest {
        // arrange
        val contactData = ContactDataModel(
            id = "test id",
            isUploaded = true
        )
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            completeUploadContactUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)
        coEvery {
            checkContactUpdateUseCase.execute()
        } returns flowOf(contactData)

        // act
        viewModel.completeUpload("")

        // assert
        coVerify(exactly = 1) {
            checkContactUpdateUseCase.execute()
        }
    }

    @Test
    fun `test ContactOnClickBack success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onBackPressed)

        // act
        viewModel.onClickBack()

        // assert
        testObserver.assertHasValue()
        testObserver.assertValue(true)
    }

    @Test
    fun `test uploadContact same item success`() {
        // arrange
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val trueCloudV3Model = TrueCloudV3Model()
        every {
            uploadContactUseCase.execute(any(), any(), any())
        } returns flowOf(trueCloudV3TransferObserver)
        every {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.COMPLETED
        every {
            completeUploadContactUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)
        val contactData = ContactDataModel(
            id = "test id",
            isUploaded = true
        )
        every {
            checkContactUpdateUseCase.execute()
        } returns flowOf(contactData)

        // act
        viewModel.uploadContact()

        // assert
        verify(exactly = 1) { uploadContactUseCase.execute(any(), any(), any()) }
    }

    @Test
    fun `test uploadContact status not complete success`() {
        // arrange
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val trueCloudV3Model = TrueCloudV3Model()
        every {
            uploadContactUseCase.execute(any(), any(), any())
        } returns flowOf(trueCloudV3TransferObserver)
        every {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.IN_PROGRESS
        every {
            completeUploadContactUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)
        val contactData = ContactDataModel(
            id = "test id",
            isUploaded = true
        )
        every {
            checkContactUpdateUseCase.execute()
        } returns flowOf(contactData)

        // act
        viewModel.uploadContact()

        // assert
        verify(exactly = 1) {
            uploadContactUseCase.execute(any(), any(), any())
        }
    }

    @Test
    fun `test transferListener status complete`() {
        // arrange
        val transferObserver = mockk<TransferObserver>()
        val trueCloudV3Model = TrueCloudV3Model()
        every {
            transferObserver.state
        } returns TransferState.IN_PROGRESS
        every {
            completeUploadContactUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)
        val contactData = ContactDataModel(
            id = "test id",
            isUploaded = true
        )
        every {
            checkContactUpdateUseCase.execute()
        } returns flowOf(contactData)

        // act
        viewModel.transferListener.onStateChanged(1, TrueCloudV3TransferState.COMPLETED)

        // assert
        verify(exactly = 1) {
            checkContactUpdateUseCase.execute()
        }
    }

    @Test
    fun `test transferListener status IN_PROGRESS`() {
        // arrange
        val transferObserver = mockk<TransferObserver>()
        val trueCloudV3Model = TrueCloudV3Model()
        every {
            transferObserver.state
        } returns TransferState.IN_PROGRESS
        every {
            completeUploadContactUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)
        val contactData = ContactDataModel(
            id = "test id",
            isUploaded = true
        )
        every {
            checkContactUpdateUseCase.execute()
        } returns flowOf(contactData)

        // act
        viewModel.transferListener.onStateChanged(1, TrueCloudV3TransferState.IN_PROGRESS)

        // assert
        verify(exactly = 0) {
            checkContactUpdateUseCase.execute()
        }
    }

    @Test
    fun `test transferListener onProgressChange`() {
        // arrange
        val transferObserver = mockk<TransferObserver>()
        val trueCloudV3Model = TrueCloudV3Model()
        every {
            transferObserver.state
        } returns TransferState.IN_PROGRESS
        every {
            completeUploadContactUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)
        val contactData = ContactDataModel(
            id = "test id",
            isUploaded = true
        )
        every {
            checkContactUpdateUseCase.execute()
        } returns flowOf(contactData)

        // act
        viewModel.transferListener.onProgressChanged(1, 45, 100)

        // assert
        verify(exactly = 0) {
            checkContactUpdateUseCase.execute()
        }
    }

    @Test
    fun `test transferListener onError`() {
        // arrange
        val transferObserver = mockk<TransferObserver>()
        val trueCloudV3Model = TrueCloudV3Model()
        every {
            transferObserver.state
        } returns TransferState.IN_PROGRESS
        every {
            completeUploadContactUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)
        val contactData = ContactDataModel(
            id = "test id",
            isUploaded = true
        )
        every {
            checkContactUpdateUseCase.execute()
        } returns flowOf(contactData)

        // act
        viewModel.transferListener.onError(1, Exception())

        // assert
        verify(exactly = 0) {
            checkContactUpdateUseCase.execute()
        }
    }

    @Test
    fun `test onActivityResult moveToNext false`() {
        // arrange
        val activityResult = mockk<Intent>()
        val contactTrueCloudModel = ContactTrueCloudModel(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            tel = mutableListOf(
                ContactPhoneNumberModel(
                    type = "mobile",
                    number = "123-456-7890"
                )
            )
        )
        every { activityResult.data } returns mockk()
        coEvery {
            getContactDataFromSelectorUseCase.execute(any())
        } returns flow {
            emit(listOf(contactTrueCloudModel))
        }
        every {
            uploadContactUseCase.execute(
                any(),
                any(),
                any()
            )
        } returns flowOf(any())

        // act
        viewModel.onActivityResult(activityResult)

        // assert
        coVerify(exactly = 1) { getContactDataFromSelectorUseCase.execute(any()) }
    }

    @Test
    fun `test onActivityResult complete`() {
        // arrange
        val observer = TestObserver.test(viewModel.onShowSnackbarSuccess)
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val activityResult = mockk<Intent>()
        val contactTrueCloudModel = ContactTrueCloudModel(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            tel = mutableListOf(
                ContactPhoneNumberModel(
                    type = "mobile",
                    number = "123-456-7890"
                )
            )
        )
        every { activityResult.data } returns mockk()
        coEvery {
            getContactDataFromSelectorUseCase.execute(any())
        } returns flow {
            emit(listOf(contactTrueCloudModel))
        }
        val trueCloudV3Model = TrueCloudV3Model()
        every {
            completeUploadContactUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)
        every {
            uploadContactUseCase.execute(
                any(),
                any(),
                any()
            )
        } returns flowOf(trueCloudV3TransferObserver)
        every { trueCloudV3TransferObserver.getState() } returns TrueCloudV3TransferState.COMPLETED

        // act
        viewModel.onActivityResult(activityResult)

        // assert
        observer.assertHasValue()
        coVerify(exactly = 1) { getContactDataFromSelectorUseCase.execute(any()) }
    }

    @Test
    fun `test onActivityResult IN_PROGRESS`() {
        // arrange
        val observer = TestObserver.test(viewModel.onShowSnackbarSuccess)
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val activityResult = mockk<Intent>()
        val contactTrueCloudModel = ContactTrueCloudModel(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            tel = mutableListOf(
                ContactPhoneNumberModel(
                    type = "mobile",
                    number = "123-456-7890"
                )
            )
        )
        every { activityResult.data } returns mockk()
        coEvery {
            getContactDataFromSelectorUseCase.execute(any())
        } returns flow {
            emit(listOf(contactTrueCloudModel))
        }
        val trueCloudV3Model = TrueCloudV3Model()
        every {
            completeUploadContactUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)
        every {
            uploadContactUseCase.execute(
                any(),
                any(),
                any()
            )
        } returns flowOf(trueCloudV3TransferObserver)
        every { trueCloudV3TransferObserver.getState() } returns TrueCloudV3TransferState.IN_PROGRESS
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.IN_PROGRESS)
        }

        // act
        viewModel.onActivityResult(activityResult)

        // assert
        observer.assertHasValue()
        coVerify(exactly = 1) { getContactDataFromSelectorUseCase.execute(any()) }
    }

    @Test
    fun `test exportToDevice success`() {
        // arrange
        val observer = TestObserver.test(viewModel.onShowSnackbarSuccess)
        val calendar = mockk<Calendar>()
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns calendar
        every { calendar.getTimeInMillis() } returns 10000L
        val mockFile = mockk<File>(relaxed = true)
        mockkStatic(Environment::class)
        every { Environment.getExternalStoragePublicDirectory(any()).path } returns "/storage/emulated/0"
        every { mockFile.exists() } returns true

        coEvery { exportContactUseCase.execute(any()) } returns flowOf(Unit)
        // act
        viewModel.exportToDevice()

        // assert
        observer.assertHasValue()
    }

    @Test
    fun `test exportToDevice error`() {
        // arrange
        val observer = TestObserver.test(viewModel.onShowSnackbarExportContactError)

        coEvery { exportContactUseCase.execute(any()) } returns flow { error("mock error") }
        // act
        viewModel.exportToDevice()

        // assert
        observer.assertHasValue()
    }

    @Test
    fun `test groupContactByAlphabet success`() = runTest {
        // arrange
        val observer = TestObserver.test(viewModel.groupAlphabetLiveData)
        val alphabetMockUp = listOf(
            AlphabetItemModel(
                alphabet = "A",
                index = 0,
                isActive = true,
                position = 0,
                size = 1
            )
        )
        val contactList = listOf(
            ContactTrueCloudModel(
                firstName = "test first"
            )
        )
        coEvery { getGroupAlphabetContactUseCase.execute(any()) } returns flowOf(alphabetMockUp)

        // act
        viewModel.groupContactByAlphabet(contactList)

        // assert
        observer.assertValue(alphabetMockUp)
    }

    @Test
    fun `test onClickCall`() = runTest {
        // arrange
        val observer = TestObserver.test(viewModel.callToNumber)
        val contactPhoneNumberModel = ContactPhoneNumberModel(
            type = "work",
            number = "9809809"
        )
        // act
        viewModel.onClickCall(contactPhoneNumberModel)

        // assert
        observer.assertHasValue()
    }

    @Test
    fun testCheckRetryState() {
        // arrange
        coEvery {
            checkContactUpdateUseCase.execute()
        } returns flowOf()
        // act
        viewModel.checkRetryState("ACTION_GET_CONTACT")
        // assert
        coVerify(exactly = 1) {
            checkContactUpdateUseCase.execute()
        }
    }

    @Test
    fun testCheckRetryStateActionNull() {
        // act
        viewModel.checkRetryState("")
        // assert
        coVerify(exactly = 0) {
            checkContactUpdateUseCase.execute()
        }
    }

    @Test
    fun testOnClickConfirmSyncAllDialog() {
        // arrange
        val observer = TestObserver.test(viewModel.onShowSnackbarSuccess)
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val contactData = ContactTrueCloudModel(
            firstName = "test first"
        )
        every {
            getContactUseCase.execute()
        } returns flowOf(mutableListOf(contactData))
        coEvery {
            setContactSyncedUseCase.execute()
        } returns Unit
        every {
            uploadContactUseCase.execute(
                any(),
                any(),
                any()
            )
        } returns flowOf(trueCloudV3TransferObserver)
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            completeUploadContactUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)

        val contactTrueCloudModelA = ContactTrueCloudModel(
            firstName = "Atest firstname",
            lastName = "A",
            email = "A",
        )

        val contactTrueCloudModelB = ContactTrueCloudModel(
            firstName = "Ztest firstname",
            lastName = "Z",
            email = "Z",
        )
        viewModel.contactList.addAll(listOf(contactTrueCloudModelB, contactTrueCloudModelA))

        every { trueCloudV3TransferObserver.getState() } returns TrueCloudV3TransferState.COMPLETED
        // act
        viewModel.onClickConfirmSyncAllDialog()

        // assert
        observer.assertHasValue()
        verify(exactly = 1) {
            uploadContactUseCase.execute(
                any(),
                any(),
                any()
            )
        }
        coVerify(exactly = 1) { setContactSyncedUseCase.execute() }
    }

    @Test
    fun testOnClickConfirmSyncAllDialogWAITING() {
        // arrange
        val observer = TestObserver.test(viewModel.onShowSnackbarSuccess)
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val contactData = ContactTrueCloudModel(
            firstName = "test first"
        )
        every {
            getContactUseCase.execute()
        } returns flowOf(mutableListOf(contactData))
        coEvery {
            setContactSyncedUseCase.execute()
        } returns Unit
        every {
            uploadContactUseCase.execute(
                any(),
                any(),
                any()
            )
        } returns flowOf(trueCloudV3TransferObserver)
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            completeUploadContactUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)

        val contactTrueCloudModelA = ContactTrueCloudModel(
            firstName = "Atest firstname",
            lastName = "A",
            email = "A",
        )

        val contactTrueCloudModelB = ContactTrueCloudModel(
            firstName = "Ztest firstname",
            lastName = "Z",
            email = "Z",
        )
        viewModel.contactList.addAll(listOf(contactTrueCloudModelB, contactTrueCloudModelA))
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        every { trueCloudV3TransferObserver.getState() } returns TrueCloudV3TransferState.WAITING
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.COMPLETED)
        }
        // act
        viewModel.onClickConfirmSyncAllDialog()

        // assert
        observer.assertHasValue()
        verify(exactly = 1) {
            uploadContactUseCase.execute(
                any(),
                any(),
                any()
            )
        }
        coVerify(exactly = 1) { setContactSyncedUseCase.execute() }
    }

    @Test
    fun testOnClickConfirmSyncAllDialogError() {
        // arrange
        val observer = TestObserver.test(viewModel.onShowSnackbarError)
        every {
            getContactUseCase.execute()
        } returns flow { error("mock error") }

        // act
        viewModel.onClickConfirmSyncAllDialog()

        // assert
        observer.assertHasValue()
    }

    @Test
    fun testDeleteAllContact() {
        // act
        val expected = Pair(
            contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_title_confirm_delete_all_contacts),
            contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_subtitle_confirm_delete_all_contacts)
        )
        val observer = TestObserver.test(viewModel.onShowDialogDeleteAll)
        viewModel.deleteAllContact()
        // assert
        observer.assertValue(expected)
    }

    @Test
    fun testOnCreated() {
        // act
        every { analyticManagerInterface.trackScreen(any()) } just runs

        viewModel.onViewCreated()
        // assert
        verify { analyticManagerInterface.trackScreen(any()) }
    }

    @Test
    fun `test deleteContact`() {
        // arrange
        val observer = TestObserver.test(viewModel.onShowSnackbarSuccess)
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val contactTrueCloudModelA = ContactTrueCloudModel(
            firstName = "Atest firstname",
            lastName = "A",
            email = "A",
        )

        val contactTrueCloudModelB = ContactTrueCloudModel(
            firstName = "Ztest firstname",
            lastName = "Z",
            email = "Z",
        )

        every {
            uploadContactUseCase.execute(any(), any(), any())
        } returns flowOf(trueCloudV3TransferObserver)
        every { router.execute(any(), any()) } just runs
        viewModel.contactList.addAll(listOf(contactTrueCloudModelB, contactTrueCloudModelA))
        viewModel.onContactClicked(contactTrueCloudModelB)
        every { trueCloudV3TransferObserver.getState() } returns TrueCloudV3TransferState.COMPLETED
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            completeUploadContactUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)
        // act
        viewModel.deleteContact()

        // assert
        observer.assertHasValue()
        verify(exactly = 1) { uploadContactUseCase.execute(any(), any(), any()) }
    }

    @Test
    fun `test deleteContact setTransferListener`() {
        // arrange
        val observer = TestObserver.test(viewModel.onShowSnackbarSuccess)
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        val contactTrueCloudModelA = ContactTrueCloudModel(
            firstName = "Atest firstname",
            lastName = "A",
            email = "A",
        )

        val contactTrueCloudModelB = ContactTrueCloudModel(
            firstName = "Ztest firstname",
            lastName = "Z",
            email = "Z",
        )

        every {
            uploadContactUseCase.execute(any(), any(), any())
        } returns flowOf(trueCloudV3TransferObserver)
        every { router.execute(any(), any()) } just runs
        viewModel.contactList.addAll(listOf(contactTrueCloudModelB, contactTrueCloudModelA))
        viewModel.onContactClicked(contactTrueCloudModelB)
        every { trueCloudV3TransferObserver.getState() } returns TrueCloudV3TransferState.WAITING
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.COMPLETED)
            slotListener.captured.onError(0, Exception())
            slotListener.captured.onProgressChanged(0, 0L, 100L)
        }
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            completeUploadContactUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)
        // act
        viewModel.deleteContact()

        // assert
        observer.assertHasValue()
        verify(exactly = 1) { uploadContactUseCase.execute(any(), any(), any()) }
    }

    @Test
    fun `test deleteContact case error`() {
        // arrange
        val observer = TestObserver.test(viewModel.onShowSnackbarError)
        val contactTrueCloudModelA = ContactTrueCloudModel(
            firstName = "Atest firstname",
            lastName = "A",
            email = "A",
        )

        val contactTrueCloudModelB = ContactTrueCloudModel(
            firstName = "Ztest firstname",
            lastName = "Z",
            email = "Z",
        )

        every {
            uploadContactUseCase.execute(any(), any(), any())
        } returns flow {
            error("error")
        }
        every { router.execute(any(), any()) } just runs
        viewModel.contactList.addAll(listOf(contactTrueCloudModelB, contactTrueCloudModelA))
        viewModel.onContactClicked(contactTrueCloudModelB)

        // act
        viewModel.deleteContact()

        // assert
        observer.assertHasValue()
        verify(exactly = 1) { uploadContactUseCase.execute(any(), any(), any()) }
    }
}
