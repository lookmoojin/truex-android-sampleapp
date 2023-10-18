package com.truedigital.features.tuned.common

import com.truedigital.features.tuned.BuildConfig

fun debugOrNonProdInvoke(ifTrue: () -> Unit) {
    if (BuildConfig.DEBUG || BuildConfig.FLAVOR != "prod") {
        ifTrue.invoke()
    }
}

fun debugOrNonProdInvoke(ifTrue: () -> Unit, ifFalse: () -> Unit) {
    return if (BuildConfig.DEBUG || BuildConfig.FLAVOR != "prod") {
        ifTrue.invoke()
    } else {
        ifFalse.invoke()
    }
}
