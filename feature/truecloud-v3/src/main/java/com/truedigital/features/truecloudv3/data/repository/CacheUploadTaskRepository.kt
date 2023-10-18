package com.truedigital.features.truecloudv3.data.repository

import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.truedigital.core.utils.DataStoreInterface
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import org.jetbrains.annotations.VisibleForTesting
import org.json.JSONException
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

interface CacheUploadTaskRepository {
    fun getTasks(): Flow<MutableList<TaskUploadModel>>
    fun getRefreshTasks(): Flow<MutableList<TaskUploadModel>>
    suspend fun addUploadTask(task: TaskUploadModel)
    suspend fun addUploadTaskList(tasks: List<TaskUploadModel>)
    suspend fun clearAllTask()
    suspend fun removeTask(id: Int)
    suspend fun getTask(id: Int): TaskUploadModel?
    suspend fun getInprogressTask(): TaskUploadModel?
    suspend fun updateTaskStatus(id: Int, statusType: TaskStatusType)
    suspend fun updateTaskIdWithObjectId(task: TaskUploadModel)
}

class CacheUploadTaskRepositoryImpl @Inject constructor(
    private val dataStoreInterface: DataStoreInterface
) : CacheUploadTaskRepository {

    companion object {
        @VisibleForTesting
        const val KEY_TURE_CLOUD_V3_UPLOAD_TASK = "KEY_TURE_CLOUD_V3_UPLOAD_TASK"
        private const val DEFAULT_UPDATE_AT = 0L
        private const val MAXIMUM_PROGRESS = 100L
        private const val DEFAULT_INDEX_NOT_FOUND = -1
        private val mutex = Mutex()
    }

    override suspend fun addUploadTask(task: TaskUploadModel) {
        mutex.lock()
        try {
            val listTaskData = getTask()
            listTaskData.add(task)
            putUpdateData(listTaskData)
        } finally {
            mutex.unlock()
        }
    }

    override fun getTasks(): Flow<MutableList<TaskUploadModel>> {
        return flow {
            var oldTask = mutableListOf<TaskUploadModel>()
            delay(400.milliseconds)
            while (true) {
                val newTask =
                    getTask().filter { taskUpload -> taskUpload.updateAt != 0L }.toMutableList()
                if (oldTask.size != newTask.size || oldTask.toSet() != newTask.toSet()) {
                    oldTask = newTask
                    emit(newTask)
                }
                delay(1000.milliseconds)
            }
        }.filterNotNull()
    }

    override fun getRefreshTasks(): Flow<MutableList<TaskUploadModel>> {
        return flow {
            emit(getTask().filter { taskUpload -> taskUpload.updateAt != 0L }.toMutableList())
        }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun clearAllTask() {
        mutex.lock()
        try {
            dataStoreInterface.apply {
                removePreference(stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK))
            }
        } finally {
            mutex.unlock()
        }
    }

    @VisibleForTesting
    suspend fun getTask(): MutableList<TaskUploadModel> {
        val rawData = dataStoreInterface.getSinglePreference(
            stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
            ""
        )
        val listTaskData = mutableListOf<TaskUploadModel>()
        if (rawData.isNotEmpty()) {
            try {
                listTaskData.addAll(
                    Gson().fromJson(
                        rawData,
                        object : TypeToken<List<TaskUploadModel>>() {}.type
                    )
                )
            } catch (e: JSONException) {
                Timber.e(e)
            }
        }
        return listTaskData
    }

    override suspend fun removeTask(id: Int) {
        mutex.lock()
        try {
            val listTaskData = getTask()
            listTaskData.forEach {
                if (id.equals(it.id)) {
                    it.updateAt = 0L
                    return@forEach
                }
            }
            putUpdateData(listTaskData)
        } finally {
            mutex.unlock()
        }
    }

    override suspend fun getTask(id: Int): TaskUploadModel? {
        return getTask().firstOrNull { it.id == id }
    }

    private suspend fun putUpdateData(task: MutableList<TaskUploadModel>) {
        dataStoreInterface.apply {
            putPreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                Gson().toJson(task)
            )
        }
    }

    override suspend fun getInprogressTask(): TaskUploadModel? {
        var taskInprogress: TaskUploadModel? = null
        val tasks = getTask()
        for (task in tasks) {
            if (task.status.equals(TaskStatusType.IN_PROGRESS)) {
                taskInprogress = task
                break
            }
        }
        return taskInprogress
    }

    override suspend fun updateTaskStatus(id: Int, statusType: TaskStatusType) {
        mutex.lock()
        try {
            val listTaskData = getTask()
            listTaskData.filter { id == it.id }
                .map { taskUploadModel ->
                    taskUploadModel.status = statusType
                    if (taskUploadModel.updateAt != DEFAULT_UPDATE_AT) {
                        taskUploadModel.updateAt = System.currentTimeMillis()
                    }
                    if (TaskStatusType.COMPLETE.equals(statusType)) {
                        taskUploadModel.updateAt = DEFAULT_UPDATE_AT
                        taskUploadModel.progress = MAXIMUM_PROGRESS
                    }
                }
            putUpdateData(listTaskData)
        } finally {
            mutex.unlock()
        }
    }

    override suspend fun addUploadTaskList(tasks: List<TaskUploadModel>) {
        mutex.lock()
        try {
            val listTaskData = getTask()
            tasks.forEach { task ->
                if (!listTaskData.any { it.objectId == task.objectId }) {
                    listTaskData.add(task)
                }
            }
            putUpdateData(listTaskData)
        } finally {
            mutex.unlock()
        }
    }

    override suspend fun updateTaskIdWithObjectId(task: TaskUploadModel) {
        mutex.lock()
        try {
            val listTaskData = getTask()
            val taskIndex = listTaskData.indexOfFirst { it.objectId == task.objectId }
            if (taskIndex != DEFAULT_INDEX_NOT_FOUND) {
                if (listTaskData[taskIndex].actionType == TaskActionType.AUTO_BACKUP) {
                    task.actionType = listTaskData[taskIndex].actionType
                }
                listTaskData[taskIndex] = task
                putUpdateData(listTaskData)
            }
        } finally {
            mutex.unlock()
        }
    }
}
