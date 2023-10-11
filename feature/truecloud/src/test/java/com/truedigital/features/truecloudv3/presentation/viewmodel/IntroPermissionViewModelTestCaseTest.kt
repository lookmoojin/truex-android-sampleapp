package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.Manifest
import com.jraska.livedata.TestObserver
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.data.repository.PermissionDisableRepository
import com.truedigital.features.truecloudv3.domain.model.DetailDialogModel
import com.truedigital.features.truecloudv3.domain.usecase.NodePermission
import com.truedigital.features.truecloudv3.domain.usecase.TrueCloudV3ConfigPermissionImageUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertEquals

internal interface IntroPermissionViewModelTestCase {
    fun `test getStorePermissionList case permission and dialogModel not null`()
    fun `test getStorePermissionList case permission is null`()
    fun `test getStorePermissionList case dialogModel is null`()
    fun `test getStorePermissionList case value is null`()
    fun `test checkFirstDisablePermission case permission STORAGE disable true`()
    fun `test checkFirstDisablePermission case permission STORAGE disable false`()
    fun `test checkFirstDisablePermission case permission CONTACT disable true`()
    fun `test checkFirstDisablePermission case permission CONTACT disable false`()
    fun `test checkFirstDisablePermission case other`()
    fun `test getPermissionImage case success`()
    fun `test getPermissionImage case dialog model is null`()
}

@ExtendWith(InstantTaskExecutorExtension::class)
internal class IntroPermissionViewModelTest : IntroPermissionViewModelTestCase {

    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    private lateinit var viewModel: IntroPermissionViewModel

    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val permissionDisableRepository: PermissionDisableRepository = mockk()
    private val configPermissionImageUseCase: TrueCloudV3ConfigPermissionImageUseCase = mockk()

    @BeforeEach
    fun setUp() {
        viewModel = IntroPermissionViewModel(
            coroutineDispatcher,
            permissionDisableRepository,
            configPermissionImageUseCase
        )
    }

    @Test
    override fun `test getStorePermissionList case permission and dialogModel not null`() {
        // arrange
        val mockArray = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        viewModel.setStorePermissionList(mockArray)
        viewModel.setStoreDetailDialogModel(
            DetailDialogModel(
                nodePermission = NodePermission.STORAGE,
                iconType = DialogIconType.FOLDER,
                title = "title",
                subTitle = "subTitle"
            )
        )
        val testObserver = TestObserver.test(viewModel.onRequestPermissionList)

        // act
        viewModel.getStorePermissionList()

        // assert
        testObserver.assertValue {
            assertEquals(mockArray, it.first)
            assertEquals(DialogIconType.FOLDER, it.second.iconType)
            assertEquals("title", it.second.title)
            assertEquals("subTitle", it.second.subTitle)
            true
        }
    }

    @Test
    override fun `test getStorePermissionList case permission is null`() {
        // arrange
        viewModel.setStorePermissionList(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        val testObserver = TestObserver.test(viewModel.onRequestPermissionList)

        // act
        viewModel.getStorePermissionList()

        // assert
        testObserver.assertNoValue()
    }

    @Test
    override fun `test getStorePermissionList case dialogModel is null`() {
        // arrange
        viewModel.setStoreDetailDialogModel(
            DetailDialogModel(
                nodePermission = NodePermission.STORAGE,
                iconType = DialogIconType.FOLDER,
                title = "title",
                subTitle = "subTitle"
            )
        )
        val testObserver = TestObserver.test(viewModel.onRequestPermissionList)

        // act
        viewModel.getStorePermissionList()

        // assert
        testObserver.assertNoValue()
    }

    @Test
    override fun `test getStorePermissionList case value is null`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onRequestPermissionList)

        // act
        viewModel.getStorePermissionList()

        // assert
        testObserver.assertNoValue()
    }

    @Test
    fun `test onPermissionDenied`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onClosePage)

        // act
        viewModel.onPermissionDenied()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickLater`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onClosePage)

        // act
        viewModel.onClickLater()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onPermissionGrantedResult`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onClosePage)

        // act
        viewModel.onPermissionGrantedResult()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    override fun `test checkFirstDisablePermission case permission STORAGE disable true`() {
        // arrange
        every { permissionDisableRepository.isDisableExternalStorage } returns true
        val testObserver = TestObserver.test(viewModel.onShowSettingDialog)

        // act
        viewModel.checkFirstDisablePermission(
            DetailDialogModel(
                nodePermission = NodePermission.STORAGE,
                iconType = DialogIconType.FOLDER,
                title = "title",
                subTitle = "subTitle"
            )
        )

        // assert
        verify(exactly = 1) { permissionDisableRepository.isDisableExternalStorage }
        testObserver.assertValue {
            assertEquals(NodePermission.STORAGE, it.nodePermission)
            assertEquals(DialogIconType.FOLDER, it.iconType)
            assertEquals("title", it.title)
            assertEquals("subTitle", it.subTitle)
            true
        }
    }

    @Test
    override fun `test checkFirstDisablePermission case permission STORAGE disable false`() {
        // arrange
        every { permissionDisableRepository.isDisableExternalStorage } returns false
        every { permissionDisableRepository.isDisableExternalStorage = true } just runs
        val testObserver = TestObserver.test(viewModel.onClosePage)

        // act
        viewModel.checkFirstDisablePermission(
            DetailDialogModel(
                nodePermission = NodePermission.STORAGE,
                iconType = DialogIconType.FOLDER,
                title = "title",
                subTitle = "subTitle"
            )
        )

        // assert
        verify(exactly = 1) { permissionDisableRepository.isDisableExternalStorage }
        verify(exactly = 1) { permissionDisableRepository.isDisableExternalStorage = true }
        testObserver.assertHasValue()
    }

    @Test
    override fun `test checkFirstDisablePermission case permission CONTACT disable true`() {
        // arrange
        every { permissionDisableRepository.isDisableReadContact } returns true
        val testObserver = TestObserver.test(viewModel.onShowSettingDialog)

        // act
        viewModel.checkFirstDisablePermission(
            DetailDialogModel(
                nodePermission = NodePermission.CONTACT,
                iconType = DialogIconType.CONTACT,
                title = "title",
                subTitle = "subTitle"
            )
        )

        // assert
        verify(exactly = 1) { permissionDisableRepository.isDisableReadContact }
        testObserver.assertValue {
            assertEquals(NodePermission.CONTACT, it.nodePermission)
            assertEquals(DialogIconType.CONTACT, it.iconType)
            assertEquals("title", it.title)
            assertEquals("subTitle", it.subTitle)
            true
        }
    }

    @Test
    override fun `test checkFirstDisablePermission case permission CONTACT disable false`() {
        // arrange
        every { permissionDisableRepository.isDisableReadContact } returns false
        every { permissionDisableRepository.isDisableReadContact = true } just runs
        val testObserver = TestObserver.test(viewModel.onClosePage)

        // act
        viewModel.checkFirstDisablePermission(
            DetailDialogModel(
                nodePermission = NodePermission.CONTACT,
                iconType = DialogIconType.CONTACT,
                title = "title",
                subTitle = "subTitle"
            )
        )

        // assert
        verify(exactly = 1) { permissionDisableRepository.isDisableReadContact }
        verify(exactly = 1) { permissionDisableRepository.isDisableReadContact = true }
        testObserver.assertHasValue()
    }

    @Test
    override fun `test getPermissionImage case success`() {
        // arrange
        viewModel.setStoreDetailDialogModel(
            DetailDialogModel(
                nodePermission = NodePermission.STORAGE,
                iconType = DialogIconType.FOLDER,
                title = "title",
                subTitle = "subTitle"
            )
        )
        coEvery {
            configPermissionImageUseCase.execute(any())
        } coAnswers {
            flowOf(Pair("button", "https://www.mock.com/permission-image-url.jpg"))
        }
        val testObserver = TestObserver.test(viewModel.onShowButtonAndImage)

        // act
        viewModel.getPermissionImage()

        // assert
        testObserver.assertValue {
            assertEquals("button", it.first)
            assertEquals("https://www.mock.com/permission-image-url.jpg", it.second)
            true
        }
    }

    @Test
    override fun `test checkFirstDisablePermission case other`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSettingDialog)
        val testObserver2 = TestObserver.test(viewModel.onClosePage)

        // act
        viewModel.checkFirstDisablePermission(
            DetailDialogModel(
                nodePermission = NodePermission.NULL,
                iconType = DialogIconType.INFO,
                title = "title",
                subTitle = "subTitle"
            )
        )

        // assert
        testObserver.assertNoValue()
        testObserver2.assertNoValue()
    }

    @Test
    override fun `test getPermissionImage case dialog model is null`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowButtonAndImage)

        // act
        viewModel.getPermissionImage()

        // assert
        testObserver.assertNoValue()
    }
}
