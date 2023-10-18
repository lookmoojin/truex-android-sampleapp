package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.repository.MigrateDataRepository
import com.truedigital.features.truecloudv3.domain.model.MigrationModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PatchMigrateStatusUseCase {
    fun execute(): Flow<MigrationModel>
}

class PatchMigrateStatusUseCaseImpl @Inject constructor(
    private val migrateDataRepository: MigrateDataRepository
) : PatchMigrateStatusUseCase {
    override fun execute(): Flow<MigrationModel> {
        return migrateDataRepository.patchStatus().map { response ->
            response.data?.migration?.let { _data ->
                MigrationModel(
                    status = _data.status,
                    estimatedTimeToComplete = _data.estimatedTimeToComplete
                )
            } ?: error(TrueCloudV3ErrorMessage.ERROR_MIGRATION_UPDATE)
        }
    }
}
