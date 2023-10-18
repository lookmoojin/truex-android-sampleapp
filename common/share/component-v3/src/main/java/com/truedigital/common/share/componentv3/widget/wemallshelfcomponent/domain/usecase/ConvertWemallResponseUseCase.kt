package com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.usecase

import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.model.WeMallParametersModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.model.WeMallShelfItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ConvertWeMallResponseUseCase {
    fun execute(
        content: WeMallShelfResponseModel,
        parametersModel: WeMallParametersModel?
    ): Flow<WeMallShelfItemModel>
}

class ConvertWeMallResponseUseCaseImpl @Inject constructor() : ConvertWeMallResponseUseCase {
    override fun execute(
        content: WeMallShelfResponseModel,
        parametersModel: WeMallParametersModel?
    ): Flow<WeMallShelfItemModel> {

        return flow {
            content.data?.get(0)?.items?.let { _items ->
                _items.forEach { responseItem ->
                    val shelfItem = WeMallShelfItemModel(
                        itemUrl = responseItem.itemUrl.orEmpty(),
                        categoryUrl = content.data[0].categoryUrl.orEmpty(),
                        thumb = responseItem.thumbnail.orEmpty(),
                        cmsId = responseItem.id ?: "",
                        title = responseItem.title ?: "",
                        categoryName = parametersModel?.setting?.category ?: "",
                        shelfName = parametersModel?.title ?: "",
                        shelfCode = parametersModel?.id ?: ""
                    )
                    emit(shelfItem)
                }
            }
        }
    }
}
