package com.truedigital.common.share.componentv3.widget.badge.data.repository

import com.truedigital.common.share.componentv3.widget.badge.data.api.NotificationBadgeApiInterface
import com.truedigital.common.share.componentv3.widget.badge.data.model.CountCategoriesInboxResponse
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface CountInboxRepository {
    fun getCountInbox(): Flow<CountCategoriesInboxResponse>
}

class CountInboxRepositoryImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val apiNotificationBadge: NotificationBadgeApiInterface
) : CountInboxRepository {

    override fun getCountInbox(): Flow<CountCategoriesInboxResponse> {
        return flow {
            emit(
                apiNotificationBadge.getCountInboxMessage(userRepository.getSsoId())
            )
        }
    }
}
