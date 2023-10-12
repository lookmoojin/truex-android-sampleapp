package com.truedigital.features.truecloudv3.domain.usecase

import com.google.gson.Gson
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.data.repository.UploadFileRepository
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import javax.inject.Inject

interface UploadContactUseCase {
    fun execute(
        contactList: List<ContactTrueCloudModel>?,
        contactKey: String,
        folderId: String?
    ): Flow<TrueCloudV3TransferObserver>
}

class UploadContactUseCaseImpl @Inject constructor(
    private val uploadFileRepository: UploadFileRepository,
    private val contextDataProvider: ContextDataProvider
) :
    UploadContactUseCase {
    companion object {
        private const val TRUE_CLOUD_PATH = "/true_cloud_cache"
    }

    override fun execute(
        contactList: List<ContactTrueCloudModel>?,
        contactKey: String,
        folderId: String?
    ): Flow<TrueCloudV3TransferObserver> {
        return flow {
            val cacheDirectory =
                contextDataProvider.getDataContext().cacheDir.absolutePath + TRUE_CLOUD_PATH
            val file = File(cacheDirectory, contactKey)
            try {
                PrintWriter(FileWriter(file)).use {
                    val gson = Gson()
                    val jsonString = gson.toJson(contactList)
                    it.write(jsonString)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
            emit(file)
        }
            .flatMapLatest { _file ->
                uploadFileRepository.uploadContactWithPath(_file.absolutePath, folderId, contactKey)
                    .map { TrueCloudV3TransferObserver(it) }
            }
    }
}
