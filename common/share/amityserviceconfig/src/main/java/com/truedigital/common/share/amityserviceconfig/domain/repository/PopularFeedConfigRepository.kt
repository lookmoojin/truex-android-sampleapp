package com.truedigital.common.share.amityserviceconfig.domain.repository

import kotlinx.coroutines.flow.Flow

interface PopularFeedConfigRepository {
    fun getFeatureConfigPopularFeed(country: String): Flow<Boolean>
}
