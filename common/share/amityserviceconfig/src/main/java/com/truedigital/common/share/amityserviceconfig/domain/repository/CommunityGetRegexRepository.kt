package com.truedigital.common.share.amityserviceconfig.domain.repository

import kotlinx.coroutines.flow.Flow

interface CommunityGetRegexRepository {
    fun getFeatureConfigRegex(country: String): Flow<String>
}
