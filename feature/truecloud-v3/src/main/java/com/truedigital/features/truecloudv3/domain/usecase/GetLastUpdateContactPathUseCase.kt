package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.data.repository.ContactRepository
import com.truedigital.features.truecloudv3.domain.model.ContactDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

interface GetLastUpdateContactPathUseCase {
    fun execute(contactData: ContactDataModel): Flow<Pair<Boolean, String>>
}

class GetLastUpdateContactPathUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository,
    private val contextDataProvider: ContextDataProvider
) :
    GetLastUpdateContactPathUseCase {
    companion object {
        private const val TRUE_CLOUD_PATH = "/true_cloud_cache"
    }

    override fun execute(contactData: ContactDataModel): Flow<Pair<Boolean, String>> {
        return flow {
            val contactKey = contactData.id
            val cachePath =
                contextDataProvider.getDataContext().cacheDir.absolutePath + TRUE_CLOUD_PATH
            val file = File(cachePath)
            if (!file.exists()) {
                file.mkdir()
            }
            val fileData = File(file.path, contactKey)
            val path = fileData.path
            val lastUpdate = contactRepository.getUpdateAt()
            if (contactData.updatedAt.equals(lastUpdate) && fileData.exists() && fileData.isFile) {
                emit(Pair(true, path))
            } else {
                contactRepository.setUpdateAt(contactData.updatedAt)
                emit(Pair(false, path))
            }
        }
    }
}
