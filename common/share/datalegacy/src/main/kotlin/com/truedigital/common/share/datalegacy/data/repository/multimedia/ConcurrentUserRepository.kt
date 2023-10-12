package com.truedigital.common.share.datalegacy.data.repository.multimedia

import androidx.annotation.VisibleForTesting
import com.truedigital.common.share.datalegacy.data.api.ccu.CcuApiInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ConcurrentUserRepository {
    fun getConcurrentUserFlow(isFetchFromRemote: Boolean): Flow<Map<String, Int>>
}

class ConcurrentUserRepositoryImpl @Inject constructor(private val ccuApi: CcuApiInterface) :
    ConcurrentUserRepository {
    companion object {
        private var concurrentMap: Map<String, Int> = mapOf()
    }

    override fun getConcurrentUserFlow(isFetchFromRemote: Boolean): Flow<Map<String, Int>> =
        flow {
            if (concurrentMap.isNotEmpty() && !isFetchFromRemote) {
                emit(concurrentMap)
            } else {
                val response = ccuApi.getCcuFlow()
                val responseBody = response.body()
                if (!response.isSuccessful || responseBody == null) {
                    emit(mapOf())
                } else {
                    concurrentMap = responseBody.ccu
                    emit(concurrentMap)
                }
            }
        }

    @VisibleForTesting
    fun setConcurrentMapData(ccuData: Map<String, Int>) {
        concurrentMap = ccuData
    }
}
