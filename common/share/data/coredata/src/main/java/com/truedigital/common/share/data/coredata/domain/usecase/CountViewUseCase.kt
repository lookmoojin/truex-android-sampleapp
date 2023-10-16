package com.truedigital.common.share.data.coredata.domain.usecase

import com.truedigital.common.share.data.coredata.data.api.CmsFnCounterApiInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface CountViewUseCase {
    fun execute(cmsId: String): Flow<Unit>
}

class CountViewUseCaseImpl @Inject constructor(
    private val api: CmsFnCounterApiInterface
) : CountViewUseCase {

    override fun execute(cmsId: String): Flow<Unit> {
        return flow {
            api.getCountView(cmsId)
            emit(Unit)
        }
    }
}
