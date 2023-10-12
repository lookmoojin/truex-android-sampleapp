package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.core.extensions.toObject
import com.truedigital.features.truecloudv3.data.model.ConfigIntroModel
import com.truedigital.features.truecloudv3.data.model.IntroLanguageModel
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface ConfigIntroImageRepository {
    suspend fun getConfig(
        countryCode: String,
        isTablet: Boolean
    ): IntroLanguageModel?
}

class ConfigIntroImageRepositoryImpl @Inject constructor(
    private val firestoreUtil: FirestoreUtil,
) : ConfigIntroImageRepository {

    companion object {
        const val NODE_NAME_TRUEID = "trueid_app"
        const val NODE_NAME_FEATURE_CONFIG = "feature_config"
        const val NODE_NAME_TRUE_CLOUD = "truecloud"
        const val NODE_NAME_CONFIG = "config"
    }

    override suspend fun getConfig(
        countryCode: String,
        isTablet: Boolean
    ): IntroLanguageModel? {
        return if (isTablet) {
            loadConfig(countryCode)?.login?.introImageTablet
        } else {
            loadConfig(countryCode)?.login?.introImage
        }
    }

    private suspend fun loadConfig(countryCode: String): ConfigIntroModel? {
        val localizedCollectionPath = FirestoreUtil.localizeCollectionPath(
            NODE_NAME_TRUEID, countryCode
        )
        return runCatching {
            firestoreUtil.getFirestore()
                .collection(localizedCollectionPath)
                .document(NODE_NAME_FEATURE_CONFIG)
                .collection(NODE_NAME_TRUE_CLOUD)
                .document(NODE_NAME_CONFIG)
                .get()
                .await()
                .toObject<ConfigIntroModel>()
        }.getOrNull()
    }
}
