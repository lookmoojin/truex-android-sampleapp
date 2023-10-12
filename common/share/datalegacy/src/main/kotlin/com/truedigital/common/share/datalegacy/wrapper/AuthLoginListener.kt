package com.truedigital.common.share.datalegacy.wrapper

interface BaseAuthActionListener {
    fun onLoginSuccess()
    fun onLoginError()
    fun onLoginCancel()
}

open class AuthLoginListener : BaseAuthActionListener {
    override fun onLoginSuccess() {}
    override fun onLoginError() {}
    override fun onLoginCancel() {}
}
