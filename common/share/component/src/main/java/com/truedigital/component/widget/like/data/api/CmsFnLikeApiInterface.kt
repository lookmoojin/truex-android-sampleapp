package com.truedigital.component.widget.like.data.api

import com.truedigital.component.widget.like.data.model.request.LikeDataRequest
import com.truedigital.component.widget.like.data.model.response.LikeResponse
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CmsFnLikeApiInterface {

    @GET("cms-fnlike/v1/islike/")
    fun getStateLike(
        @Query("id") cmsId: String,
        @Query("ssoid") ssoId: String
    ): Observable<LikeResponse>

    @POST("cms-fnlike/v1/like")
    fun postLike(@Body request: LikeDataRequest): Completable
}
