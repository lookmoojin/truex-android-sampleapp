package com.truedigital.common.share.componentv3.widget.searchanimation.usecase

import com.truedigital.common.share.componentv3.widget.searchanimation.constant.SearchAnimationConstant.Key.SEARCHANIMATION_ADS_KEY
import com.truedigital.common.share.componentv3.widget.searchanimation.constant.SearchAnimationConstant.Key.SEARCHANIMATION_ADS_URL_KEY
import com.truedigital.common.share.componentv3.widget.searchanimation.constant.SearchAnimationConstant.Key.SEARCHANIMATION_DEEPLINK_KEY
import com.truedigital.common.share.componentv3.widget.searchanimation.constant.SearchAnimationConstant.Key.SEARCHANIMATION_END_TIME_KEY
import com.truedigital.common.share.componentv3.widget.searchanimation.constant.SearchAnimationConstant.Key.SEARCHANIMATION_SEARCH_KEY
import com.truedigital.common.share.componentv3.widget.searchanimation.constant.SearchAnimationConstant.Key.SEARCHANIMATION_START_TIME_KEY
import com.truedigital.common.share.componentv3.widget.searchanimation.model.SearchAnimationData
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import io.reactivex.Single
import javax.inject.Inject

interface GetSearchAnimationUseCase {
    fun execute(pageKey: String): Single<SearchAnimationData>
}

class GetSearchAnimationUseCaseImpl @Inject constructor(
    private val initialAppConfigRepository: InitialAppConfigRepository
) : GetSearchAnimationUseCase {
    override fun execute(pageKey: String): Single<SearchAnimationData> {
        val searchAnimation = SearchAnimationData()
        val config =
            initialAppConfigRepository.getConfigByKey(SEARCHANIMATION_SEARCH_KEY) as? Map<*, *>
        config?.let { _config ->
            val searchAnimationConfig =
                (_config[SEARCHANIMATION_ADS_KEY] as? Map<*, *>)?.get(pageKey) as? Map<*, *>
            searchAnimation.apply {
                adsUrl = searchAnimationConfig?.get(SEARCHANIMATION_ADS_URL_KEY) as? String ?: ""
                deeplink = searchAnimationConfig?.get(SEARCHANIMATION_DEEPLINK_KEY) as? String ?: ""
                searchAnimationTime.startDate =
                    searchAnimationConfig?.get(SEARCHANIMATION_START_TIME_KEY) as? String ?: ""
                searchAnimationTime.endDate =
                    searchAnimationConfig?.get(SEARCHANIMATION_END_TIME_KEY) as? String ?: ""
            }
        }
        return Single.just(searchAnimation)
    }
}
