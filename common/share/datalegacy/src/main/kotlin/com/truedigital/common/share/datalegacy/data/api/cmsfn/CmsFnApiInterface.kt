package com.truedigital.common.share.datalegacy.data.api.cmsfn

import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.CmsShelfResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CmsFnApiInterface {

    @GET("cms-fnshelf/v1/{shelf_id}")
    fun getCmsShelfList(
        @Path("shelf_id") shelfId: String,
        @Query("fields") fields: String
    ): Observable<Response<CmsShelfResponse>>

    /** cms-fncounter
     ************************************************************************************************/

    @GET("cms-fncounter/v1/count")
    suspend fun getCountView(@Query("id") id: String): Response<Unit>
}
