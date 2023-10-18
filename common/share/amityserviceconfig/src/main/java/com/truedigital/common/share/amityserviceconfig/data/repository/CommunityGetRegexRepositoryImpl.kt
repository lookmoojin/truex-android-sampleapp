package com.truedigital.common.share.amityserviceconfig.data.repository

import com.google.gson.Gson
import com.truedigital.common.share.amityserviceconfig.domain.repository.CommunityGetRegexRepository
import com.truedigital.core.extensions.addOnFailureListenerWithNewExecutor
import com.truedigital.core.extensions.addOnSuccessListenerWithNewExecutor
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CommunityGetRegexRepositoryImpl @Inject constructor(
    private val firestoreUtil: FirestoreUtil
) : CommunityGetRegexRepository {

    companion object {
        private const val NODE_NAME_TRUEID = "trueid_app"
        private const val NODE_NAME_FEATURE_CONFIG = "feature_config"
        private const val NODE_NAME_COMMUNITY = "community"
        private const val NODE_NAME_CONFIG = "config"
        private const val NODE_MASKING_MOBILE_NUMBER = "masking_mobile_number"
    }

    override fun getFeatureConfigRegex(country: String): Flow<String> {
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
                    close(exception)
                }
                .addOnSuccessListenerWithNewExecutor { response ->
                    val data = Gson().toJson(response.data?.get(NODE_MASKING_MOBILE_NUMBER))
                    trySend(data)
                }
            awaitClose()
        }
    }
}
