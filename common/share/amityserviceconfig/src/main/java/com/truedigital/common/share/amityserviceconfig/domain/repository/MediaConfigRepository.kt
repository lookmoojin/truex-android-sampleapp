package com.truedigital.common.share.amityserviceconfig.domain.repository

import kotlinx.coroutines.flow.Flow

interface MediaConfigRepository {
    fun getFeatureConfigMediaConfig(country: String): Flow<Boolean>
}
