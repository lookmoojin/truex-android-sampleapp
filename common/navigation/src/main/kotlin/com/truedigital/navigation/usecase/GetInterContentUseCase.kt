package com.truedigital.navigation.usecase

import com.google.gson.JsonArray
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.data.CommonViewModel
import com.truedigital.core.data.ShelfSkeleton
import com.truedigital.core.extensions.collectSafe
import com.truedigital.navigation.data.repository.GetCacheInterContentRepository
import com.truedigital.navigation.data.repository.GetInterContentRepository
import com.truedigital.navigation.domain.usecase.GetPersonaConfigUseCase
import com.truedigital.navigation.domain.usecase.GetTodayPersonaSegmentEnableUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

interface GetInterContentUseCase {
    fun execute(url: String): Flow<List<ShelfSkeleton>>
}

class GetInterContentUseCaseImpl @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val getInterContentRepository: GetInterContentRepository,
    private val getCacheInterContentRepository: GetCacheInterContentRepository,
    private val getPersonaConfigUseCase: GetPersonaConfigUseCase,
    private val getTodayPersonaSegmentEnableUseCase: GetTodayPersonaSegmentEnableUseCase,
    private val loginManagerInterface: LoginManagerInterface
) :
    GetInterContentUseCase {

    companion object {
        private const val FOOTER = "footer"
        private const val THEME = "theme"
        private const val HEADER = "header"
        private const val SHELF_ID = "shelfId"
        private const val SHELF_CODE = "shelfCode"
        private const val SHELF_CUSTOM_TYPE = "shelf_custom_type"
        private const val ITEMS = "items"
        private const val TODAY_PAGE = "today"
        private const val PLACEMENT_ID = "placement_id"
        private const val TYPE_CALL_API = "type_call_api"
        private const val DELAY_TIMES = 1000L
        private const val COMPONENT_NAME_TRUEPOINT = "truepoint"
        private const val COMPONENT_NAME_TRUESCORE = "truescore"
        private const val COMPONENT_NAME_INBOX = "inbox"
        private const val VIEW_TYPE_COMPONENT = "shelf_component"
    }

    override fun execute(url: String): Flow<List<ShelfSkeleton>> {
        return if (getTodayPersonaSegmentEnableUseCase.execute() &&
            loginManagerInterface.isLoggedIn() &&
            url.contains(TODAY_PAGE)
        ) {
            getConfigFromPersona(url = url)
        } else {
            getContentFromUrl(url = url)
        }
    }

    private fun getConfigFromPersona(url: String): Flow<List<ShelfSkeleton>> {
        var personaUrl = ""
        return flow {
            getPersonaConfigUseCase.execute()
                .flowOn(coroutineDispatcher.io())
                .flatMapConcat { personalDomainData ->
                    personaUrl = personalDomainData.url
                    getInterContentRepository.getContentFeed(personalDomainData.url)
                }.map {
                    getCacheInterContentRepository.updateContentFeedCache(personaUrl, it)
                    mapIntentContentToSkeleton(it)
                }.catch { e ->
                    if (personaUrl.isNotEmpty()) {
                        getCacheInterContentRepository.getContentFeed(personaUrl)?.let {
                            emit(mapIntentContentToSkeleton(it))
                        } ?: run {
                            getContentFromUrl(url = url).collectSafe {
                                emit(it)
                            }
                        }
                    } else {
                        getContentFromUrl(url = url).collectSafe {
                            emit(it)
                        }
                    }
                }.collectSafe {
                    emit(it)
                }
        }
    }

    private fun getContentFromUrl(url: String): Flow<List<ShelfSkeleton>> {
        return flow {
            getInterContentRepository.getContentFeed(url).map {
                getCacheInterContentRepository.updateContentFeedCache(url, it)
                mapIntentContentToSkeleton(it)
            }.catch { e ->
                Timber.e(e)
                getCacheInterContentRepository.getContentFeed(url)?.let {
                    emit(mapIntentContentToSkeleton(it))
                } ?: run {
                    throw e
                }
            }.collectSafe {
                emit(it)
            }
        }
    }

    private fun mapIntentContentToSkeleton(jsonArray: JsonArray): List<ShelfSkeleton> {
        val skeletonList = mutableListOf<ShelfSkeleton>()
        jsonArray.forEachIndexed { index, jsonElement ->
            try {
                jsonElement.asJsonObject.get(HEADER)?.asJsonObject?.let { json ->
                    if (json.size() > 0) {
                        json.add(THEME, jsonElement.asJsonObject.get(THEME))
                        json.add(ITEMS, jsonElement.asJsonObject.get(ITEMS))
                        json.add(PLACEMENT_ID, jsonElement.asJsonObject.get(PLACEMENT_ID))
                        json.add(TYPE_CALL_API, jsonElement.asJsonObject.get(TYPE_CALL_API))
                        json.add(SHELF_CUSTOM_TYPE, jsonElement.asJsonObject.get(SHELF_CUSTOM_TYPE))
                        json.add(SHELF_CODE, jsonElement.asJsonObject.get(SHELF_ID))
                        skeletonList.add(
                            ShelfSkeleton(
                                json,
                                jsonElement.asJsonObject.get(SHELF_ID)?.asString
                                    ?: index.toString(),
                                index = index
                            ).apply {
                                timeStamp = getTimeStamp()
                                originalShelfIndex = index
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            skeletonList.add(
                ShelfSkeleton(
                    json = jsonElement.asJsonObject,
                    index = index
                ).apply {
                    timeStamp = getTimeStamp()
                    originalShelfIndex = index
                }
            )
            try {
                jsonElement.asJsonObject.get(FOOTER)?.asJsonObject?.let { json ->
                    if (json.has("data")) {
                        skeletonList.add(
                            ShelfSkeleton(
                                json,
                                jsonElement.asJsonObject.get(SHELF_ID)?.asString
                                    ?: index.toString(),
                                index = index
                            ).apply {
                                timeStamp = getTimeStamp()
                                originalShelfIndex = index
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return if (!loginManagerInterface.isLoggedIn()) {
            processToFilterShelf(skeletonList.toMutableList())
        } else {
            addShelfIndex(skeletonList.toMutableList())
        }
    }

    private fun getTimeStamp(): Long {
        return System.currentTimeMillis() / DELAY_TIMES
    }

    private fun addShelfIndex(filterShelfList: List<ShelfSkeleton>): List<ShelfSkeleton> {
        filterShelfList.forEachIndexed { index, shelfSkeleton ->
            shelfSkeleton.index = index
        }

        val groupByList = filterShelfList.groupBy {
            it.vType?.contains(CommonViewModel.Header.HeaderHorizontal.VIEW_TYPE) == true
        }
        val headerList = groupByList[true]?.toMutableList()
        val noHeaderList = groupByList[false]?.toMutableList()

        return if (noHeaderList != null && headerList != null) {
            val shelfSkeletonNewIndex = addShelfIndexToSkeletonNoHeader(noHeaderList)
            val shelfSkeletonList = addHeaderToSkeletonList(shelfSkeletonNewIndex, headerList)
            shelfSkeletonList
        } else {
            emptyList()
        }
    }

    private fun addShelfIndexToSkeletonNoHeader(skeletonList: MutableList<ShelfSkeleton>): MutableList<ShelfSkeleton> {
        skeletonList.forEachIndexed { index, shelfSkeleton ->
            shelfSkeleton.shelfIndex = index
        }
        return skeletonList
    }

    private fun addHeaderToSkeletonList(
        skeletonList: MutableList<ShelfSkeleton>,
        headerList: MutableList<ShelfSkeleton>
    ): MutableList<ShelfSkeleton> {
        headerList.forEach {
            skeletonList.add(it.index, it)
        }

        return skeletonList
    }

    private fun processToFilterShelf(skeletonList: MutableList<ShelfSkeleton>): List<ShelfSkeleton> {
        val filterShelfList = skeletonList.filterNot {
            if (it.vType?.contains(VIEW_TYPE_COMPONENT) == true) {
                val modelComponent = it.items?.firstOrNull()
                modelComponent?.let { _modelComponent ->
                    val shelfComponentName =
                        _modelComponent.asJsonObject.get("data").asJsonObject.get("setting").asJsonObject.get(
                            "component_name"
                        ).asString ?: ""
                    return@filterNot shelfComponentName == COMPONENT_NAME_TRUEPOINT ||
                        shelfComponentName == COMPONENT_NAME_TRUESCORE || shelfComponentName == COMPONENT_NAME_INBOX
                }
            }
            false
        }
        return addShelfIndex(filterShelfList)
    }
}
