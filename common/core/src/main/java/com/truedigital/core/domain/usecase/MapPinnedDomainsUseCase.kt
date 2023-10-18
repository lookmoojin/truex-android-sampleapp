package com.truedigital.core.domain.usecase

import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

interface MapPinnedDomainsUseCase {
    fun execute(): Map<String, List<String>>
}

class MapPinnedDomainsUseCaseImpl @Inject constructor(
    private val getPinnedDomainsUseCase: GetPinnedDomainsUseCase
) : MapPinnedDomainsUseCase {
    override fun execute(): Map<String, List<String>> {
        val pinnedDomains = getPinnedDomainsUseCase.execute()
        val mapPinnedDomains = mutableMapOf<String, List<String>>()
        runBlocking {
            pinnedDomains.map { pinned ->
                launch {
                    val urls = listOfNotNull(pinned.controlList, pinned.blackBox).flatten()
                    urls.forEach { control ->
                        pinned.urls?.let { url ->
                            mapPinnedDomains[control] = url
                        }
                    }
                }
            }.joinAll()
        }
        return mapPinnedDomains
    }
}
