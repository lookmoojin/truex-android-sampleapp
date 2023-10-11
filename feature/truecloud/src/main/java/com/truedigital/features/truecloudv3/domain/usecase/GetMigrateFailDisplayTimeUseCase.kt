package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.MigrateDataRepository
import javax.inject.Inject

interface GetMigrateFailDisplayTimeUseCase {
    suspend fun execute(): Long
}

class GetMigrateFailDisplayTimeUseCaseImpl @Inject constructor(
    private val migrateDataRepository: MigrateDataRepository
) : GetMigrateFailDisplayTimeUseCase {
    override suspend fun execute(): Long {
        return migrateDataRepository.getFailDisplayTime()
    }
}
