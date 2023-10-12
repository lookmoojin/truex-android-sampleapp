package com.truedigital.common.share.datalegacy.domain.config.usecase

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.truedigital.core.constant.FireBaseConstant
import com.truedigital.core.extensions.addOnCompleteListenerWithNewExecutor
import com.truedigital.core.utils.SharedPrefsUtils
import io.reactivex.Completable
import javax.inject.Inject

interface GetAppConfigUseCase {
    fun execute(): Completable
}

class GetAppConfigUseCaseImpl @Inject constructor(
    private val fireBaseRemoteConfig: FirebaseRemoteConfig,
    private val sharedPrefs: SharedPrefsUtils
) : GetAppConfigUseCase {

    override fun execute(): Completable {
        return Completable.create { emitter ->
            // This is workaround solution to decide calling api
            if (sharedPrefs.contains(FireBaseConstant.FIREBASE_INBOX_ENABLED)) {
                emitter.onComplete()
            }
            fireBaseRemoteConfig.fetchAndActivate().addOnCompleteListenerWithNewExecutor { task ->
                if (task.isSuccessful) {
                    emitter.onComplete()
                    saveRemoteConfigToStorage()
                } else {
                    emitter.onError(task.exception?.fillInStackTrace() ?: Throwable())
                }
            }
        }
    }

    private fun saveRemoteConfigToStorage() {
        saveBooleanRemoteConfigToStorage(FireBaseConstant.FIREBASE_INBOX_ENABLED)
        saveBooleanRemoteConfigToStorage(FireBaseConstant.WELCOME_PAGE_FORCE_LOGIN_AB)
        saveBooleanRemoteConfigToStorage(FireBaseConstant.TODAY_ITEM_1X1_HIGHLIGHT)
        saveBooleanRemoteConfigToStorage(FireBaseConstant.TODAY_PERSONA_SEGMENT_ENABLE)
        saveBooleanRemoteConfigToStorage(FireBaseConstant.TODAY_NOW_TRENDING_ENABLE)

        saveStringRemoteConfigToStorage(FireBaseConstant.FIREBASE_ONE_APP_FORCE_LOGIN)
        saveStringRemoteConfigToStorage(FireBaseConstant.FIREBASE_ONE_APP_FORCE_LOGIN_RELAUNCH)
        saveStringRemoteConfigToStorage(FireBaseConstant.FIREBASE_MORE_PAGE_MENU)
        saveStringRemoteConfigToStorage(FireBaseConstant.FIREBASE_ONE_APP_SETTING)
        saveStringRemoteConfigToStorage(FireBaseConstant.TODAY_VIDEO_VERTICAL_CONFIG)
        saveStringRemoteConfigToStorage(FireBaseConstant.TODAY_REORDER_ITEMS_BY_PERSONA_CONFIG)
        saveStringRemoteConfigToStorage(FireBaseConstant.DEBUG_MODE)
        saveStringRemoteConfigToStorage(FireBaseConstant.FIREBASE_ANIMALS)
        saveStringRemoteConfigToStorage(FireBaseConstant.FIREBASE_ROCKET)
        saveStringRemoteConfigToStorage(FireBaseConstant.FIREBASE_PLANET)
        saveStringRemoteConfigToStorage(FireBaseConstant.FIREBASE_PINNED_DOMAINS)
    }

    private fun saveBooleanRemoteConfigToStorage(key: String) {
        val value = fireBaseRemoteConfig.getBoolean(key)
        sharedPrefs.put(key, value)
    }

    private fun saveStringRemoteConfigToStorage(key: String) {
        val value = fireBaseRemoteConfig.getString(key)
        sharedPrefs.put(key, value)
    }
}
