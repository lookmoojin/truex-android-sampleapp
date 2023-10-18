package com.truedigital.features.tuned.common

fun String.xmlEncode() = this.replace("%", "%25")
    .replace("<", "%3C")
    .replace(">", "%3E")
    .replace("\"", "%22")
    .replace(" ", "%20")
    .replace("#", "%23")
