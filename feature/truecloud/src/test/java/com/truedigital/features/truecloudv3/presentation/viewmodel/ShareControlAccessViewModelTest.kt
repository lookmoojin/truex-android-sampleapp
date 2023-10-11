package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.content.Context
import com.jraska.livedata.TestObserver
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.domain.model.ShareConfigModel
import com.truedigital.features.truecloudv3.domain.model.SharedFile
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.domain.usecase.GetShareConfigUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetShareLinkUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UpdateShareConfigUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import java.text.SimpleDateFormat
import java.util.Date

@ExtendWith(InstantTaskExecutorExtension::class)
class ShareControlAccessViewModelTest {
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val getShareConfigUseCase: GetShareConfigUseCase = mockk()
    private val updateShareConfigUseCase: UpdateShareConfigUseCase = mockk()
    private val contextDataProviderWrapper: ContextDataProviderWrapper = mockk()
    private val getShareLinkUseCase: GetShareLinkUseCase = mockk()
    private lateinit var viewModel: ShareControlAccessViewModel

    @BeforeEach
    fun setUp() {
        viewModel = ShareControlAccessViewModel(
            coroutineDispatcher = coroutineDispatcher,
            getShareLinkUseCase = getShareLinkUseCase,
            getShareConfigUseCase = getShareConfigUseCase,
            updateShareConfigUseCase = updateShareConfigUseCase,
            contextDataProviderWrapper = contextDataProviderWrapper
        )
    }

    @Test
    fun setFileModelTest() {
        // arrange
        val testObserverOnUpdateView = TestObserver.test(viewModel.onUpdateView)
        val shareFile = SharedFile(
            id = "id",
            isPrivate = true,
            expireAt = "expireAt",
            password = "password",
            updatedAt = "",
            createdAt = ""
        )
        val shareConfigModel = ShareConfigModel(
            sharedFile = shareFile
        )
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )
        every { getShareConfigUseCase.execute(any()) } returns flowOf(shareConfigModel)

        // act
        viewModel.setFileModel(fileModel)

        // assert
        testObserverOnUpdateView.assertHasValue()
    }

    @Test
    fun getShareLinkTest() {
        // arrange
        val testObserverOnUpdateView = TestObserver.test(viewModel.openShareIntent)

        val mockUrlResponse = "url_test"
        val shareFile = SharedFile(
            id = "id",
            isPrivate = false,
            expireAt = "expireAt",
            password = "password",
            updatedAt = "",
            createdAt = ""
        )
        val shareConfigModel = ShareConfigModel(
            sharedFile = shareFile
        )

        every { getShareConfigUseCase.execute(any()) } returns flowOf(shareConfigModel)
        every { getShareLinkUseCase.execute(any()) } returns flowOf(mockUrlResponse)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )

        // act
        viewModel.setFileModel(fileModel)
        viewModel.getShareLink()

        // assert
        testObserverOnUpdateView.assertHasValue()
    }

    @Test
    fun switchIsPrivateTestFalse() {
        val testObserverOnUpdateView = TestObserver.test(viewModel.onShowUpdateButton)

        val shareFile = SharedFile(
            id = "id",
            isPrivate = false,
            expireAt = "expireAt",
            password = "password",
            updatedAt = "",
            createdAt = ""
        )
        val shareConfigModel = ShareConfigModel(
            sharedFile = shareFile
        )

        every { getShareConfigUseCase.execute(any()) } returns flowOf(shareConfigModel)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )

        // act
        viewModel.setFileModel(fileModel)
        viewModel.switchIsPrivate()

        // assert
        testObserverOnUpdateView.assertValue(true)
    }

    @Test
    fun switchIsPrivateTestTrue() {
        val testObserverOnUpdateView = TestObserver.test(viewModel.onShowUpdateButton)

        val shareFile = SharedFile(
            id = "id",
            isPrivate = true,
            expireAt = "expireAt",
            password = "password",
            updatedAt = "",
            createdAt = ""
        )
        val shareConfigModel = ShareConfigModel(
            sharedFile = shareFile
        )

        every { getShareConfigUseCase.execute(any()) } returns flowOf(shareConfigModel)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )

        // act
        viewModel.setFileModel(fileModel)
        viewModel.switchIsPrivate()

        // assert
        testObserverOnUpdateView.assertValue(true)
    }

    @Test
    fun clickEditPasswordTest() {
        val testObserverOnUpdateView = TestObserver.test(viewModel.onEditPassword)

        // act
        viewModel.clickEditPassword()

        // assert
        testObserverOnUpdateView.assertValue(Unit)
    }

    @Test
    fun onCancelDatePickerDialogTestCaseNull() {
        val testObserverOnUpdateView = TestObserver.test(viewModel.onExpireAtSwitchOffView)

        // act
        viewModel.onCancelDatePickerDialog()

        // assert
        testObserverOnUpdateView.assertValue(true)
    }

    @Test
    fun onCancelDatePickerDialogTestCaseNotNull() {
        val testObserverOnUpdateView = TestObserver.test(viewModel.onExpireAtSwitchOffView)
        val shareFile = SharedFile(
            id = "id",
            isPrivate = true,
            expireAt = "expireAt",
            password = "password",
            updatedAt = "",
            createdAt = ""
        )
        val shareConfigModel = ShareConfigModel(
            sharedFile = shareFile
        )

        every { getShareConfigUseCase.execute(any()) } returns flowOf(shareConfigModel)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )

        // act
        viewModel.setFileModel(fileModel)
        viewModel.onCancelDatePickerDialog()

        // assert
        testObserverOnUpdateView.assertNoValue()
    }

    @Test
    fun switchExpirationTestTrue() {
        val testObserverOnUpdateView = TestObserver.test(viewModel.onShowUpdateButton)

        val shareFile = SharedFile(
            id = "id",
            isPrivate = true,
            expireAt = "expireAt",
            password = "password",
            updatedAt = "",
            createdAt = ""
        )
        val shareConfigModel = ShareConfigModel(
            sharedFile = shareFile
        )

        every { getShareConfigUseCase.execute(any()) } returns flowOf(shareConfigModel)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )

        // act
        viewModel.setFileModel(fileModel)
        viewModel.switchExpiration()

        // assert
        testObserverOnUpdateView.assertValue(true)
    }

    @Test
    fun switchExpirationTestFalse() {
        val testObserverOnUpdateView = TestObserver.test(viewModel.onShowUpdateButton)

        val shareFile = SharedFile(
            id = "id",
            isPrivate = true,
            expireAt = "",
            password = "password",
            updatedAt = "",
            createdAt = ""
        )
        val shareConfigModel = ShareConfigModel(
            sharedFile = shareFile
        )

        every { getShareConfigUseCase.execute(any()) } returns flowOf(shareConfigModel)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )

        // act
        viewModel.setFileModel(fileModel)
        viewModel.switchExpiration()

        // assert
        testObserverOnUpdateView.assertValue(true)
    }

    @Test
    fun switchSetPassswordTestFalse() {
        val testObserverOnUpdateView = TestObserver.test(viewModel.onShowUpdateButton)

        val shareFile = SharedFile(
            id = "id",
            isPrivate = true,
            expireAt = "",
            password = "",
            updatedAt = "",
            createdAt = ""
        )
        val shareConfigModel = ShareConfigModel(
            sharedFile = shareFile
        )

        every { getShareConfigUseCase.execute(any()) } returns flowOf(shareConfigModel)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )

        // act
        viewModel.setFileModel(fileModel)
        viewModel.switchSetPassword()

        // assert
        testObserverOnUpdateView.assertValue(true)
    }

    @Test
    fun switchSetPassswordTestTrue() {
        val testObserverOnUpdateView = TestObserver.test(viewModel.onShowUpdateButton)

        val shareFile = SharedFile(
            id = "id",
            isPrivate = true,
            expireAt = "",
            password = "xxx",
            updatedAt = "",
            createdAt = ""
        )
        val shareConfigModel = ShareConfigModel(
            sharedFile = shareFile
        )

        every { getShareConfigUseCase.execute(any()) } returns flowOf(shareConfigModel)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )

        // act
        viewModel.setFileModel(fileModel)
        viewModel.switchSetPassword()
        viewModel.switchSetPassword()

        // assert
        testObserverOnUpdateView.assertValue(true)
    }

    @Test
    fun updateConfigTestPass() {
        val testObserverOnUpdateView = TestObserver.test(viewModel.onShowShareButton)
        val shareFile = SharedFile(
            id = "id",
            isPrivate = true,
            expireAt = "",
            password = "xxx",
            updatedAt = "",
            createdAt = ""
        )
        val shareConfigModel = ShareConfigModel(
            sharedFile = shareFile
        )
        val contextDataProvider: ContextDataProvider = mockk()
        every { getShareConfigUseCase.execute(any()) } returns flowOf(shareConfigModel)
        every {
            updateShareConfigUseCase.execute(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns flowOf(shareConfigModel)
        every { contextDataProviderWrapper.get() } returns contextDataProvider
        every { contextDataProvider.getString(any()) } returns ""

        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )

        // act
        viewModel.setFileModel(fileModel)
        viewModel.updateConfig("xxxxxx")

        // assert
        testObserverOnUpdateView.assertHasValue()
    }
    @Test
    fun updateConfigTestPassHasExpireAt() {
        val testObserverOnUpdateView = TestObserver.test(viewModel.onShowShareButton)
        val shareConfigModel = ShareConfigModel(
            sharedFile = SharedFile(
                id = "id",
                isPrivate = true,
                expireAt = "2023-06-21T12:34:56.789+0000",
                password = "xxx",
                updatedAt = "",
                createdAt = ""
            )
        )
        val contextDataProvider: ContextDataProvider = mockk()
        every { getShareConfigUseCase.execute(any()) } returns flowOf(shareConfigModel)
        every {
            updateShareConfigUseCase.execute(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns flowOf(shareConfigModel)
        every { contextDataProviderWrapper.get() } returns contextDataProvider
        every { contextDataProvider.getString(any()) } returns ""

        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )

        // act
        viewModel.setFileModel(fileModel)
        viewModel.updateConfig("xxxxxx")

        // assert
        testObserverOnUpdateView.assertHasValue()
    }
    @Test
    fun updateConfigTestPassEmptyPassword() {
        val testObserverOnUpdateView = TestObserver.test(viewModel.onShowShareButton)
        val shareFile = SharedFile(
            id = "id",
            isPrivate = true,
            expireAt = "",
            password = "xxx",
            updatedAt = "",
            createdAt = ""
        )
        val shareConfigModel = ShareConfigModel(
            sharedFile = shareFile
        )
        val contextDataProvider: ContextDataProvider = mockk()
        every { getShareConfigUseCase.execute(any()) } returns flowOf(shareConfigModel)
        every {
            updateShareConfigUseCase.execute(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns flowOf(shareConfigModel)
        every { contextDataProviderWrapper.get() } returns contextDataProvider
        every { contextDataProvider.getString(any()) } returns ""

        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )

        // act
        viewModel.setFileModel(fileModel)
        viewModel.updateConfig("")

        // assert
        testObserverOnUpdateView.assertNoValue()
    }
    @Test
    fun updateConfigTestFailPass3character() {
        val testObserverOnUpdateView = TestObserver.test(viewModel.onShowSnackbarError)

        val contextDataProvider: ContextDataProvider = mockk()
        every { contextDataProviderWrapper.get() } returns contextDataProvider
        every { contextDataProvider.getString(any()) } returns ""
        viewModel.switchSetPassword()
        viewModel.updateConfig("xxx")

        // assert
        testObserverOnUpdateView.assertHasValue()
    }

    @Test
    fun updateConfigTestError() {
        val testObserverOnUpdateView = TestObserver.test(viewModel.onShowSnackbarError)
        val shareFile = SharedFile(
            id = "id",
            isPrivate = true,
            expireAt = "",
            password = "xxx",
            updatedAt = "",
            createdAt = ""
        )
        val shareConfigModel = ShareConfigModel(
            sharedFile = shareFile
        )
        val contextDataProvider: ContextDataProvider = mockk()
        every {
            getShareConfigUseCase.execute(
                any()
            )
        } returns flowOf(shareConfigModel)
        every {
            updateShareConfigUseCase.execute(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns flow { error("mock error") }
        every { contextDataProviderWrapper.get() } returns contextDataProvider
        every { contextDataProvider.getString(any()) } returns ""
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )

        // act
        viewModel.setFileModel(fileModel)
        viewModel.updateConfig("xxxxxx")

        // assert
        testObserverOnUpdateView.assertHasValue()
    }
    @Test
    fun updateExpireTimeTest() {
        val testObserverOnUpdateExpireTime = TestObserver.test(viewModel.onUpdateExpireTime)
        val testObserverOnUpdateExpirationView = TestObserver.test(viewModel.onUpdateExpirationView)
        val date = Date()
        val mockDateFormat = mockk<SimpleDateFormat>()
        every { mockDateFormat.format(any()) } returns "2023-06-21T12:34:56.789+0000"

        // act
        viewModel.updateExpireTime(date)

        // assert
        testObserverOnUpdateExpireTime.assertHasValue()
        testObserverOnUpdateExpirationView.assertValue(true)
    }
    @Test
    fun passwordChangedTest() {
        val testObserveronUpdatePassword = TestObserver.test(viewModel.onUpdatePassword)
        val testObserveronUpdatePasswordCount = TestObserver.test(viewModel.onUpdatePasswordCount)

        val contextDataProvider: ContextDataProvider = mockk()
        val context: Context = mockk()
        every { contextDataProviderWrapper.get() } returns contextDataProvider
        every { contextDataProvider.getDataContext() } returns context
        every { context.getString(any(), any()) } returns "123456"
        // act
        viewModel.passwordChanged("123456")

        // assert
        testObserveronUpdatePassword.assertNoValue()
        testObserveronUpdatePasswordCount.assertHasValue()
    }
    @Test
    fun passwordChangedMoreThanMaxTest() {
        val testObserveronUpdatePassword = TestObserver.test(viewModel.onUpdatePassword)
        val testObserveronUpdatePasswordCount = TestObserver.test(viewModel.onUpdatePasswordCount)

        val contextDataProvider: ContextDataProvider = mockk()
        val context: Context = mockk()
        every { contextDataProviderWrapper.get() } returns contextDataProvider
        every { contextDataProvider.getDataContext() } returns context
        every { context.getString(any(), any()) } returns "123456789012345678901234567890"
        // act
        viewModel.passwordChanged("123456789012345678901234567890")

        // assert
        testObserveronUpdatePassword.assertHasValue()
        testObserveronUpdatePasswordCount.assertHasValue()
    }
}
