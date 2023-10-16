package com.truedigital.common.share.data.coredata.data.repository

import com.truedigital.common.share.data.coredata.data.api.CmsContentApiInterface
import com.truedigital.common.share.data.coredata.data.model.response.ContentDetailData
import com.truedigital.common.share.datalegacy.data.base.Failure
import com.truedigital.common.share.datalegacy.data.base.ResultResponse
import com.truedigital.common.share.datalegacy.data.base.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface CmsContentRepository {
    fun getContentDetail(
        cmsId: String,
        fields: String,
        country: String,
        lang: String,
        expand: String
    ): Flow<ResultResponse<ContentDetailData>>
}

class CmsContentRepositoryImpl @Inject constructor(private val api: CmsContentApiInterface) : CmsContentRepository {

    companion object {
        const val MESSAGE_CONTENT_DETAIL_FAILED = "failed to load content detail"
    }

    override fun getContentDetail(
        cmsId: String,
        fields: String,
        country: String,
        lang: String,
        expand: String
    ): Flow<ResultResponse<ContentDetailData>> {
        return flow {
            emit(
                api.getCmsContentDetails(
                    cmsId = cmsId,
                    country = country,
                    lang = lang,
                    fields = fields,
                    expand = expand
                ).run {
                    when {
                        !isSuccessful || body() == null || body()?.data == null -> {
                            Failure(Throwable(MESSAGE_CONTENT_DETAIL_FAILED))
                        }
                        else -> {
                            body()?.data?.let {
                                Success((it))
                            } ?: Failure(Throwable(MESSAGE_CONTENT_DETAIL_FAILED))
                        }
                    }
                }
            )
        }
    }
}
