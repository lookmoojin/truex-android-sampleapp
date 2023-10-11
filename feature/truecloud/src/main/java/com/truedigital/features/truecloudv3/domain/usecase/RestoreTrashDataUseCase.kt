package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.TrashRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RestoreTrashDataUseCase {
    fun execute(fileList: List<String>): Flow<Int>
}

class RestoreTrashDataUseCaseImpl @Inject constructor(
    private val trashRepository: TrashRepository
) : RestoreTrashDataUseCase {

    override fun execute(
        fileList: List<String>,
    ): Flow<Int> {
        return trashRepository.restoreFile(fileList)
    }
}
