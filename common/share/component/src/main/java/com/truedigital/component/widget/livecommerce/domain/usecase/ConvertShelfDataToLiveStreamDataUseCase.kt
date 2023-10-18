package com.truedigital.component.widget.livecommerce.domain.usecase

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.truedigital.component.widget.livecommerce.domain.model.CommerceShelfResponseItems
import javax.inject.Inject

interface ConvertShelfDataToLiveStreamDataUseCase {
    fun execute(shelfItemData: String): Triple<String, String, String>
}

class ConvertShelfDataToLiveStreamDataUseCaseImpl @Inject constructor(
    private val gson: Gson
) : ConvertShelfDataToLiveStreamDataUseCase {
    override fun execute(shelfItemData: String): Triple<String, String, String> {
        return try {
            val shelfData = gson.fromJson(shelfItemData, CommerceShelfResponseItems::class.java)
            return Triple(
                shelfData.setting?.title.orEmpty(),
                shelfData.setting?.navigate.orEmpty(),
                shelfData.setting?.ssoIdList.orEmpty()
            )
        } catch (e: JsonSyntaxException) {
            return Triple("", "", "")
        }
    }
}
