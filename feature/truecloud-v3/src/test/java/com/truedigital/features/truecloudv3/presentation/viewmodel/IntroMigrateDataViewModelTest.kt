package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.domain.model.MigrationModel
import com.truedigital.features.truecloudv3.domain.usecase.PatchMigrateStatusUseCase
import com.truedigital.features.truecloudv3.navigation.IntroMigrateToMigrating
import com.truedigital.features.truecloudv3.navigation.MainToMigrate
import com.truedigital.features.truecloudv3.navigation.router.IntroMigrateDataRouterUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertEquals

internal interface IntroMigrateDataViewModelTestCase {
    fun `test onClickLater case success`()
}

@ExtendWith(InstantTaskExecutorExtension::class)
internal class IntroMigrateDataViewModelTest : IntroMigrateDataViewModelTestCase {

    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    private val router: IntroMigrateDataRouterUseCase = mockk()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val patchMigrateStatusUseCase: PatchMigrateStatusUseCase = mockk()
    private lateinit var viewModel: IntroMigrateDataViewModel

    @BeforeEach
    fun setUp() {
        viewModel = IntroMigrateDataViewModel(
            router = router,
            coroutineDispatcher = coroutineDispatcher,
            patchMigrateStatusUseCase = patchMigrateStatusUseCase
        )
    }

    @Test
    override fun `test onClickLater case success`() {
        // arrange
        coEvery {
            patchMigrateStatusUseCase.execute()
        } coAnswers {
            flowOf(MigrationModel())
        }
        val testObserver = TestObserver.test(viewModel.onLaterClicked)

        // act
        viewModel.onClickLater()

        // assert
        verify(exactly = 1) { patchMigrateStatusUseCase.execute() }
        testObserver.assertValue {
            assertEquals(true, it)
            true
        }
    }

    @Test
    fun `test onClickLater case error`() {
        // arrange
        coEvery {
            patchMigrateStatusUseCase.execute()
        } coAnswers {
            flow { error(TrueCloudV3ErrorMessage.ERROR_MIGRATION_UPDATE) }
        }
        val testObserver = TestObserver.test(viewModel.onLaterClicked)

        // act
        viewModel.onClickLater()

        // assert
        verify(exactly = 1) { patchMigrateStatusUseCase.execute() }
        testObserver.assertNoValue()
    }

    @Test
    fun `test onClickMigrate success`() {
        // arrange
        every { router.execute(any()) } just runs

        // act
        viewModel.onClickMigrate()

        // assert
        verify(exactly = 1) { router.execute(IntroMigrateToMigrating) }
    }

    @Test
    fun `test onClickMigrate fail`() {
        // arrange
        every { router.execute(any()) } just runs

        // act
        viewModel.onClickMigrate()

        // assert
        verify(exactly = 0) { router.execute(MainToMigrate) }
    }
}
