package com.truedigital.features.truecloudv3.presentation.viewmodel

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewModelScope
import com.truedigital.features.truecloudv3.R
import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.utils.DataStoreUtil
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic
import com.truedigital.features.truecloudv3.domain.model.DataUsageModel
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject
import javax.inject.Named

class AutoBackupViewModel @Inject constructor(
    private val dataStoreUtil: DataStoreUtil,
    private val contextDataProviderWrapper: ContextDataProviderWrapper,
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface
) : ScopedViewModel() {

    companion object {
        const val PACKAGE = "package"
        const val BACKUP_STATE_KEY = "backup_state"
        const val imageBackup = "image_backup"
        const val videoBackup = "video_backup"
        const val audioBackup = "audio_backup"
        const val otherBackup = "other_backup"
        const val lastTimeStamp = "last_time_stamp"

        @VisibleForTesting
        var backupState: Boolean = false
    }

    val onActiveBackupSuccess = SingleLiveEvent<String>()
    val onUpdateBackupState = SingleLiveEvent<Boolean>()
    val onGetBackupState = SingleLiveEvent<Boolean>()
    val onGetImageState = SingleLiveEvent<Boolean>()
    val onGetVideoState = SingleLiveEvent<Boolean>()
    val onGetAudioState = SingleLiveEvent<Boolean>()
    val onGetOtherState = SingleLiveEvent<Boolean>()
    val onShowStorage = SingleLiveEvent<DataUsageModel>()
    private val contentActive: HashMap<String, Boolean> = hashMapOf()

    fun init() {
        viewModelScope.launch {
            backupState =
                dataStoreUtil.getSinglePreference(stringPreferencesKey(BACKUP_STATE_KEY), "false")
                    .toBoolean()
            onUpdateBackupState.value = backupState
            onGetBackupState.value = backupState
            contentActive.apply {
                this[imageBackup] =
                    dataStoreUtil.getSinglePreference(stringPreferencesKey(imageBackup), "true")
                        .toBoolean()
                this[videoBackup] =
                    dataStoreUtil.getSinglePreference(stringPreferencesKey(videoBackup), "false")
                        .toBoolean()
                this[audioBackup] =
                    dataStoreUtil.getSinglePreference(stringPreferencesKey(audioBackup), "false")
                        .toBoolean()
                this[otherBackup] =
                    dataStoreUtil.getSinglePreference(stringPreferencesKey(otherBackup), "false")
                        .toBoolean()
            }
            getDataFromMap()
        }
    }

    fun setBackupState(isChecked: Boolean) {
        viewModelScope.launchSafe {
            dataStoreUtil.putPreference(
                stringPreferencesKey(BACKUP_STATE_KEY),
                isChecked.toString()
            )
            onUpdateBackupState.value = isChecked
            if (isChecked && !backupState) {
                onActiveBackupSuccess.value = contextDataProviderWrapper.get()
                    .getString(R.string.true_cloudv3_auto_backup_toast)
            } else {
                if (!contentActive.containsValue(true)) {
                    contentActive[imageBackup] = true
                    dataStoreUtil.putPreference(stringPreferencesKey(imageBackup), "true")
                    onGetImageState.value = true
                }
            }
            backupState = false
            trackEvent(isChecked)
        }
    }

    fun setContentBackUpState(key: String, isChecked: Boolean) {
        viewModelScope.launchSafe {
            dataStoreUtil.putPreference(stringPreferencesKey(key), isChecked.toString())
            contentActive[key] = isChecked
            analyticManagerInterface.trackEvent(
                hashMapOf(
                    MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_BACKUP,
                    TrueCloudV3TrackAnalytic.PARAMS_FILE_TYPE to key.split("_").first(),
                    TrueCloudV3TrackAnalytic.METHOD to
                            if (isChecked) TrueCloudV3TrackAnalytic.METHOD_ON else TrueCloudV3TrackAnalytic.METHOD_OFF
                )
            )
        }
    }

    fun setDataUsage(data: DataUsageModel) {
        onShowStorage.value = data
    }

    private fun getDataFromMap() {
        onGetImageState.value = contentActive[imageBackup]
        onGetVideoState.value = contentActive[videoBackup]
        onGetAudioState.value = contentActive[audioBackup]
        onGetOtherState.value = contentActive[otherBackup]
    }

    private fun trackEvent(isChecked: Boolean) {
        if (!isChecked) {
            analyticManagerInterface.trackEvent(
                hashMapOf(
                    MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_BACKUP,
                    TrueCloudV3TrackAnalytic.PARAMS_FILE_TYPE to TrueCloudV3TrackAnalytic.LINK_DESC_ALL_FILES,
                    TrueCloudV3TrackAnalytic.METHOD to TrueCloudV3TrackAnalytic.METHOD_OFF
                )
            )
        } else {
            val fileType = arrayListOf(imageBackup, videoBackup, audioBackup, otherBackup)
            fileType.forEach {
                viewModelScope.launchSafe {
                    analyticManagerInterface.trackEvent(
                        hashMapOf(
                            MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_BACKUP,
                            TrueCloudV3TrackAnalytic.PARAMS_FILE_TYPE to it.split("_").first(),
                            TrueCloudV3TrackAnalytic.METHOD to
                                    if (dataStoreUtil.getSinglePreference(
                                            stringPreferencesKey(it),
                                            "false"
                                        ).toBoolean()
                                    ) {
                                        TrueCloudV3TrackAnalytic.METHOD_ON
                                    } else {
                                        TrueCloudV3TrackAnalytic.METHOD_OFF
                                    }
                        )
                    )
                }
            }
        }
    }
}
