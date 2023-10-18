package com.truedigital.common.share.datalegacy.data.api.ccu

import com.truedigital.common.share.datalegacy.data.multimedia.model.CcuResponse
import retrofit2.Response
import retrofit2.http.GET

interface CcuApiInterface {
    @GET("hccu/get_ccu.php")
    suspend fun getCcuFlow(): Response<CcuResponse>
}
