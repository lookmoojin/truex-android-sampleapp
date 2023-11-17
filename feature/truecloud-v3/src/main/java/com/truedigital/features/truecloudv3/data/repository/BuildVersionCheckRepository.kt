package com.truedigital.features.truecloudv3.data.repository

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

interface BuildVersionCheckRepository {
    fun isGreaterOrEqualVersionSdk27(): Boolean
}

class BuildVersionCheckRepositoryImpl : BuildVersionCheckRepository {
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
    override fun isGreaterOrEqualVersionSdk27(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
    }
}
