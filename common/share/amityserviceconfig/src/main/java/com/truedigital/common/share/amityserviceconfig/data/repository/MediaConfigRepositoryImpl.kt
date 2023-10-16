package com.truedigital.common.share.amityserviceconfig.data.repository

import com.google.gson.Gson
import com.truedigital.common.share.amityserviceconfig.data.model.MediaConfigResponse
import com.truedigital.common.share.amityserviceconfig.domain.repository.MediaConfigRepository
import com.truedigital.core.extensions.addOnFailureListenerWithNewExecutor
import com.truedigital.core.extensions.addOnSuccessListenerWithNewExecutor
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MediaConfigRepositoryImpl @Inject constructor(
    private val firestoreUtil: FirestoreUtil
) : MediaConfigRepository {

    companion object {
        private const val NODE_NAME_TRUEID = "trueid_app"
        private const val NODE_NAME_FEATURE_CONFIG = "feature_config"
        private const val NODE_NAME_COMMUNITY = "community"
        private const val NODE_NAME_CONFIG = "config"
        private const val NODE_MEDIA = "media"
    }

    override fun getFeatureConfigMediaConfig(country: String): Flow<Boolean> {
        return callbackFlow {
            val collectionPath = FirestoreUtil.localizeCollectionPath(
                NODE_NAME_TRUEID, country.lowercase()
            )
            firestoreUtil.getFirestore()
                .collection(collectionPath)
                .document(NODE_NAME_FEATURE_CONFIG)
                .collection(NODE_NAME_COMMUNITY)
                .document(NODE_NAME_CONFIG)
                .get()
                .addOnFailureListenerWithNewExecutor { exception ->
                    trySend(false)
                    close(exception)
                }
                .addOnSuccessListenerWithNewExecutor { response ->
                    Gson().run {
                        val config: MediaConfigResponse? =
                            fromJson(
                                response.data?.get(NODE_MEDIA).toString(),
                                MediaConfigResponse::class.java
                            )
                        trySend(config?.enable?.android ?: false)
                    }
                }
            awaitClose()
        }
    }
}
