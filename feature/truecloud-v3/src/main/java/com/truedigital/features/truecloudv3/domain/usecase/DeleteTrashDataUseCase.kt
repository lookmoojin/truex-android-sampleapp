package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.TrashRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DeleteTrashDataUseCase {
    fun execute(fileList: List<String>): Flow<Int>
}

class DeleteTrashDataUseCaseImpl @Inject constructor(
    private val trashRepository: TrashRepository
) : DeleteTrashDataUseCase {

    override fun execute(
        fileList: List<String>,
    ): Flow<Int> {
        return trashRepository.deleteTrashFile(fileList)
    }
}
