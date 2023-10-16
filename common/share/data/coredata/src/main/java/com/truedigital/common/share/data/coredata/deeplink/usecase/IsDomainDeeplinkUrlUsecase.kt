package com.truedigital.common.share.data.coredata.deeplink.usecase

import java.net.URI
import java.net.URL
import javax.inject.Inject

interface IsDomainDeeplinkUrlUseCase {
    fun execute(domainList: List<String>, stringUrl: String): Boolean
}

class IsDomainDeeplinkUrlUseCaseImpl @Inject constructor() : IsDomainDeeplinkUrlUseCase {
    override fun execute(domainList: List<String>, stringUrl: String): Boolean {
        val host = getHost(stringUrl)
        return domainList.find { domain ->
            domain == host
        }?.isNotEmpty() == true
    }

    private fun getHost(stringUrl: String): String {
        return when {
            stringUrl.startsWith("https://") || stringUrl.startsWith("http://") -> URL(stringUrl).host.orEmpty()
            stringUrl.startsWith("trueid://") -> URI(stringUrl).authority.orEmpty()
            else -> ""
        }
    }
}
