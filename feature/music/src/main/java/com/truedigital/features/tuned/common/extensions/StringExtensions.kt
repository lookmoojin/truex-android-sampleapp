package com.truedigital.features.tuned.common.extensions

import java.util.concurrent.TimeUnit

fun Long.toDurationString(): String =
    String.format(
        "%d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(this),
        TimeUnit.MILLISECONDS.toSeconds(this) % TimeUnit.MINUTES.toSeconds(1)
    )
