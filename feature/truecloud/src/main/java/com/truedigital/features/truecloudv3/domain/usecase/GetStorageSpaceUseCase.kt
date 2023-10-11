package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage.ERROR_STORAGE_NULL
import com.truedigital.features.truecloudv3.data.repository.GetStorageSpaceRepository
import com.truedigital.features.truecloudv3.domain.model.DataMigrationModel
import com.truedigital.features.truecloudv3.domain.model.DataStorageModel
import com.truedigital.features.truecloudv3.domain.model.DataUsageModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.extension.convertToTrueCloudV3Model
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetStorageSpaceUseCase {
    fun execute(): Flow<DataStorageModel>
}

class GetStorageSpaceUseCaseImpl @Inject constructor(
    private val getStorageSpaceRepository: GetStorageSpaceRepository
) : GetStorageSpaceUseCase {
    override fun execute(): Flow<DataStorageModel> {
        return getStorageSpaceRepository.getStorage().map { response ->
            response.data?.let { _data ->
                var dataUsageModel: DataUsageModel? = null
                _data.dataUsage?.let { dataUsage ->
                    dataUsageModel = DataUsageModel(
                        images = dataUsage.images,
                        videos = dataUsage.videos,
                        audio = dataUsage.audio,
                        others = dataUsage.others,
                        contacts = dataUsage.contacts,
                        total = dataUsage.total,
                        sortedObj = dataUsage.sortedObj
                    )
                }
                var dataMigrationModel: DataMigrationModel? = null
                _data.migration?.let { migration ->
                    dataMigrationModel = DataMigrationModel(
                        status = migration.status,
                        estimatedTimeToComplete = migration.estimatedTimeToComplete,
                        failedDisplayTime = migration.failedDisplayTime
                    )
                }
                var trueCloudV3Model: TrueCloudV3Model? = null
                _data.rootFolder?.let { objectModel ->
                    trueCloudV3Model = objectModel.convertToTrueCloudV3Model()
                }

                DataStorageModel(
                    quota = _data.quota,
                    rootFolder = trueCloudV3Model,
                    dataUsage = dataUsageModel,
                    migration = dataMigrationModel
                )
            } ?: error(ERROR_STORAGE_NULL)
        }
    }
}
