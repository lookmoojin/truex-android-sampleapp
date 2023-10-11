package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.features.truecloudv3.common.MigrationStatus
import com.truedigital.features.truecloudv3.navigation.MainToMigrate
import com.truedigital.features.truecloudv3.navigation.SettingToMigrating
import com.truedigital.features.truecloudv3.navigation.router.SettingTrueCloudV3RouterUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class SettingTrueCloudV3ViewModelTest {
    private val router: SettingTrueCloudV3RouterUseCase = mockk()
    private val analyticManagerInterface: AnalyticManagerInterface = mockk()
    private lateinit var viewModel: SettingTrueCloudV3ViewModel

    @BeforeEach
    fun setUp() {
        viewModel = SettingTrueCloudV3ViewModel(
            router = router,
            analyticManagerInterface = analyticManagerInterface
        )
    }

    @Test
    fun `test Setting callMigrate success`() {
        // arrange
        every { router.execute(any()) } just runs

        // act
        viewModel.migrateClicked()

        // assert
        verify(exactly = 1) { router.execute(SettingToMigrating) }
    }

    @Test
    fun `test Setting callMigrate fail`() {
        // arrange
        every { router.execute(any()) } just runs

        // act
        viewModel.migrateClicked()

        // assert
        verify(exactly = 0) { router.execute(MainToMigrate) }
    }

    @Test
    fun `test Setting setMigrateStatus MIGRATING`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowMigratingStatus)
        // act
        viewModel.setMigrateStatus(MigrationStatus.MIGRATING.key)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test Setting setMigrateStatus PENDING`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowMigratePendingStatus)
        // act
        viewModel.setMigrateStatus(MigrationStatus.PENDING.key)

        // assert
        testObserver.assertHasValue()
    }
    @Test
    fun `test Setting setMigrateStatus MIGRATED`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onHideMigrationStatus)
        // act
        viewModel.setMigrateStatus(MigrationStatus.MIGRATED.key)

        // assert
        testObserver.assertHasValue()
    }
    @Test
    fun `test Setting setStorageDetail`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetStorage)
        // act
        viewModel.setStorageDetail("StorageDetail_test")

        // assert
        testObserver.assertHasValue()
    }
}
