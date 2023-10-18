package com.truedigital.common.share.datalegacy.data.repository.entitlement.repository

import com.truedigital.common.share.datalegacy.data.api.ice.IceApiInterface
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.entity.DeviceEntitlement
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.request.add.AddDeviceEntitlementRequest
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.request.delete.DeleteDeviceEntitlementRequest
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.add.AddDeviceEntitlementResponse
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.delete.RemoveDeviceEntitlementResponse
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.get.GetDeviceEntitlementResponse
import io.reactivex.Single
import retrofit2.Response
import javax.inject.Inject

interface DeviceEntitlementRepository {
    fun addDeviceEntitlement(addDeviceEntitlementRequest: AddDeviceEntitlementRequest): Single<Response<AddDeviceEntitlementResponse>>
    fun getDeviceEntitlementList(ssoId: String): Single<GetDeviceEntitlementResponse>
    fun removeDeviceEntitlementList(
        ssoId: String,
        deviceEntitlementRequest: DeleteDeviceEntitlementRequest
    ): Single<RemoveDeviceEntitlementResponse>

    fun loadActiveDeviceCache(): Single<DeviceEntitlement>
    fun saveActiveDeviceCache(deviceEntitlementList: DeviceEntitlement)
}

class DeviceEntitlementRepositoryImpl @Inject constructor(
    private val api: IceApiInterface
) : DeviceEntitlementRepository {

    companion object {
        var deviceEntitlement: DeviceEntitlement = DeviceEntitlement()
    }

    override fun addDeviceEntitlement(addDeviceEntitlementRequest: AddDeviceEntitlementRequest): Single<Response<AddDeviceEntitlementResponse>> {
        return api.addDeviceEntitlement(addDeviceEntitlementRequest)
    }

    override fun getDeviceEntitlementList(ssoId: String): Single<GetDeviceEntitlementResponse> {
        return api.getDeviceEntitlement(ssoId).map { response ->
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Throwable(DeviceEntitlementErrorCode.ERROR_GET_DEVICE_LIST_FAIL.errorCode)
            }
        }
    }

    override fun removeDeviceEntitlementList(
        ssoId: String,
        deviceEntitlementRequest: DeleteDeviceEntitlementRequest
    ): Single<RemoveDeviceEntitlementResponse> {
        return api.removeDeviceEntitlement(ssoId, deviceEntitlementRequest)
    }

    override fun loadActiveDeviceCache(): Single<DeviceEntitlement> {
        return Single.just(deviceEntitlement)
    }

    override fun saveActiveDeviceCache(activeDeviceEntitlement: DeviceEntitlement) {
        deviceEntitlement = activeDeviceEntitlement
    }
}
