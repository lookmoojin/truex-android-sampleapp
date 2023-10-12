package com.truedigital.common.share.datalegacy.data.api.ice

import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.request.add.AddDeviceEntitlementRequest
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.request.delete.DeleteDeviceEntitlementRequest
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.add.AddDeviceEntitlementResponse
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.delete.RemoveDeviceEntitlementResponse
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.get.GetDeviceEntitlementResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path

interface IceApiInterface {
    /***********************************************************************************************
     * ice-entitlement
     **********************************************************************************************/

    @POST("ice-entitlement/v3/ice/etmt")
    fun addDeviceEntitlement(
        @Body addDeviceEntitlementRequest: AddDeviceEntitlementRequest
    ): Single<Response<AddDeviceEntitlementResponse>>

    @GET("ice-entitlement/v3/ice/etmt/{ssoId}")
    fun getDeviceEntitlement(
        @Path("ssoId") ssoId: String
    ): Single<Response<GetDeviceEntitlementResponse>>

    @HTTP(method = "DELETE", path = "ice-entitlement/v3/ice/etmt/soft/{ssoId}", hasBody = true)
    fun removeDeviceEntitlement(
        @Path("ssoId") ssoId: String,
        @Body deviceEntitlementRequest: DeleteDeviceEntitlementRequest
    ): Single<RemoveDeviceEntitlementResponse>
}
