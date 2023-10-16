package com.truedigital.common.share.componentv3.widget.badge.domain.usecase

import com.truedigital.common.share.componentv3.widget.badge.data.repository.CountInboxRepository
import com.truedigital.common.share.componentv3.widget.badge.model.CategoriesInboxModel
import com.truedigital.common.share.componentv3.widget.badge.model.CountInboxModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetServiceCountInboxUseCase {
    fun execute(): Flow<CountInboxModel?>
}

class GetServiceCountInboxUseCaseImpl @Inject constructor(
    private val countInboxRepository: CountInboxRepository,
) : GetServiceCountInboxUseCase {

    override fun execute(): Flow<CountInboxModel?> {
        return countInboxRepository.getCountInbox().map {
            it.data?.let { data ->
                val listCategory = data.details?.map { detail ->
                    CategoriesInboxModel(detail.category ?: "", detail.unseen)
                }
                CountInboxModel(data.totalUnseens, listCategory)
            }
        }
    }
}
