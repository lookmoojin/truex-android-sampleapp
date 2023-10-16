package com.truedigital.common.share.data.coredata.data.repository

import com.newrelic.agent.android.NewRelic
import com.truedigital.common.share.data.coredata.data.api.SimilarApiInterface
import com.truedigital.common.share.data.coredata.data.exception.EmptyListError
import com.truedigital.common.share.data.coredata.data.model.PersonalizeSimilarResponse
import com.truedigital.common.share.data.coredata.data.model.RequestQuerySimilar
import com.truedigital.common.share.data.coredata.domain.model.SimilarRequestModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ShortPersonalizeRepository {
    fun getShortData(similarRequestModel: SimilarRequestModel): Flow<PersonalizeSimilarResponse.Data>
}

class ShortPersonalizeRepositoryImpl @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val similarApiInterface: SimilarApiInterface,
    private val localizationRepository: LocalizationRepository
) : ShortPersonalizeRepository {
    override fun getShortData(similarRequestModel: SimilarRequestModel): Flow<PersonalizeSimilarResponse.Data> {
        return flow {
            val deviceId = deviceRepository.getAndroidId()
            val placementId = similarRequestModel.placementId
            val country = localizationRepository.getAppCountryCode()
            val lang = localizationRepository.getAppLanguageCodeForEnTh()
            val limit = similarRequestModel.limit
            val contentId = similarRequestModel.contentId
            val querySimilar =
                "query {short(placement_id:\"$placementId\",country:\"$country\",lang:\"$lang\"" +
                    ",content_id:\"$contentId\",device_id:\"$deviceId\",limit:$limit){id title detail tags status " +
                    "publish_date expire_date source_url navigate count_likes count_views " +
                    "create_by create_by_ssoid article_category setting genres " +
                    "partner_related{ id title detail content_type thumb thumb_list navigate " +
                    "publish_date expire_date} " +
                    "relate_content{ id title detail content_type thumb thumb_list navigate " +
                    "publish_date expire_date setting} " +
                    "content_type thumb_list thumb}}"
            val response =
                similarApiInterface.getSimilarData(RequestQuerySimilar(query = querySimilar))
            if (response.isSuccessful && response.body() != null) {
                response.body()?.responseData?.let { similarData ->
                    emit(similarData)
                }
            } else {
                val handlingExceptionMap = mapOf(
                    "Key" to "PersonalizeSimilarRepository",
                    "Value" to "Response or Map Data Error"
                )

                NewRelic.recordHandledException(Exception(response.message()), handlingExceptionMap)
                throw EmptyListError()
            }
        }
    }
}
