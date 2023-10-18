package com.truedigital.common.share.componentv3.widget.badge.domain.usecase

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

interface CountInboxUseCase {
    fun execute(): MutableLiveData<Unit>
}

class CountInboxUseCaseImpl @Inject constructor() : CountInboxUseCase {
    override fun execute() = MutableLiveData<Unit>()
}
