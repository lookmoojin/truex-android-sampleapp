package com.truedigital.core.utils.extension

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.update(builder: T.() -> T) {
    this.value?.let {
        value = builder(it)
    }
}
