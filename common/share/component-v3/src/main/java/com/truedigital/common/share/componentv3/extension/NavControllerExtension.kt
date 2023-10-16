package com.truedigital.common.share.componentv3.extension

import androidx.navigation.NavController

fun NavController.popBackStackAllInstances(
    destination: Int,
    inclusive: Boolean
): Boolean {
    var popped: Boolean
    while (true) {
        popped = popBackStack(destination, inclusive)
        if (!popped) {
            break
        }
    }
    return popped
}

fun NavController.safePopBackStack() {
    runCatching {
        popBackStack()
    }
}

fun <T> NavController.setSavedStateHandle(key: String, result: T?) {
    this.previousBackStackEntry?.savedStateHandle?.set(key, result)
}

fun <T> NavController.getSavedStateHandle(key: String) =
    this.currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)
