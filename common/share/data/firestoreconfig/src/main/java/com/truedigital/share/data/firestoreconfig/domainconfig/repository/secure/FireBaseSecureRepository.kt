package com.truedigital.share.data.firestoreconfig.domainconfig.repository.secure

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.truedigital.share.data.firestoreconfig.BuildConfig
import javax.inject.Inject

interface FireBaseSecureRepository {
    fun getFireBaseSecure(context: Context)
}

class FireBaseSecureRepositoryImpl @Inject constructor() : FireBaseSecureRepository {
    override fun getFireBaseSecure(context: Context) {
        FirebaseApp.initializeApp(
            context,
            FirebaseOptions.Builder()
                .setApplicationId(BuildConfig.FIRE_BASE_APPLICATION_ID)
                .setApiKey(BuildConfig.FIRE_BASE_API_KEY)
                .setGcmSenderId(BuildConfig.FIRE_BASE_GCM_SENDER_ID)
                .setStorageBucket(BuildConfig.FIRE_BASE_STORAGE_BUCKET)
                .setProjectId(BuildConfig.FIRE_BASE_PROJECT_ID)
                .build(),
            BuildConfig.FIRE_BASE_SECURE_NAME
        )
    }
}
