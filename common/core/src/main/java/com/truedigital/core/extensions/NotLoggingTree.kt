package com.truedigital.core.extensions

import timber.log.Timber

class NotLoggingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        // default implementation ignored
    }
}
