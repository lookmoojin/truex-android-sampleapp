package com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.usecase

import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfRequestModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.repository.WeMallShelfRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetWeMallShelfContentUseCase {
    fun execute(
        token: String,
        request: WeMallShelfRequestModel
    ): Flow<WeMallShelfResponseModel?>
}

class GetWeMallShelfContentUseCaseImpl @Inject constructor(
    private val weMallShelfRepository: WeMallShelfRepository
) : GetWeMallShelfContentUseCase {

    override fun execute(
        token: String,
        request: WeMallShelfRequestModel
    ): Flow<WeMallShelfResponseModel?> {
        return weMallShelfRepository.getWeMallShelfComponent(token, request)
    }
}
