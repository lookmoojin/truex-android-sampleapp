package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.MigrateDataRepository
import javax.inject.Inject

interface SetMigrateFailDisplayTimeUseCase {
    suspend fun execute(time: Long)
}

class SetMigrateFailDisplayTimeUseCaseImpl @Inject constructor(
    private val migrateDataRepository: MigrateDataRepository
) : SetMigrateFailDisplayTimeUseCase {
    override suspend fun execute(time: Long) {
        migrateDataRepository.addFailDisplayTime(time)
    }
}
