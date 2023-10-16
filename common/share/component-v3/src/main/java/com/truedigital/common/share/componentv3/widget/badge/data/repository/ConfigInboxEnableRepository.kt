package com.truedigital.common.share.componentv3.widget.badge.data.repository

import com.truedigital.core.constant.FireBaseConstant
import com.truedigital.core.utils.SharedPrefsUtils
import io.reactivex.Observable
import javax.inject.Inject

interface ConfigInboxEnableRepository {
    fun isEnableInbox(): Observable<Boolean>
}

class ConfigInboxEnableRepositoryImpl @Inject constructor(private val sharedPrefs: SharedPrefsUtils) :
    ConfigInboxEnableRepository {
    override fun isEnableInbox(): Observable<Boolean> {
        return Observable.just(sharedPrefs.get(FireBaseConstant.FIREBASE_INBOX_ENABLED, false))
    }
}
