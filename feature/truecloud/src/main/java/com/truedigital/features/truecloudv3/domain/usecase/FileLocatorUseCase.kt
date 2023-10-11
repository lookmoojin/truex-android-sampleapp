package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface FileLocatorUseCase {
    fun execute(parentId: String, fileList: ArrayList<String>, type: String): Flow<Int>
}

class FileLocatorUseCaseImpl @Inject constructor(
    private val fileRepository: FileRepository
) : FileLocatorUseCase {

    override fun execute(
        parentId: String,
        fileList: ArrayList<String>,
        type: String
    ): Flow<Int> {
        return fileRepository.locateFile(parentId, fileList, type)
    }
}
