package com.tdg.onboarding.data.repository

import com.truedigital.core.extensions.toObject
import com.tdg.onboarding.data.model.WhatNewResponse
import com.truedigital.share.data.firestoreconfig.FirestoreUtil
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface WhatNewConfigRepository {
    suspend fun getConfig(): WhatNewResponse?
}

class WhatNewConfigRepositoryImpl @Inject constructor(
    private val firestoreUtil: FirestoreUtil
) : WhatNewConfigRepository {

    companion object {
        const val NODE_NAME_CONFIG = "config"
        const val NODE_NAME_WHAT_NEW = "what_new"
    }

    override suspend fun getConfig(): WhatNewResponse? {
        return runCatching {
            firestoreUtil.getFirestoreTrueX()
                .collection(NODE_NAME_CONFIG)
                .document(NODE_NAME_WHAT_NEW)
                .get()
                .await()
                .toObject<WhatNewResponse>()
        }.getOrNull()
    }
}
