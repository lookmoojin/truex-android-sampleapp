package com.truedigital.share.data.firestoreconfig.initialappconfig.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.truedigital.core.extensions.addOnFailureListenerWithNewExecutor
import com.truedigital.core.extensions.addOnSuccessListenerWithNewExecutor
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface InitialAppConfigRepository {
    fun loadInitialAppConfig(countryCode: String)
    fun getConfigByKey(key: String): Any?
    suspend fun getConfigByKey(key: String, countryCode: String): Any?
}

class InitialAppConfigRepositoryImpl @Inject constructor(
    val firestoreUtil: FirestoreUtil
) : InitialAppConfigRepository {

    companion object {
        const val CONFIG_NODE_NAME_TRUEID = "trueid_app"
        const val CONFIG_NODE_NAME_INITIAL_APP = "initial_app"

        var initialAppConfig: Map<String, Any>? = null
    }

    override fun loadInitialAppConfig(countryCode: String) {
        getAppConfig(countryCode)
    }

    override fun getConfigByKey(key: String): Any? {
        return initialAppConfig?.get(key)
    }

    /* get config from firestore with coroutines */
    override suspend fun getConfigByKey(key: String, countryCode: String): Any? {
        getAppConfig(countryCode).await()
        return initialAppConfig?.get(key)
    }

    private fun getAppConfig(countryCode: String): Task<DocumentSnapshot> {
        val localizedCollectionPath = FirestoreUtil.localizeCollectionPath(
            CONFIG_NODE_NAME_TRUEID, countryCode
        )
        return firestoreUtil.getFirestore()
            .collection(localizedCollectionPath)
            .document(CONFIG_NODE_NAME_INITIAL_APP)
            .get()
            .addOnFailureListenerWithNewExecutor {
                initialAppConfig = null
            }
            .addOnSuccessListenerWithNewExecutor { response ->
                initialAppConfig = response.data
            }
    }
}
