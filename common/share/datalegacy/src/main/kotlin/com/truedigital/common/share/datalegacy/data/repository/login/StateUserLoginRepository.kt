package com.truedigital.common.share.datalegacy.data.repository.login

import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import javax.inject.Inject

interface StateUserLoginRepository {
    fun isOpenByPass(): Boolean
    fun setMobileNumber(mobileNumber: String)
    fun getMobileNumber(): String
    fun setDeeplink(deeplink: String)
    fun getDeeplink(): String
    fun clearData()
}

class StateUserLoginRepositoryImpl @Inject constructor(
    private val authManagerWrapper: AuthManagerWrapper
) : StateUserLoginRepository {

    private var mobileNumber: String = ""
    private var deeplink: String = ""

    override fun isOpenByPass(): Boolean {
        return authManagerWrapper.isOpenByPass()
    }

    override fun setMobileNumber(mobileNumber: String) {
        this.mobileNumber = mobileNumber
    }

    override fun getMobileNumber(): String {
        return mobileNumber
    }

    override fun setDeeplink(deeplink: String) {
        this.deeplink = deeplink
    }

    override fun getDeeplink(): String {
        return deeplink
    }

    override fun clearData() {
        this.mobileNumber = ""
        this.deeplink = ""
    }
}
