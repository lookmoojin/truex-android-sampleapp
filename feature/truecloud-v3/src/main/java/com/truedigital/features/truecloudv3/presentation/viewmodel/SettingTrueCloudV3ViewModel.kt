package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.os.Bundle
import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticModel
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.features.truecloudv3.common.MigrationStatus
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic
import com.truedigital.features.truecloudv3.domain.model.DataUsageModel
import com.truedigital.features.truecloudv3.navigation.SettingToAutoBackup
import com.truedigital.features.truecloudv3.navigation.SettingToMigrating
import com.truedigital.features.truecloudv3.navigation.router.SettingTrueCloudV3RouterUseCase
import com.truedigital.features.truecloudv3.presentation.SettingTrueCloudV3Fragment
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject
import javax.inject.Named

class SettingTrueCloudV3ViewModel @Inject constructor(
    private val router: SettingTrueCloudV3RouterUseCase,
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface,
) : ScopedViewModel() {
    val onSetStorage = SingleLiveEvent<String>()
    val onShowMigratingStatus = SingleLiveEvent<Unit>()
    val onShowMigratePendingStatus = SingleLiveEvent<Unit>()
    val onHideMigrationStatus = SingleLiveEvent<Unit>()
    private var dataUsage: DataUsageModel? = null

    fun migrateClicked() {
        router.execute(SettingToMigrating)
    }

    fun autoBackupClick() {
        router.execute(
            SettingToAutoBackup,
            Bundle().apply {
                putParcelable(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_DATA_USAGE, dataUsage)
            }
        )
    }

    fun setMigrateStatus(migrateStatus: String) {
        if (migrateStatus.isNotEmpty()) {
            when (migrateStatus) {
                MigrationStatus.MIGRATING.key -> {
                    onShowMigratingStatus.value = Unit
                }
                MigrationStatus.PENDING.key,
                MigrationStatus.FAILED.key -> {
                    onShowMigratePendingStatus.value = Unit
                }
                else -> {
                    onHideMigrationStatus.value = Unit
                }
            }
        }
    }

    fun onViewCreated() {
        analyticManagerInterface.trackScreen(
            PlatformAnalyticModel().apply {
                screenClass = SettingTrueCloudV3Fragment::class.java.canonicalName as String
                screenName = TrueCloudV3TrackAnalytic.SCREEN_SETTING
            }
        )
    }

    fun setStorageDetail(storageDetail: String) {
        onSetStorage.value = storageDetail
    }

    fun setDataUsageDetail(storageData: DataUsageModel) {
        dataUsage = storageData
    }
}
