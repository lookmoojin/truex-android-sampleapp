package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.os.Bundle
import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.features.truecloudv3.common.FileCategoryType
import com.truedigital.features.truecloudv3.common.FileCategoryTypeManager
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic
import com.truedigital.features.truecloudv3.data.repository.FileRepositoryImpl
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.navigation.OptionFileSelectedToFileLocatorFragment
import com.truedigital.features.truecloudv3.navigation.router.FileTrueCloudRouterUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject
import javax.inject.Named

class OptionFileSelectedBottomSheetDialogViewModel @Inject constructor(
    private val fileTrueCloudRouterUseCase: FileTrueCloudRouterUseCase,
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface
) : ScopedViewModel() {

    val onDisableSelectFileMode = SingleLiveEvent<Boolean>()
    val onCheckCategoryType = SingleLiveEvent<Boolean>()
    val clickDelete = SingleLiveEvent<MutableList<TrueCloudFilesModel.File>>()
    private var selectedItemList = ArrayList<TrueCloudFilesModel.File>()
    private var folderId: String? = null

    @VisibleForTesting
    var categoryType = FileCategoryType.UNSUPPORTED_FORMAT

    fun init(
        itemList: ArrayList<TrueCloudFilesModel.File>,
        folderId: String,
        categoryType: String
    ) {
        selectedItemList = itemList
        this.folderId = folderId
        this.categoryType = FileCategoryTypeManager.getCategoryType(categoryType)
        onCheckCategoryType.value = this.categoryType == FileCategoryType.UNSUPPORTED_FORMAT
    }

    fun onClickMoveTo() {
        openPage(FileRepositoryImpl.MOVE)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_MOVE_TO
            )
        )
    }

    fun onClickCopy() {
        openPage(FileRepositoryImpl.COPY)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_COPY_TO
            )
        )
    }

    fun deleteSelectedItem() {
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_REMOVE_TO_TRASH
            )
        )
        val filterData = selectedItemList.filter { it.fileMimeType != FileMimeType.CONTACT }.toMutableList()
        clickDelete.value = filterData
    }

    fun onSetDisableSelectFileMode(isDisableSelectFileMode: Boolean) {
        onDisableSelectFileMode.value = isDisableSelectFileMode
    }

    fun openPage(type: String) {
        val data = selectedItemList.map { it.id }
        fileTrueCloudRouterUseCase.execute(
            OptionFileSelectedToFileLocatorFragment,
            Bundle().apply {
                putString(
                    TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_LOCATE_TYPE,
                    type
                )
                putStringArrayList(
                    TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_SELECTED,
                    ArrayList(data)
                )
                putString(
                    TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FOLDER_ID,
                    folderId
                )
                putString(
                    TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CATEGORY,
                    categoryType.type
                )
            }
        )
    }
}
