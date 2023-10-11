package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.truedigital.common.share.currentdate.usecase.GetCurrentDateTimeUseCase
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.domain.model.MigrationModel
import com.truedigital.features.truecloudv3.domain.usecase.MigrateDataUseCase
import com.truedigital.features.truecloudv3.domain.usecase.PatchMigrateStatusUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@ExtendWith(InstantTaskExecutorExtension::class)
class MigrateDataViewModelTest {
    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private val migrateDataUseCase: MigrateDataUseCase = mockk(relaxed = true)
    private val patchMigrateStatusUseCase: PatchMigrateStatusUseCase = mockk(relaxed = true)
    private val getCurrentDateTimeUseCase: GetCurrentDateTimeUseCase = mockk(relaxed = true)
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val contextDataProviderWrapper: ContextDataProviderWrapper = mockk(relaxed = true)
    private lateinit var viewModel: MigrateDataViewModel

    @BeforeEach
    fun setup() {
        viewModel = MigrateDataViewModel(
            coroutineDispatcher = coroutineDispatcher,
            migrateDataUseCase = migrateDataUseCase,
            patchMigrateStatusUseCase = patchMigrateStatusUseCase,
            contextDataProviderWrapper = contextDataProviderWrapper,
            getCurrentDateTimeUseCase = getCurrentDateTimeUseCase,
        )
    }

    @Test
    fun `test callMigrate case error`() {
        // arrange
        coEvery {
            migrateDataUseCase.execute()
        } coAnswers {
            flow { error(TrueCloudV3ErrorMessage.ERROR_MIGRATION_MIGRATE) }
        }
        val testObserver = TestObserver.test(viewModel.onMigrating)

        // act
        viewModel.callMigrate()

        // assert
        verify(exactly = 1) { migrateDataUseCase.execute() }
        testObserver.assertNoValue()
    }

    @Test
    fun `test callMigrate case success`() {
        // arrange
        val migrationModel = MigrationModel(
            status = "Migrating",
            estimatedTimeToComplete = "Time"
        )

        coEvery {
            migrateDataUseCase.execute()
        } coAnswers {
            flowOf(migrationModel)
        }
        every {
            getCurrentDateTimeUseCase.execute()
        } returns flowOf(1675755129)
        val testObserver = TestObserver.test(viewModel.onMigrating)

        // act
        viewModel.callMigrate()

        // assert
        verify(exactly = 1) { migrateDataUseCase.execute() }
        testObserver.assertHasValue()
    }

    @Test
    fun `test callMigrate case success estimatedTimeToComplete null`() {
        // arrange
        val migrationModel = MigrationModel(
            status = "Migrating"
        )

        coEvery {
            migrateDataUseCase.execute()
        } coAnswers {
            flowOf(migrationModel)
        }
        val testObserver = TestObserver.test(viewModel.onMigrating)

        // act
        viewModel.callMigrate()
        // assert
        verify(exactly = 1) { migrateDataUseCase.execute() }
        testObserver.assertValue(
            contextDataProviderWrapper.get().getDataContext()
                .getString(R.string.true_cloudv3_migration_time_remaining_na)
        )
    }

    @Test
    fun `test callMigrate case success estimatedTimeToComplete more than one hour`() {
        // arrange
        val migrationModel = MigrationModel(
            status = "Migrating",
            estimatedTimeToComplete = "2023-08-03T17:15:00.000Z"
        )

        coEvery {
            migrateDataUseCase.execute()
        } coAnswers {
            flowOf(migrationModel)
        }
        every {
            getCurrentDateTimeUseCase.execute()
        } returns flowOf(1675755129)

        // act
        viewModel.callMigrate()
        // assert
        verify(exactly = 1) { migrateDataUseCase.execute() }
    }

    @Test
    fun `test callMigrate case success estimatedTimeToComplete 1 minute`() {
        // arrange
        val migrationModel = MigrationModel(
            status = "Migrating",
            estimatedTimeToComplete = "2023-11-08T11:07:24.000Z"
        )

        coEvery {
            migrateDataUseCase.execute()
        } coAnswers {
            flowOf(migrationModel)
        }
        every {
            getCurrentDateTimeUseCase.execute()
        } returns flowOf(1675759644)

        // act
        viewModel.callMigrate()
        // assert
        verify(exactly = 1) { migrateDataUseCase.execute() }
    }

    @Test
    fun `test callMigrateCancel case error`() {
        // arrange
        coEvery {
            patchMigrateStatusUseCase.execute()
        } coAnswers {
            flow { error(TrueCloudV3ErrorMessage.ERROR_MIGRATION_UPDATE) }
        }
        val testObserver = TestObserver.test(viewModel.onMigrateCanceled)
        val testObserverFail = TestObserver.test(viewModel.onMigrateCancelFailed)

        // act
        viewModel.callMigrateCancel()

        // assert
        verify(exactly = 1) { patchMigrateStatusUseCase.execute() }
        testObserver.assertNoValue()
        testObserverFail.assertHasValue()
    }

    @Test
    fun `test callMigrateCancel case success`() {
        // arrange
        val migrationModel = MigrationModel(
            status = "Migrating",
            estimatedTimeToComplete = "Time"
        )

        coEvery {
            patchMigrateStatusUseCase.execute()
        } coAnswers {
            flowOf(migrationModel)
        }
        val testObserver = TestObserver.test(viewModel.onMigrateCanceled)
        val testObserverFail = TestObserver.test(viewModel.onMigrateCancelFailed)

        // act
        viewModel.callMigrateCancel()

        // assert
        verify(exactly = 1) { patchMigrateStatusUseCase.execute() }
        testObserver.assertHasValue()
        testObserverFail.assertNoValue()
    }
}
