package com.truedigital.common.share.data.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.data.coredata.data.api.CmsContentApiInterface
import com.truedigital.common.share.data.coredata.data.model.response.ContentDetailData
import com.truedigital.common.share.data.coredata.data.model.response.ContentDetailResponse
import com.truedigital.common.share.data.coredata.data.repository.CmsContentRepository
import com.truedigital.common.share.data.coredata.data.repository.CmsContentRepositoryImpl
import com.truedigital.common.share.datalegacy.data.base.Failure
import com.truedigital.common.share.datalegacy.data.base.Success
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response

internal class CmsContentRepositoryImplTest {

    private lateinit var cmsContentRepository: CmsContentRepository
    private val cmsContentApiInterface: CmsContentApiInterface = mock()

    @BeforeEach
    fun setUp() {
        cmsContentRepository = CmsContentRepositoryImpl(cmsContentApiInterface)
    }

    @Test
    fun getContentDetail_expandIsNotNullOrEmpty_success_returnContentDetailData() =
        runTest {
            val responseData = ContentDetailResponse().apply {
                this.code = 200
                this.data = ContentDetailData().apply {
                    this.id = "id"
                    this.detail = "detail"
                    this.thumb = "thumb"
                }
            }

            whenever(cmsContentApiInterface.getCmsContentDetails(any(), any(), any(), any(), any()))
                .thenReturn(Response.success(responseData))

            val flow = cmsContentRepository.getContentDetail(
                "cmsId",
                "field",
                "country",
                "lang",
                "expand"
            )
            flow.collect { resultResponse ->
                val contentDetail = resultResponse as? Success
                assertEquals(contentDetail?.data?.id, responseData.data?.id)
                assertEquals(contentDetail?.data?.detail, responseData.data?.detail)
                assertEquals(contentDetail?.data?.thumb, responseData.data?.thumb)
            }
        }

    @Test
    fun getContentDetail_expandIsNotNullOrEmpty_success_responseDataIsNull_returnError() =
        runTest {
            val responseData = ContentDetailResponse()

            whenever(cmsContentApiInterface.getCmsContentDetails(any(), any(), any(), any(), any()))
                .thenReturn(Response.success(responseData))

            val flow =
                cmsContentRepository.getContentDetail("cmsId", "field", "country", "lang", "expand")
            flow.collect {
                val resultResponse = it as? Failure
                assertNotNull(resultResponse)
                assertEquals(
                    resultResponse?.exception?.message,
                    CmsContentRepositoryImpl.MESSAGE_CONTENT_DETAIL_FAILED
                )
            }
        }

    @Test
    fun getContentDetail_fail_returnError() = runTest {
        val responseBody =
            "{\"code\":400,\"status\":400,\"error\":null,\"message\":\"unexpected error\"}"
                .toResponseBody("application/json".toMediaTypeOrNull())

        val responseRaw = okhttp3.Response.Builder()
            .code(400)
            .message("unexpected error")
            .request(Request.Builder().url("http://example.com").build())
            .protocol(Protocol.HTTP_1_0)
            .build()

        whenever(cmsContentApiInterface.getCmsContentDetails(any(), any(), any(), any(), any()))
            .thenReturn(Response.error(responseBody, responseRaw))

        val flow = cmsContentRepository.getContentDetail("cmsId", "field", "country", "lang", "")
        flow.collect {
            val resultResponse = it as? Failure
            assertNotNull(resultResponse)
            assertEquals(
                resultResponse?.exception?.message,
                CmsContentRepositoryImpl.MESSAGE_CONTENT_DETAIL_FAILED
            )
        }
    }
}
