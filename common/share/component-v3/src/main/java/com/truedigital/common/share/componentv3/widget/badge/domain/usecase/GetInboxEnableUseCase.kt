package com.truedigital.common.share.componentv3.widget.badge.domain.usecase

import com.truedigital.common.share.componentv3.widget.badge.data.repository.ConfigInboxEnableRepository
import io.reactivex.Observable
import javax.inject.Inject

interface GetInboxEnableUseCase {
    fun execute(): Observable<Boolean>
}

class GetInboxEnableUseCaseImpl @Inject constructor(private val configInboxEnableRepository: ConfigInboxEnableRepository) :
    GetInboxEnableUseCase {
    override fun execute(): Observable<Boolean> {
        return configInboxEnableRepository.isEnableInbox()
    }
}
