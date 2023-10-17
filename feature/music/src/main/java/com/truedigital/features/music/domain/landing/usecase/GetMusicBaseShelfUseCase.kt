package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.common.share.data.coredata.data.repository.CmsShelvesRepository
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.landing.repository.CacheMusicLandingRepository
import com.truedigital.features.music.domain.model.MusicShelfItemType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

interface GetMusicBaseShelfUseCase {
    fun execute(shelfId: String): Flow<String>
}

class GetMusicBaseShelfUseCaseImpl @Inject constructor(
    private val cacheMusicLandingRepository: CacheMusicLandingRepository,
    private val cmsShelvesRepository: CmsShelvesRepository,
    private val localizationRepository: LocalizationRepository
) : GetMusicBaseShelfUseCase {

    companion object {
        private const val FIELD = "setting"
        private const val ERROR_CONDITION = "music base shelf error api path is empty"
    }

    override fun execute(shelfId: String): Flow<String> {
        cacheMusicLandingRepository.loadPathApiForYouShelf()?.let { cacheData ->
            return flowOf(cacheData)
        } ?: run {
            return cmsShelvesRepository.getCmsPublicContentShelfListData(
                shelfId = shelfId,
                country = localizationRepository.getAppCountryCode(),
                fields = FIELD
            ).map { data ->
                val settingValue = data.shelfList?.firstOrNull()?.setting

                if (settingValue?.apiName == MusicConstant.Key.SLUG_TUNED_GLOBAL &&
                    settingValue.type == MusicShelfItemType.BY_API.value &&
                    settingValue.api?.isNotEmpty() == true
                ) {
                    settingValue.api.orEmpty()
                } else {
                    error(ERROR_CONDITION)
                }
            }.onEach { apiPath ->
                cacheMusicLandingRepository.savePathApiForYouShelf(apiPath)
            }
        }
    }
}
