package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.TrashRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface EmptyTrashDataUseCase {
    fun execute(fileList: List<String>): Flow<Int>
}

class EmptyTrashDataUseCaseImpl @Inject constructor(
    private val trashRepository: TrashRepository
) : EmptyTrashDataUseCase {

    override fun execute(
        fileList: List<String>,
    ): Flow<Int> {
        return trashRepository.emptyTrash(fileList)
    }
}
