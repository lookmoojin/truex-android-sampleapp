package com.truedigital.foundation.extension

fun <T> List<T>.isEqual(list: List<T>): Boolean {
    if (this.size != list.size) {
        return false
    }

    return this.zip(list).all { (x, y) -> x == y }
}
