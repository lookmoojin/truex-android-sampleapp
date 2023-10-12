package com.truedigital.features.truecloudv3.data.repository

import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.utils.DataStoreUtil
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal interface CacheUploadTaskRepositoryTestImpl {
    fun `test getTask return empty`()
    fun `test getRefreshTasks return success`()
    fun `test getRefreshTasks return empty updateAt = 0`()
    fun `test addUploadTask return success`()
    fun `test clearAllTask return success`()
    fun `test removeTask return success`()
    fun `test getTask return success`()
    fun `test getInprogressTask return success`()
    fun `test updateStatus2 return success`()
}

class CacheUploadTaskRepositoryTest : CacheUploadTaskRepositoryTestImpl {
    private lateinit var cacheUploadTaskRepository: CacheUploadTaskRepository
    private lateinit var cacheUploadTaskRepositoryImpl: CacheUploadTaskRepositoryImpl
    private val dataStoreUtil: DataStoreUtil = mockk()
    private val KEY_TURE_CLOUD_V3_UPLOAD_TASK = "KEY_TURE_CLOUD_V3_UPLOAD_TASK"

    @BeforeEach
    fun setup() {
        cacheUploadTaskRepository = CacheUploadTaskRepositoryImpl(
            dataStoreInterface = dataStoreUtil
        )
        cacheUploadTaskRepositoryImpl = CacheUploadTaskRepositoryImpl(
            dataStoreInterface = dataStoreUtil
        )
    }

    @Test
    override fun `test getRefreshTasks return success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 1L
        )
        val list = mutableListOf(taskUploadModel)
        val uploadFileModel = taskUploadModel.getUploadFilesModel()
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                ""
            )
        } returns Gson().toJson(list)

        // act
        val flow = cacheUploadTaskRepository.getRefreshTasks()

        // assert
        flow.collectSafe { response ->
            assertEquals(list, response)
        }
        assertEquals(1, uploadFileModel.id)
        assertEquals("abc", uploadFileModel.path)
        assertEquals(TaskStatusType.IN_PROGRESS, uploadFileModel.status)
        assertEquals("xyz.jpg", uploadFileModel.name)
        assertEquals("100", uploadFileModel.size)
        assertEquals(FileMimeType.IMAGE, uploadFileModel.type)
        assertEquals(1L, uploadFileModel.updateAt)

        assertEquals(taskUploadModel.id, uploadFileModel.id)
        assertEquals(taskUploadModel.path, uploadFileModel.path)
        assertEquals(taskUploadModel.status, uploadFileModel.status)
        assertEquals(taskUploadModel.name, uploadFileModel.name)
        assertEquals(taskUploadModel.size, uploadFileModel.size)
        assertEquals(taskUploadModel.type, uploadFileModel.type)
        assertEquals(taskUploadModel.updateAt, uploadFileModel.updateAt)
        assertEquals(taskUploadModel.coverImageSize, uploadFileModel.coverImageSize)
        assertEquals(taskUploadModel.objectId, uploadFileModel.objectId)
        assertEquals(taskUploadModel.actionType, uploadFileModel.actionType)
        assertEquals(taskUploadModel.progress, uploadFileModel.progress)
    }

    @Test
    override fun `test getRefreshTasks return empty updateAt = 0`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        val list = mutableListOf(taskUploadModel)
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                ""
            )
        } returns Gson().toJson(list)

        // act
        val flow = cacheUploadTaskRepository.getRefreshTasks()

        // assert
        flow.collectSafe { response ->
            assertEquals(mutableListOf(), response)
        }
    }

    @Test
    override fun `test addUploadTask return success`() = runTest {
        // arrange
        val taskUploadModel2 = TaskUploadModel(
            id = 2,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        val list = mutableListOf(taskUploadModel2)
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                ""
            )
        } returns Gson().toJson(list)
        coEvery {
            dataStoreUtil.putPreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                any()
            )
        } returns Unit

        // act
        cacheUploadTaskRepository.addUploadTask(taskUploadModel2)

        // assert
        coVerify(exactly = 1) {
            dataStoreUtil.apply {
                putPreference(
                    stringPreferencesKey(
                        KEY_TURE_CLOUD_V3_UPLOAD_TASK
                    ),
                    any()
                )
            }
        }
    }

    @Test
    override fun `test clearAllTask return success`() = runTest {
        // arrange
        coEvery {
            dataStoreUtil.removePreference(
                stringPreferencesKey(
                    KEY_TURE_CLOUD_V3_UPLOAD_TASK
                )
            )
        } returns Unit
        // act
        cacheUploadTaskRepository.clearAllTask()

        // assert
        coVerify(exactly = 1) {
            dataStoreUtil.apply {
                removePreference(
                    stringPreferencesKey(
                        KEY_TURE_CLOUD_V3_UPLOAD_TASK
                    )
                )
            }
        }
    }

    @Test
    override fun `test removeTask return success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        val taskUploadModel2 = TaskUploadModel(
            id = 2,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        val list = mutableListOf(taskUploadModel, taskUploadModel2)
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                ""
            )
        } returns Gson().toJson(list)

        coEvery {
            dataStoreUtil.putPreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                Gson().toJson(list)
            )
        } returns Unit

        // act
        cacheUploadTaskRepository.removeTask(2)

        // assert
        coVerify(exactly = 1) {
            dataStoreUtil.apply {
                putPreference(
                    stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                    any()
                )
            }
        }
    }

    @Test
    override fun `test getTask return success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L,
            coverImageSize = 0,
            objectId = "1239143245245",
            actionType = TaskActionType.UPLOAD,
            progress = 990
        )
        val taskUploadModel2 = TaskUploadModel(
            id = 2,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        val uploadFileModel = taskUploadModel.getUploadFilesModel()
        taskUploadModel2.progress = 890
        taskUploadModel2.objectId = "1235624y5345"
        taskUploadModel2.coverImageSize = 0
        taskUploadModel2.type = FileMimeType.VIDEO
        taskUploadModel2.size = "5324"
        taskUploadModel2.name = "name test"

        val list = mutableListOf(taskUploadModel, taskUploadModel2)
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                ""
            )
        } returns Gson().toJson(list)

        // act
        val response = cacheUploadTaskRepository.getTask(1)

        // assert
        assertEquals(taskUploadModel, response)
        assertEquals(uploadFileModel, response?.getUploadFilesModel())
        assertEquals(uploadFileModel.id, response?.getUploadFilesModel()?.id)
        assertEquals(uploadFileModel.path, response?.getUploadFilesModel()?.path)
        assertEquals(uploadFileModel.name, response?.getUploadFilesModel()?.name)
        assertEquals(uploadFileModel.size, response?.getUploadFilesModel()?.size)
        assertEquals(uploadFileModel.type, response?.getUploadFilesModel()?.type)
        assertEquals(uploadFileModel.updateAt, response?.getUploadFilesModel()?.updateAt)
        assertEquals(uploadFileModel.status, response?.getUploadFilesModel()?.status)
        assertEquals(uploadFileModel.actionType, response?.getUploadFilesModel()?.actionType)
        assertEquals(uploadFileModel.objectId, response?.getUploadFilesModel()?.objectId)
        assertEquals(uploadFileModel.progress, response?.getUploadFilesModel()?.progress)
    }

    @Test
    override fun `test getTask return empty`() = runTest {
        // arrange
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                ""
            )
        } returns ""

        // act
        val response = cacheUploadTaskRepositoryImpl.getTask()

        // assert
        assertEquals(response, mutableListOf())
    }

    @Test
    fun `getTasks should return correct task list`() = runTest {
        // Set up mock behavior
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 1L
        )
        val taskList = mutableListOf(taskUploadModel)
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                ""
            )
        } returns Gson().toJson(taskList)
        val mockCacheUploadTaskRepositoryImpl = mockk<CacheUploadTaskRepositoryImpl>()
        coEvery { mockCacheUploadTaskRepositoryImpl.getTask() } returns taskList

        // Call getTasks and collect emitted values
        val taskListFlow = cacheUploadTaskRepositoryImpl.getTasks()
        val emittedTaskLists = mutableListOf<MutableList<TaskUploadModel>>()
        emittedTaskLists.add(taskListFlow.first() ?: mutableListOf())

        // Assert that the emitted values match the expected results
        assertEquals(1, emittedTaskLists.size)
        assertEquals(taskList, emittedTaskLists[0])
    }

    @Test
    override fun `test getInprogressTask return success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.FAILED,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        val taskUploadModel2 = TaskUploadModel(
            id = 2,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        val list = mutableListOf(taskUploadModel, taskUploadModel2)
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                ""
            )
        } returns Gson().toJson(list)

        // act
        val response = cacheUploadTaskRepository.getInprogressTask()

        // assert
        assertEquals(taskUploadModel2, response)
    }

    @Test
    override fun `test updateStatus2 return success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        val taskUploadModel2 = TaskUploadModel(
            id = 2,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        val list = mutableListOf(taskUploadModel, taskUploadModel2)
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                ""
            )
        } returns Gson().toJson(list)
        coEvery {
            dataStoreUtil.putPreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                any()
            )
        } returns Unit

        // act
        cacheUploadTaskRepository.updateTaskStatus(1, TaskStatusType.FAILED)

        // assert
        coVerify(exactly = 1) {
            dataStoreUtil.apply {
                putPreference(
                    stringPreferencesKey(
                        KEY_TURE_CLOUD_V3_UPLOAD_TASK
                    ),
                    any()
                )
            }
        }
    }

    @Test
    fun `test updateStatus2 status complete return success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 5L
        )
        val taskUploadModel2 = TaskUploadModel(
            id = 2,
            path = "abc",
            status = TaskStatusType.COMPLETE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        val list = mutableListOf(taskUploadModel, taskUploadModel2)
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                ""
            )
        } returns Gson().toJson(list)
        coEvery {
            dataStoreUtil.putPreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                any()
            )
        } returns Unit

        // act
        cacheUploadTaskRepository.updateTaskStatus(1, TaskStatusType.FAILED)

        // assert
        coVerify(exactly = 1) {
            dataStoreUtil.apply {
                putPreference(
                    stringPreferencesKey(
                        KEY_TURE_CLOUD_V3_UPLOAD_TASK
                    ),
                    any()
                )
            }
        }
    }

    @Test
    fun testAddUploadTaskList() = runTest {
// arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            objectId = "objectId1",
            updateAt = 5L
        )
        val taskUploadModel2 = TaskUploadModel(
            id = 2,
            path = "abc",
            status = TaskStatusType.COMPLETE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            objectId = "objectId2",
            updateAt = 0L
        )
        val taskUploadModelOld = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            objectId = "objectId1",
            updateAt = 5L
        )
        val taskUploadModel2Old = TaskUploadModel(
            id = 2,
            path = "abc",
            status = TaskStatusType.COMPLETE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            objectId = "objectId3",
            updateAt = 0L
        )
        val list = listOf(taskUploadModel, taskUploadModel2)
        val listOld = listOf(taskUploadModelOld, taskUploadModel2Old)
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                ""
            )
        } returns Gson().toJson(listOld)
        coEvery {
            dataStoreUtil.putPreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                any()
            )
        } returns Unit

        // act
        cacheUploadTaskRepository.addUploadTaskList(list)

        // assert
        coVerify(exactly = 1) {
            dataStoreUtil.apply {
                putPreference(
                    stringPreferencesKey(
                        KEY_TURE_CLOUD_V3_UPLOAD_TASK
                    ),
                    any()
                )
            }
        }
    }
    @Test
    fun testUpdateTaskIdWithObjectId() = runTest {
// arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            objectId = "objectId1",
            updateAt = 5L
        )
        val taskUploadModelOld = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            objectId = "objectId1",
            updateAt = 5L
        )
        val taskUploadModel2Old = TaskUploadModel(
            id = 2,
            path = "abc",
            status = TaskStatusType.COMPLETE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            objectId = "objectId2",
            updateAt = 0L
        )
        val listOld = listOf(taskUploadModelOld, taskUploadModel2Old)
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                ""
            )
        } returns Gson().toJson(listOld)
        coEvery {
            dataStoreUtil.putPreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                any()
            )
        } returns Unit

        // act
        cacheUploadTaskRepository.updateTaskIdWithObjectId(taskUploadModel)

        // assert
        coVerify(exactly = 1) {
            dataStoreUtil.apply {
                putPreference(
                    stringPreferencesKey(
                        KEY_TURE_CLOUD_V3_UPLOAD_TASK
                    ),
                    any()
                )
            }
        }
    }

    @Test
    fun testUpdateTaskIdWithObjectIdActionTypeAutoBackup() = runTest {
// arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            objectId = "objectId1",
            updateAt = 5L,
            actionType = TaskActionType.AUTO_BACKUP
        )
        val taskUploadModelOld = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            objectId = "objectId1",
            updateAt = 5L,
            actionType = TaskActionType.AUTO_BACKUP
        )
        val taskUploadModel2Old = TaskUploadModel(
            id = 2,
            path = "abc",
            status = TaskStatusType.COMPLETE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            objectId = "objectId2",
            updateAt = 0L
        )
        val listOld = listOf(taskUploadModelOld, taskUploadModel2Old)
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                ""
            )
        } returns Gson().toJson(listOld)
        coEvery {
            dataStoreUtil.putPreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_UPLOAD_TASK),
                any()
            )
        } returns Unit

        // act
        cacheUploadTaskRepository.updateTaskIdWithObjectId(taskUploadModel)

        // assert
        coVerify(exactly = 1) {
            dataStoreUtil.apply {
                putPreference(
                    stringPreferencesKey(
                        KEY_TURE_CLOUD_V3_UPLOAD_TASK
                    ),
                    any()
                )
            }
        }
    }
}
