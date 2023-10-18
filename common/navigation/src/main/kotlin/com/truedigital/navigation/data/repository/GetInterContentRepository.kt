package com.truedigital.navigation.data.repository

import com.google.gson.JsonArray
import com.truedigital.navigation.data.PersonaData
import com.truedigital.navigation.data.api.PersonaApiInterface
import com.truedigital.navigations.share.data.api.InterCdnApiInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

interface GetInterContentRepository {
    fun getContentFeed(url: String): Flow<JsonArray>
    fun getPersonaData(country: String, lang: String): Flow<PersonaData>
}

class GetInterContentRepositoryImpl @Inject constructor(
    private val api: InterCdnApiInterface,
    private val personaApi: PersonaApiInterface
) : GetInterContentRepository {

    companion object {
        private const val PROGRESSIVE_SHELF_SUCCESS_CODE = 10001
        private const val PLACEMENT_ID = "trueid-personalize-shelf-puerto"
        private const val PAGE_NAME = "today"
    }

    override fun getContentFeed(url: String): Flow<JsonArray> {
        return flow {
            val dataFeed = api.getContent(url)
            emit(dataFeed)
        }
    }

    override fun getPersonaData(country: String, lang: String): Flow<PersonaData> {
        return flow {
            val response = personaApi.getPersonaData(
                country = country,
                lang = lang,
                pageName = PAGE_NAME,
                placementId = PLACEMENT_ID
            )
            if (response.isSuccessful &&
                response.body()?.code == PROGRESSIVE_SHELF_SUCCESS_CODE &&
                response.body()?.data != null
            ) {
                emit(response.body()?.data ?: PersonaData())
            } else {
                throw HttpException(response)
            }
        }
    }
}
