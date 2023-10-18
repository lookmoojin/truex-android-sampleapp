package com.truedigital.component.widget.livecommerce.data.repository

import com.truedigital.component.widget.livecommerce.data.api.AmityActiveLiveStreamApi
import com.truedigital.component.widget.livecommerce.data.model.CommerceActiveLiveStreamData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetActiveLiveStreamRepository {
    fun execute(userIds: String): Flow<List<CommerceActiveLiveStreamData>>
}

class GetActiveLiveStreamRepositoryImpl @Inject constructor(
    private val amityActiveLiveStreamApi: AmityActiveLiveStreamApi
) : GetActiveLiveStreamRepository {
    companion object {
        const val ERROR_UNKNOWN_API = "Unknown API Exception"
    }

    override fun execute(userIds: String): Flow<List<CommerceActiveLiveStreamData>> {
        return flow {
            val response = amityActiveLiveStreamApi.getLiveGroup(userIds = userIds)
            if (response.isSuccessful) {
                response.body()?.data?.let { listData ->
                    emit(listData.filterNotNull())
                } ?: run {
                    emit(listOf())
                }
            } else {
                error(ERROR_UNKNOWN_API)
            }
        }
    }
}
