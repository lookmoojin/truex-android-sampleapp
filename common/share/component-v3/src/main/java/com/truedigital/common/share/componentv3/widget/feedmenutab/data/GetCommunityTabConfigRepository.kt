package com.truedigital.common.share.componentv3.widget.feedmenutab.data

import com.truedigital.common.share.componentv3.widget.feedmenutab.data.model.CommunityTabConfigModel
import com.truedigital.core.extensions.toObject
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface GetCommunityTabConfigRepository {
    suspend fun getCommunityTabConfig(countryCode: String): CommunityTabConfigModel?
}

class GetCommunityTabConfigRepositoryImpl @Inject constructor(
    val firestoreUtil: FirestoreUtil
) : GetCommunityTabConfigRepository {

    companion object {
        private const val CONFIG_NODE_NAME_TRUEID = "trueid_app"
        private const val CONFIG_NODE_NAME_FEATURE_CONFIG = "feature_config"
        private const val CONFIG_NODE_NAME_TODAY = "today"
        private const val CONFIG_NODE_NAME_CONFIG = "config"
    }

    private var currentCountryCode: String = ""
    private var communityDataConfig: CommunityTabConfigModel? = null

    override suspend fun getCommunityTabConfig(countryCode: String): CommunityTabConfigModel? {
        return if (currentCountryCode == countryCode && communityDataConfig != null) {
            communityDataConfig
        } else {
            loadConfig(countryCode).also { config ->
                currentCountryCode = countryCode
                communityDataConfig = config
            }
        }
    }

    private suspend fun loadConfig(
        countryCode: String
    ): CommunityTabConfigModel? {
        val localizedCollectionPath = FirestoreUtil.localizeCollectionPath(
            CONFIG_NODE_NAME_TRUEID, countryCode
        )
        return runCatching {
            firestoreUtil.getFirestore()
                .collection(localizedCollectionPath)
                .document(CONFIG_NODE_NAME_FEATURE_CONFIG)
                .collection(CONFIG_NODE_NAME_TODAY)
                .document(CONFIG_NODE_NAME_CONFIG)
                .get()
                .await()
                .toObject<CommunityTabConfigModel>()
        }.getOrNull()
    }
}
