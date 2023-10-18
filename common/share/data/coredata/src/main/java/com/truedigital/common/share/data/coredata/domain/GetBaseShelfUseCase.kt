package com.truedigital.common.share.data.coredata.domain

import com.truedigital.common.share.data.coredata.data.repository.CmsShelvesRepository
import com.truedigital.common.share.data.coredata.domain.constant.ShelfType
import com.truedigital.common.share.data.coredata.domain.model.NavigationBaseShelfModel
import com.truedigital.common.share.data.coredata.domain.model.NavigationModel
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Data
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Shelf
import com.truedigital.core.data.device.model.LocalizationModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.device.repository.localization
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetBaseShelfUseCase {
    fun execute(shelfId: String): Flow<NavigationBaseShelfModel>
}

open class GetBaseShelfUseCaseImpl @Inject constructor(
    private val cmsShelvesRepository: CmsShelvesRepository,
    private val localizationRepository: LocalizationRepository
) :
    GetBaseShelfUseCase {
    companion object {
        private const val fields = "setting,thumb_list"
    }

    override fun execute(shelfId: String): Flow<NavigationBaseShelfModel> {
        return if (shelfId.isEmpty()) {
            flowOf(NavigationBaseShelfModel())
        } else {
            cmsShelvesRepository.getCmsShelfListData(
                shelfId = shelfId,
                country = localizationRepository.getAppCountryCode(),
                fields = fields
            ).map(::mapShelfData)
        }
    }

    private fun mapShelfData(data: Data): NavigationBaseShelfModel {
        val title = getLocalizationFromSetting(data.setting)
        val navItems = mutableListOf<NavigationModel>()
        data.shelfList?.forEach { shelf ->
            if (isTopNavShelf(shelf)) {
                val shelfId = shelf.id.orEmpty()
                val shelfTitle = getLocalizationFromSetting(shelf.setting)
                val shelfSlug = shelf.setting?.shelfSlug.orEmpty()
                val shelfNavigate = shelf.setting?.navigate.orEmpty()
                if (shelfTitle.isNotEmpty() && shelfId.isNotEmpty()) {
                    navItems.add(
                        NavigationModel().apply {
                            this.deeplinkTopNav = shelf.setting?.deeplinkTopNavigation.orEmpty()
                            this.productGroupCode = shelf.setting?.productGroupCode.orEmpty()
                            this.title = shelfTitle
                            this.shelfId = shelfId
                            this.shelfCode = shelf.setting?.shelfCode.orEmpty()
                            this.shelfSlug = shelfSlug
                            this.navigate = shelfNavigate
                            this.titleEn = shelf.setting?.titleEn.orEmpty()
                            this.type = shelf.setting?.type.orEmpty()
                            this.viewType = shelf.setting?.viewType.orEmpty()
                            this.movieType = shelf.setting?.movieType.orEmpty()
                            this.contentRights = shelf.setting?.contentRights.orEmpty()
                            this.stickyBannerId = shelf.setting?.stickyBannerId.orEmpty()
                            this.stickyBannerSlug = shelf.setting?.stickyBannerSlug.orEmpty()
                        }
                    )
                }
            }
        }
        if (navItems.isEmpty()) {
            error("Empty nav list")
        }

        return NavigationBaseShelfModel().apply {
            this.title = title
            this.navItems = navItems
            this.shelfSlug = data.setting?.shelfSlug.orEmpty()
            this.titleEn = data.setting?.titleEn.orEmpty()
            this.headerLogo = data.setting?.headerLogo.orEmpty()
            this.trueidPlusOnboardingShelfId = data.setting?.trueidPlusOnboardingShelfId.orEmpty()
            this.trueidPlusSubscribePartnerCode =
                data.setting?.trueidPlusSubscribePartnerCode.orEmpty()
            this.trueidPlusSubscribeProductCode =
                data.setting?.trueidPlusSubscribeProductCode.orEmpty()
        }
    }

    private fun getLocalizationFromSetting(setting: Setting?): String {
        return setting?.let {
            localizationRepository.localization(
                LocalizationModel().apply {
                    enWord = setting.titleEn.orEmpty()
                    idWord = setting.titleId.orEmpty()
                    kmWord = setting.titleKm.orEmpty()
                    myWord = setting.titleMy.orEmpty()
                    phWord = setting.titleFil.orEmpty()
                    thWord = setting.titleTh.orEmpty()
                    vnWord = setting.titleVi.orEmpty()
                }
            )
        } ?: ""
    }

    private fun isTopNavShelf(shelf: Shelf): Boolean {
        return (
            shelf.setting?.viewType == ShelfType.VIEW_TYPE_TRUE_ID_TOP_NAV &&
                shelf.setting?.type == ShelfType.TYPE_BY_SHELF_ID
            ) ||
            (
                (
                    shelf.setting?.viewType == ShelfType.VIEW_TYPE_VERTICAL ||
                        shelf.setting?.viewType == ShelfType.VIEW_TYPE_HORIZONTAL
                    ) &&
                    shelf.setting?.type == ShelfType.TYPE_BY_CATE
                )
    }
}
