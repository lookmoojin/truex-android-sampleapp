package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

enum class NodePermission(val value: String) {
    STORAGE("image_storage"),
    CONTACT("image_contact"),
    NULL("")
}

interface TrueCloudV3ConfigPermissionImageUseCase {
    fun execute(nodePermission: NodePermission): Flow<Pair<String, String>>
}

class TrueCloudV3ConfigPermissionImageUseCaseImpl @Inject constructor(
    private val initialAppConfigRepository: InitialAppConfigRepository,
    private val localizationRepository: LocalizationRepository
) : TrueCloudV3ConfigPermissionImageUseCase {

    companion object {
        private const val NODE_NAME = "ui_permission"
        private const val NODE_NAME_ANDROID = "android"
        private const val NODE_NAME_BUTTON = "button"
    }

    override fun execute(nodePermission: NodePermission): Flow<Pair<String, String>> {
        return flow {
            val config = initialAppConfigRepository.getConfigByKey(
                NODE_NAME,
                localizationRepository.getAppCountryCode()
            ) as? Map<*, *>
            val languageCode = localizationRepository.getAppLanguageCode()
            val android = config?.get(NODE_NAME_ANDROID) as? Map<*, *>
            val byLocal = android?.get(languageCode) as? Map<*, *>
            byLocal?.let {
                val button = it[NODE_NAME_BUTTON] as? String ?: ""
                val image = it[nodePermission.value] as? String ?: ""
                emit(Pair(button, image))
            } ?: kotlin.run {
                emit(Pair("", ""))
            }
        }
    }
}
