package com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.repository

import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.api.WeMallShelfApiInterface
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfRequestModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface WeMallShelfRepository {
    fun getWeMallShelfComponent(
        token: String,
        request: WeMallShelfRequestModel
    ): Flow<WeMallShelfResponseModel?>
}

class WeMallShelfRepositoryImpl @Inject constructor(private val weMallShelfApi: WeMallShelfApiInterface) :
    WeMallShelfRepository {
    override fun getWeMallShelfComponent(
        token: String,
        request: WeMallShelfRequestModel
    ): Flow<WeMallShelfResponseModel?> {
        return flow {
            val responseModel = weMallShelfApi.getWeMallShelfComponent(
                authorization = token,
                deviceId = request.deviceID,
                ssoId = request.ssoID,
                categoryName = request.categoryName,
                lang = request.lang,
                limit = request.limit
            )
            responseModel.code?.let { _code ->
                if (_code == "10001") {
                    if (responseModel.data?.get(0)?.items.isNullOrEmpty()) {
                        emit(null)
                    } else {
                        emit(responseModel)
                    }
                } else {
                    emit(null)
                }
            } ?: run {
                emit(null)
            }
        }
    }
}
