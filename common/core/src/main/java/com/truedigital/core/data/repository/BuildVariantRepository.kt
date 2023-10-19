package com.truedigital.core.data.repository

import com.truedigital.core.BuildConfig
import javax.inject.Inject

interface BuildVariantRepository {

//    fun getFlavorPlatform(): String
    fun getVersionName(): String
    fun getVersionCode(): String
}

class BuildVariantRepositoryImpl @Inject constructor() : BuildVariantRepository {

//    override fun getFlavorPlatform(): String = BuildConfig.FLAVOR_platform

    override fun getVersionName(): String = BuildConfig.VERSION_NAME

    override fun getVersionCode(): String = BuildConfig.VERSION_CODE
}
