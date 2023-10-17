package com.truedigital.features.apppermission.usecase

import android.Manifest
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.features.apppermission.data.repository.AppPermissionGetConfigRepository
import com.truedigital.features.apppermission.domain.model.AppPermissionConfigDataModel
import javax.inject.Inject

interface GetPermissionDataUseCase {
    fun execute(permissionNotAllowList: ArrayList<String>): MutableList<BannerBaseItemModel>
}

class GetPermissionDataUseCaseImpl @Inject constructor(
    private var appPermissionGetConfigRepository: AppPermissionGetConfigRepository
) : GetPermissionDataUseCase {

    override fun execute(permissionNotAllowList: ArrayList<String>): MutableList<BannerBaseItemModel> {

        val bannerPermission = mutableListOf<BannerBaseItemModel>()

        permissionNotAllowList.forEach { permission ->
            bannerPermission.add(
                BannerBaseItemModel().apply {
                    thum = mapDataPermission(
                        permission,
                        appPermissionGetConfigRepository.getAppPermissionData()
                    )
                }
            )
        }
        return bannerPermission.filterNot { it.thum.isEmpty() }.toMutableList()
    }

    private fun mapDataPermission(
        permission: String,
        appPermissionConfigDataModel: AppPermissionConfigDataModel
    ): String {
        val permissionMapList = hashMapOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE to appPermissionConfigDataModel.imageStorage,
            Manifest.permission.ACCESS_FINE_LOCATION to appPermissionConfigDataModel.imageLocation
        )
        return permissionMapList[permission] ?: ""
    }
}
