package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.StorageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetStorageSpaceRepository {
    fun getStorage(): Flow<StorageResponse>
}

class GetStorageSpaceRepositoryImpl @Inject constructor(
    private val trueCloudV3Interface: TrueCloudV3Interface,
    private val userRepository: UserRepository
) : GetStorageSpaceRepository {

    companion object {
        private const val IMAGES = "Images"
        private const val VIDEOS = "Videos"
        private const val AUDIO = "Audio"
        private const val OTHERS = "Others"
        private const val CONTACTS = "Contacts"
    }

    override fun getStorage(): Flow<StorageResponse> {
        return flow {
            trueCloudV3Interface.storage(
                ssoid = userRepository.getSsoId()
            ).run {
                val responseBody = body()
                if (isSuccessful && responseBody != null) {
                    val sort = mutableListOf(
                        Pair(IMAGES, responseBody.data?.dataUsage?.images),
                        Pair(VIDEOS, responseBody.data?.dataUsage?.videos),
                        Pair(AUDIO, responseBody.data?.dataUsage?.audio),
                        Pair(OTHERS, responseBody.data?.dataUsage?.others),
                        Pair(CONTACTS, responseBody.data?.dataUsage?.contacts)
                    )
                    sort.sortByDescending { it.second }
                    responseBody.data?.dataUsage?.sortedObj = sort

                    emit(responseBody)
                } else {
                    error(code())
                }
            }
        }
    }
}
