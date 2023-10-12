package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.ObjectInfoResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetTrueCloudObjectRepository {
    fun getObjectInfo(key: String): Flow<ObjectInfoResponse>
}

class GetTrueCloudObjectRepositoryImpl @Inject constructor(
    private val trueCloudV3Interface: TrueCloudV3Interface,
    private val userRepository: UserRepository
) : GetTrueCloudObjectRepository {
    override fun getObjectInfo(key: String): Flow<ObjectInfoResponse> {
        return flow {
            trueCloudV3Interface.getObjectInfo(
                ssoid = userRepository.getSsoId(),
                fileid = key,
            ).run {
                val responseBody = body()
                if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                } else {
                    error(code())
                }
            }
        }
    }
}
