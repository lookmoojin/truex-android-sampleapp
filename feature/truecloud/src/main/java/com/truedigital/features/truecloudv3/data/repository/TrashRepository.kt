package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.TrashObjectRequestModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface TrashRepository {
    fun restoreFile(
        fileList: List<String>
    ): Flow<Int>

    fun deleteTrashFile(
        fileList: List<String>
    ): Flow<Int>

    fun emptyTrash(
        fileList: List<String>
    ): Flow<Int>
}

class TrashRepositoryImpl @Inject constructor(
    private val trueCloudV3Interface: TrueCloudV3Interface,
    private val userRepository: UserRepository
) : TrashRepository {

    companion object {
        private const val ERROR_TRASH = "Move to trash failed"
    }

    override fun restoreFile(fileList: List<String>): Flow<Int> {
        return flow {
            trueCloudV3Interface.restoreTrashData(
                ssoid = userRepository.getSsoId(),
                obj = TrashObjectRequestModel(
                    objectIds = fileList
                )
            ).run {
                val response = body()
                if (isSuccessful && response != null) {
                    emit(code())
                } else {
                    error(ERROR_TRASH)
                }
            }
        }
    }

    override fun deleteTrashFile(fileList: List<String>): Flow<Int> {
        return flow {
            trueCloudV3Interface.deleteTrashData(
                ssoid = userRepository.getSsoId(),
                obj = TrashObjectRequestModel(
                    objectIds = fileList
                )
            ).run {
                val response = body()
                if (isSuccessful && response != null) {
                    emit(code())
                } else {
                    error(ERROR_TRASH)
                }
            }
        }
    }

    override fun emptyTrash(fileList: List<String>): Flow<Int> {
        return flow {
            trueCloudV3Interface.emptyTrashData(
                ssoid = userRepository.getSsoId(),
                obj = TrashObjectRequestModel(
                    objectIds = fileList
                )
            ).run {
                val response = body()
                if (isSuccessful && response != null) {
                    emit(code())
                } else {
                    error(ERROR_TRASH)
                }
            }
        }
    }
}
