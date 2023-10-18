package com.truedigital.common.share.datalegacy.exception

abstract class TDBaseException : Throwable() {

    abstract fun getErrorCode(): String
    abstract fun getErrorMessage(): String

    override fun getLocalizedMessage(): String {
        return "${getErrorMessage()} (${getErrorCode()})"
    }
}
