package com.tdg.onboarding.domain.usecase

import com.tdg.onboarding.data.repository.WhatNewConfigRepository
import com.tdg.onboarding.data.repository.WhatsNewDataCheckRepository
import com.tdg.onboarding.domain.model.WhatNewData
import com.tdg.onboarding.domain.model.WhatNewType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetWhatNewConfigUseCase {
    fun execute(): Flow<WhatNewData?>
}

class GetWhatNewConfigUseCaseImpl @Inject constructor(
    private val whatNewConfigRepository: WhatNewConfigRepository,
    private val whatsNewDataCheckRepository: WhatsNewDataCheckRepository
) : GetWhatNewConfigUseCase {

    override fun execute(): Flow<WhatNewData?> {
        return flow {
            val response = whatNewConfigRepository.getConfig()
            emit(
                if (response?.android?.enable == true) {
                    val android = response.android
                    val timestamp = response.android.timestamp
                    if (whatsNewDataCheckRepository.getTimeStamp() == timestamp) {
                        null
                    } else {
                        whatsNewDataCheckRepository.saveTimeStamp(timestamp.orEmpty())
                        WhatNewData(
                            imageMobile = android.imageUrl?.mobile.orEmpty(),
                            imageTablet = android.imageUrl?.tablet.orEmpty(),
                            type = mapWhatNewType(android.type.orEmpty()),
                            url = android.url.orEmpty()
                        )
                    }
                } else {
                    null
                }
            )
        }
    }

    private fun mapWhatNewType(type: String): WhatNewType {
        return if (type.equals(WhatNewType.INAPPBROWSER.value, true)) {
            WhatNewType.INAPPBROWSER
        } else if (type.equals(WhatNewType.EXTERNALBROWSER.value, true)) {
            WhatNewType.EXTERNALBROWSER
        } else {
            WhatNewType.NONE
        }
    }
}
