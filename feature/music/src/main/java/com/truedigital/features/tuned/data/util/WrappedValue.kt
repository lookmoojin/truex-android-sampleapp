package com.truedigital.features.tuned.data.util

import com.google.gson.annotations.SerializedName

data class WrappedValue<T>(@SerializedName("Value") val value: T)
