package com.truedigital.share.data.firestoreconfig

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestoreSettings
import com.truedigital.core.constant.FireBaseConstant
import java.util.Locale
import javax.inject.Inject

interface FirestoreUtil {
    companion object {
        fun localizeCollectionPath(
            collectionName: String,
            countryCode: String
        ): String {
            return if (countryCode.isNotEmpty()) {
                "${collectionName}_${countryCode.lowercase(Locale.ROOT)}"
            } else collectionName
        }
    }

    fun getFirestore(): FirebaseFirestore
    fun getFirestoreUsageMeter(): FirebaseFirestore
}

class FirestoreUtilImpl @Inject constructor() : FirestoreUtil {

    override fun getFirestore(): FirebaseFirestore {
        val settings: FirebaseFirestoreSettings = firestoreSettings {
            cacheSizeBytes = FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
            isPersistenceEnabled = true
        }

        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(
            FirebaseApp.getInstance(
                BuildConfig.FIRE_BASE_SECURE_NAME
            )
        )
        firestore.firestoreSettings = settings

        return firestore
    }

    override fun getFirestoreUsageMeter(): FirebaseFirestore {
        return FirebaseFirestore.getInstance(
            FirebaseApp.getInstance(
                FireBaseConstant.USAGEMETER_REAL_TIME_DB_APP_NAME
            )
        )
    }
}
