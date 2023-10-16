package com.truedigital.common.share.componentv3.widget.badge.domain.usecase

import com.truedigital.common.share.componentv3.widget.badge.data.repository.CountInboxRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetTotalUnseenUseCase {
    fun execute(isLoadOnly: Boolean): Flow<Int>
}

class GetTotalUnseenUseCaseImpl @Inject constructor(
    private val countInboxRepository: CountInboxRepository,
    private val getNewCountInboxUseCase: GetNewCountInboxUseCase
) : GetTotalUnseenUseCase {

    var isFirstTime = false

    override fun execute(isLoadOnly: Boolean): Flow<Int> {
        return if (isLoadOnly || !isFirstTime) {
            isFirstTime = true
            countInboxRepository.getCountInbox().map {
                it.data?.totalUnseens ?: getNewCountInboxUseCase.execute()
            }
        } else {
            flow {
                getNewCountInboxUseCase.execute()
            }
        }
    }
}
