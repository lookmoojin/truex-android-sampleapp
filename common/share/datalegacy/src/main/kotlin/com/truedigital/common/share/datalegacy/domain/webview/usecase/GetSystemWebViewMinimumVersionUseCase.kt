package com.truedigital.common.share.datalegacy.domain.webview.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetSystemWebViewMinimumVersionUseCase {
    fun execute(): Flow<String>
}

class GetSystemWebViewMinimumVersionUseCaseImpl @Inject constructor(
    private val initialAppConfigRepository: InitialAppConfigRepository,
    private val localizationRepository: LocalizationRepository
) : GetSystemWebViewMinimumVersionUseCase {

    companion object {
        const val NODE_NAME_SYSTEM_WEBVIEW = "system_webview"
        const val NODE_NAME_MIN_VERSION = "min_version"
        const val DEFAULT_VERSION = "0.0.0.0"
    }

    override fun execute(): Flow<String> {
        return flow {
            val config = initialAppConfigRepository.getConfigByKey(
                NODE_NAME_SYSTEM_WEBVIEW,
                localizationRepository.getAppCountryCode()
            ) as? Map<*, *>
            emit(config?.get(NODE_NAME_MIN_VERSION) as? String ?: DEFAULT_VERSION)
        }
    }
}
