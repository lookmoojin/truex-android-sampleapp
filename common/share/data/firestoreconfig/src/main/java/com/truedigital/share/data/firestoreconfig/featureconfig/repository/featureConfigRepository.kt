package com.truedigital.share.data.firestoreconfig.featureconfig.repository

import com.truedigital.core.extensions.addOnFailureListenerWithNewExecutor
import com.truedigital.core.extensions.addOnSuccessListenerWithNewExecutor
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import javax.inject.Inject

interface FeatureConfigRepository {
    fun loadInitialAppConfig(countryCode: String)
    fun getConfigByKey(key: String): Any?
}

class FeatureConfigRepositoryImpl @Inject constructor(val firestoreUtil: FirestoreUtil) :
    FeatureConfigRepository {

    companion object {
        const val CONFIG_NODE_NAME_TRUEID = "trueid_app"
        const val CONFIG_NODE_NAME_INITIAL_APP = "feature_config"

        var featureConfig: Map<String, Any>? = null
    }

    override fun loadInitialAppConfig(countryCode: String) {
        val localizedCollectionPath = FirestoreUtil.localizeCollectionPath(
            CONFIG_NODE_NAME_TRUEID, countryCode
        )
        firestoreUtil.getFirestore()
            .collection(localizedCollectionPath)
            .document(CONFIG_NODE_NAME_INITIAL_APP)
            .get()
            .addOnFailureListenerWithNewExecutor {
                featureConfig = null
            }
            .addOnSuccessListenerWithNewExecutor { response ->
                featureConfig = response.data
            }
    }

    override fun getConfigByKey(key: String): Any? {
        return featureConfig?.get(key)
    }
}
