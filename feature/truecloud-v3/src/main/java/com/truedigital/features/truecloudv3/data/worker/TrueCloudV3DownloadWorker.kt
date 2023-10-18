package com.truedigital.features.truecloudv3.data.worker

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage.ERROR_INIT_DOWNLOAD_DATA
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3DownloadInterface
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.provider.SecureTokenServiceProvider
import com.truedigital.features.truecloudv3.service.TrueCloudV3Service
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class TrueCloudV3DownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var trueCloudV3DownloadInterface: TrueCloudV3DownloadInterface

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var sTSProvider: SecureTokenServiceProvider

    @Inject
    lateinit var contextDataProvider: ContextDataProvider

    init {
        TrueCloudV3Component.getInstance().inject(this)
    }

    companion object {
        const val KEY = "key"
        const val PATH = "file_path"
    }

    override suspend fun doWork(): Result {
        val key = inputData.getString(KEY) ?: ""
        val filePath = inputData.getString(PATH)
        flow {
            trueCloudV3DownloadInterface.initialDownload(
                ssoid = userRepository.getSsoId(),
                objectId = key
            ).run {
                val responseBody = body()
                if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                } else {
                    error(ERROR_INIT_DOWNLOAD_DATA)
                }
            }
        }.combine(sTSProvider.getSTS()) { initData, stsdata ->
            Pair(initData, stsdata)
        }.catch { Timber.e(it) }
            .collectSafe {
                val stsData = it.second
                val context: Context = contextDataProvider.getDataContext()
                val intent = Intent(context, TrueCloudV3Service::class.java)
                intent.putExtra(TrueCloudV3Service.ACCESS_TOKEN, stsData.accessToken)
                intent.putExtra(TrueCloudV3Service.SECRET_KEY, stsData.secretKey)
                intent.putExtra(TrueCloudV3Service.SESSION_KEY, stsData.sessionKey)
                intent.putExtra(TrueCloudV3Service.FILE_PATH, filePath)
                intent.putExtra(TrueCloudV3Service.ENDPOINT, stsData.endpoint)
                intent.putExtra(TrueCloudV3Service.OBJECT_ID, key)
                intent.putExtra(
                    TrueCloudV3Service.INTENT_TRANSFER_OPERATION,
                    TrueCloudV3Service.TRANSFER_OPERATION_DOWNLOAD
                )
                context.startService(intent)
            }
        return Result.success()
    }
}
