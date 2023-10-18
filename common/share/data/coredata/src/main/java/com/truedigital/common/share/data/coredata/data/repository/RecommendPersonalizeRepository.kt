package com.truedigital.common.share.data.coredata.data.repository

import com.truedigital.common.share.data.coredata.data.api.PersonalizeApiInterface
import com.truedigital.common.share.data.coredata.data.request.MovieYouMightAlsoLikeRequest
import com.truedigital.common.share.datalegacy.data.recommend.model.request.RecommendedDataRequest
import com.truedigital.common.share.datalegacy.data.recommend.model.response.RecommendedResponse
import com.truedigital.common.share.datalegacy.data.similar.SimilarResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface RecommendPersonalizeRepository {
    fun getShelfRecommendList(
        request: RecommendedDataRequest,
        contentType: String
    ): Flow<RecommendedResponse>

    fun getShelfYouMightAlsoLikeList(
        request: MovieYouMightAlsoLikeRequest
    ): Flow<RecommendedResponse>

    fun getSimilarShowList(
        request: MovieYouMightAlsoLikeRequest
    ): Flow<SimilarResponse>

    fun getSimilarMovieList(
        request: MovieYouMightAlsoLikeRequest
    ): Flow<SimilarResponse>
}

class RecommendPersonalizeRepositoryImpl @Inject constructor(
    private val api: PersonalizeApiInterface
) : RecommendPersonalizeRepository {

    companion object {
        const val MESSAGE_GET_SHELF_LIST_FAILED = "failed to load shelf list"
    }

    override fun getShelfRecommendList(
        request: RecommendedDataRequest,
        contentType: String
    ): Flow<RecommendedResponse> = flow {
        val response = api.getRecommendedShelf(
            contentType = contentType,
            deviceId = request.deviceId ?: "",
            ssoId = request.ssoId ?: "",
            maxItems = request.maxItems ?: "",
            language = request.language ?: "",
            isVodLayer = request.isVodLayer
        )

        val result = if (response.isSuccessful && response.body()?.items != null) {
            response.body() ?: RecommendedResponse()
        } else {
            error(MESSAGE_GET_SHELF_LIST_FAILED)
        }
        emit(result)
    }

    override fun getShelfYouMightAlsoLikeList(
        request: MovieYouMightAlsoLikeRequest
    ): Flow<RecommendedResponse> = flow {
        val response = api.getYouMightAlsoLikeShelf(
            ssoId = request.ssoId ?: "",
            deviceId = request.deviceId ?: "",
            globalItemId = request.globalItemId ?: "",
            maxItems = request.maxItems ?: "",
            contentRights = request.contentRights ?: "",
            language = request.language ?: ""
        )

        val result = if (response.isSuccessful && response.body()?.items != null) {
            response.body() ?: RecommendedResponse()
        } else {
            error(MESSAGE_GET_SHELF_LIST_FAILED)
        }
        emit(result)
    }

    override fun getSimilarShowList(
        request: MovieYouMightAlsoLikeRequest
    ): Flow<SimilarResponse> = flow {
        val response = api.getSimilarShow(
            ssoId = request.ssoId ?: "",
            deviceId = request.deviceId ?: "",
            globalItemId = request.globalItemId ?: "",
            maxItems = request.maxItems ?: "",
            contentRights = request.contentRights ?: "",
            language = request.language ?: "",
            isVodLayer = request.isVodLayer ?: "Y"
        )

        val result = if (response.isSuccessful && response.body()?.items != null) {
            response.body() ?: SimilarResponse()
        } else {
            error(MESSAGE_GET_SHELF_LIST_FAILED)
        }
        emit(result)
    }

    override fun getSimilarMovieList(
        request: MovieYouMightAlsoLikeRequest
    ): Flow<SimilarResponse> = flow {
        val response = api.getSimilarMovie(
            ssoId = request.ssoId ?: "",
            deviceId = request.deviceId ?: "",
            globalItemId = request.globalItemId ?: "",
            maxItems = request.maxItems ?: "",
            contentRights = request.contentRights ?: "",
            language = request.language ?: "",
            isVodLayer = request.isVodLayer
        )

        val result = if (response.isSuccessful && response.body()?.items != null) {
            response.body() ?: SimilarResponse()
        } else {
            error(MESSAGE_GET_SHELF_LIST_FAILED)
        }
        emit(result)
    }
}
