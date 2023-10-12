package com.truedigital.features.truecloudv3.data.repository

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

interface BuildVersionCheckRepository {
    fun isGreaterOrEqualVersionSdk27(): Boolean
}

class BuildVersionCheckRepositoryImpl : BuildVersionCheckRepository {
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
    override fun isGreaterOrEqualVersionSdk27(): Boolean {
        return true
    }
}
