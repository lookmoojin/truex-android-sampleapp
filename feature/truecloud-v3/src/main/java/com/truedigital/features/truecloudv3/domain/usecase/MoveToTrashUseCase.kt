package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface MoveToTrashUseCase {
    fun execute(fileList: List<String>): Flow<Int>
}

class MoveToTrashUseCaseImpl @Inject constructor(
    private val fileRepository: FileRepository
) : MoveToTrashUseCase {
    override fun execute(fileList: List<String>): Flow<Int> {
        return fileRepository.moveToTrash(fileList)
    }
}
