package com.truedigital.features.apppermission.data.repository

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.utils.SharedPrefsInterface
import com.truedigital.core.utils.get
import com.truedigital.core.utils.put
import com.truedigital.features.apppermission.constant.AppPermissionConstant.Key.PERMISSION_ANDROID_KEY
import com.truedigital.features.apppermission.constant.AppPermissionConstant.Key.PERMISSION_BUTTON_KEY
import com.truedigital.features.apppermission.constant.AppPermissionConstant.Key.PERMISSION_IMAGE_LOCATION_KEY
import com.truedigital.features.apppermission.constant.AppPermissionConstant.Key.PERMISSION_IMAGE_STORAGE_KEY
import com.truedigital.features.apppermission.constant.AppPermissionConstant.Key.PERMISSION_UI_KEY
import com.truedigital.features.apppermission.domain.model.AppPermissionConfigDataModel
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import javax.inject.Inject

interface AppPermissionGetConfigRepository {
    fun getAppPermissionData(): AppPermissionConfigDataModel
}

class AppPermissionGetConfigRepositoryImpl @Inject constructor(
    private val initialAppConfigRepository: InitialAppConfigRepository,
    private val localizationRepository: LocalizationRepository,
    private val sharedPrefsInterface: SharedPrefsInterface
) : AppPermissionGetConfigRepository {

    companion object {
        const val KEY_SAVE_IMAGE_LOCATION = "image_location"
        const val KEY_SAVE_IMAGE_STORAGE = "image_storage"
        const val KEY_SAVE_BUTTON = "save_button"
    }

    override fun getAppPermissionData(): AppPermissionConfigDataModel {
        val appPermissionConfigDataModel = AppPermissionConfigDataModel()
        val config = initialAppConfigRepository.getConfigByKey(PERMISSION_UI_KEY) as? Map<*, *>
        if (config != null) {
            val androidConfig = config[PERMISSION_ANDROID_KEY] as? Map<*, *>
            androidConfig?.let { _androidConfig ->
                val permissionData =
                    _androidConfig[localizationRepository.getAppLanguageCode()] as? Map<*, *>
                permissionData?.let { _permissionData ->
                    appPermissionConfigDataModel.button =
                        _permissionData[PERMISSION_BUTTON_KEY] as? String ?: ""
                    appPermissionConfigDataModel.imageLocation =
                        _permissionData[PERMISSION_IMAGE_LOCATION_KEY] as? String ?: ""
                    appPermissionConfigDataModel.imageStorage =
                        _permissionData[PERMISSION_IMAGE_STORAGE_KEY] as? String ?: ""
                    sharedPrefsInterface.put(
                        KEY_SAVE_BUTTON,
                        appPermissionConfigDataModel.button
                    )
                    sharedPrefsInterface.put(
                        KEY_SAVE_IMAGE_LOCATION,
                        appPermissionConfigDataModel.imageLocation
                    )
                    sharedPrefsInterface.put(
                        KEY_SAVE_IMAGE_STORAGE,
                        appPermissionConfigDataModel.imageStorage
                    )
                }
            }
        } else {
            val getButtonData = sharedPrefsInterface.get(KEY_SAVE_BUTTON, "")
            val getImageLocation = sharedPrefsInterface.get(KEY_SAVE_IMAGE_LOCATION, "")
            val getImageStorage = sharedPrefsInterface.get(KEY_SAVE_IMAGE_STORAGE, "")

            appPermissionConfigDataModel.button = getButtonData
            appPermissionConfigDataModel.imageLocation = getImageLocation
            appPermissionConfigDataModel.imageStorage = getImageStorage
        }
        return appPermissionConfigDataModel
    }
}
