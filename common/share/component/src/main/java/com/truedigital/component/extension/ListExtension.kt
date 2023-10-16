package com.truedigital.component.extension

// ex [aaa, bbb, ccc] -> aaa|bbb|ccc
fun <E> List<E>?.toStringWithPipe(): String {
    return this.orEmpty().toString()
        .replace("[", "")
        .replace("]", "")
        .replace(", ", "|")
}
