package com.truedigital.features.truecloudv3.presentation.viewmodel

import androidx.datastore.preferences.core.stringPreferencesKey
import com.jraska.livedata.TestObserver
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.utils.DataStoreUtil
import com.truedigital.core.utils.SharedPrefsUtils
import com.truedigital.features.truecloudv3.domain.model.DataUsageModel
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(InstantTaskExecutorExtension::class)
class AutoBackupViewModelTest {

    private val sharedPrefsUtils: SharedPrefsUtils = mockk(relaxed = true)
    private val contextDataProviderWrapper: ContextDataProviderWrapper = mockk(relaxed = true)
    private val analyticManagerInterface: AnalyticManagerInterface = mockk(relaxed = true)
    private val dataStoreUtil: DataStoreUtil = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private val dispatcher = TestCoroutineScheduler()
    private val testScope = TestScope(dispatcher)
    private lateinit var viewModel: AutoBackupViewModel

    @BeforeEach
    fun setUp() {
        viewModel = AutoBackupViewModel(
            contextDataProviderWrapper = contextDataProviderWrapper,
            analyticManagerInterface = analyticManagerInterface,
            dataStoreUtil = dataStoreUtil
        )
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `test init success`() = runTest {

        // arrange
        coEvery { dataStoreUtil.getSinglePreference(stringPreferencesKey(AutoBackupViewModel.BACKUP_STATE_KEY), "false") } returns "true"
        coEvery { dataStoreUtil.getSinglePreference(stringPreferencesKey(AutoBackupViewModel.imageBackup), "true") } returns "true"
        coEvery { dataStoreUtil.getSinglePreference(stringPreferencesKey(AutoBackupViewModel.videoBackup), "false") } returns "true"
        coEvery { dataStoreUtil.getSinglePreference(stringPreferencesKey(AutoBackupViewModel.audioBackup), "false") } returns "true"
        coEvery { dataStoreUtil.getSinglePreference(stringPreferencesKey(AutoBackupViewModel.otherBackup), "false") } returns "true"

        // act
        viewModel.init()

        // assert
        assertEquals(false, AutoBackupViewModel.backupState)
    }

    @Test
    fun `test setBackupState is true success`() = runTest {
        // act
        viewModel.setBackupState(true)
        // assert
        assertEquals(false, AutoBackupViewModel.backupState)
    }

    @Test
    fun `test setBackupState is false success`() = runTest {
        // act
        testScope.launchSafe {
            viewModel.setBackupState(false)
        }
        // assert
        assertEquals(false, AutoBackupViewModel.backupState)
    }

    @Test
    fun `test setDataUsage success`() = runTest {
        val testObserver = TestObserver.test(viewModel.onShowStorage)
        val data = DataUsageModel(
            images = 10L,
            videos = 2L
        )
        // act
        viewModel.setDataUsage(data)

        // assert
        testObserver.assertValue(data)
    }
}
