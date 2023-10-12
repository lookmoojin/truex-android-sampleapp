package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.ObjectsRequestModel
import com.truedigital.features.truecloudv3.data.model.TrashObjectRequestModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface FileRepository {
    fun locateFile(
        parentId: String,
        fileList: ArrayList<String>,
        type: String
    ): Flow<Int>

    fun moveToTrash(
        fileList: List<String>
    ): Flow<Int>
}

class FileRepositoryImpl @Inject constructor(
    private val trueCloudV3Interface: TrueCloudV3Interface,
    private val userRepository: UserRepository
) : FileRepository {

    companion object {
        const val MOVE = "move"
        const val COPY = "copy"
        private const val ERROR_LOCATE_FILE = "Locate File Failed"
        private const val ERROR_MOVE_TO_TRASH = "Move to trash failed"
    }

    override fun locateFile(
        parentId: String,
        fileList: ArrayList<String>,
        type: String
    ): Flow<Int> {
        return flow {
            trueCloudV3Interface.moveObject(
                ssoid = userRepository.getSsoId(),
                type = type,
                obj = ObjectsRequestModel(
                    parentObjectId = parentId,
                    objectIds = fileList
                )
            ).run {
                val response = body()
                if (isSuccessful && response != null) {
                    emit(code())
                } else {
                    error(ERROR_LOCATE_FILE)
                }
            }
        }
    }

    override fun moveToTrash(fileList: List<String>): Flow<Int> {
        return flow {
            trueCloudV3Interface.moveToTrash(
                ssoid = userRepository.getSsoId(),
                obj = TrashObjectRequestModel(
                    objectIds = fileList
                )
            ).run {
                val response = body()
                if (isSuccessful && response != null) {
                    emit(code())
                } else {
                    error(ERROR_MOVE_TO_TRASH)
                }
            }
        }
    }
}
