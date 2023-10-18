package com.truedigital.common.share.datalegacy.data.repository.entitlement

import com.truedigital.common.share.datalegacy.data.api.ice.IceApiInterface
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.entity.DeviceEntitlement
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.get.GetDeviceEntitlementResponse
import com.truedigital.common.share.datalegacy.data.repository.entitlement.repository.DeviceEntitlementRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response

internal class DeviceEntitlementRepositoryTest {

    private lateinit var deviceEntitlementRepository: DeviceEntitlementRepositoryImpl
    private lateinit var api: IceApiInterface

    @BeforeEach
    fun setup() {
        api = mockk()
        deviceEntitlementRepository = DeviceEntitlementRepositoryImpl(api)
    }

    @Test
    fun `getDeviceEntitlementList returns expected response`() {
        val ssoId = "123"
        val responseBody =
            "{\"data\": {\"devices\": [{\"id\": \"1\", \"name\": \"Device 1\"}]}}".toResponseBody("application/json".toMediaTypeOrNull())
        val successResponse =
            Response.success(GetDeviceEntitlementResponse().apply { responseBody })
        every { api.getDeviceEntitlement(ssoId) } returns Single.just(successResponse)

        val result = deviceEntitlementRepository.getDeviceEntitlementList(ssoId).test()

        result.assertValue { true }
    }

    @Test
    fun `getDeviceEntitlementList throws error when response is unsuccessful`() {
        val ssoId = "123"
        val errorResponse =
            Response.error<GetDeviceEntitlementResponse>(404, "Not Found".toResponseBody(null))
        every { api.getDeviceEntitlement(ssoId) } returns Single.just(errorResponse)

        val result = deviceEntitlementRepository.getDeviceEntitlementList(ssoId).test()

        result.assertError(Throwable::class.java)
    }

    @Test
    fun `saveActiveDeviceCache sets deviceEntitlement`() {
        val deviceEntitlement = DeviceEntitlement().apply {
            listOf(
                DeviceEntitlement().apply {
                    limitDevice = 1
                    activeDeviceEntitlementList = mutableListOf()
                }
            )
        }
        deviceEntitlementRepository.saveActiveDeviceCache(deviceEntitlement)

        assertEquals(deviceEntitlement, DeviceEntitlementRepositoryImpl.deviceEntitlement)
    }
}
