package com.truedigital.core.extensions

fun List<String>?.validateStringValue(requireNonNull: Boolean): List<String> {
    return when {
        requireNonNull -> when {
            this.isNullOrEmpty() -> listOf("")
            else -> this
        }
        else -> this ?: listOf()
    }
}

fun <T> List<T>.ifNotEmpty(block: (List<T>) -> Unit): List<T> {
    if (this.isNotEmpty()) {
        block.invoke(this)
    }
    return this
}

fun <T> List<T>?.doOnNullOrEmpty(block: (List<T>?) -> Unit) {
    if (isNullOrEmpty()) {
        block.invoke(this)
    }
}
