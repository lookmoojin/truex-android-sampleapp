package com.truedigital.common.share.datalegacy.data.endpoint

import com.truedigital.common.share.datalegacy.domain.endpoint.usecase.GetApiConfigurationUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiConfigurationManager @Inject constructor(
    private val getApiConfigurationUseCase: GetApiConfigurationUseCase
) {

    fun getUrl(serviceName: String): String {
        return getApiConfigurationUseCase.getServiceUrl(serviceName)
    }

    fun getUrl(isLoggedIn: Boolean, serviceName: String): String {
        return getApiConfigurationUseCase.getServiceUrl(isLoggedIn, serviceName)
    }

    fun getToken(serviceName: String): String {
        return getApiConfigurationUseCase.getServiceToken(serviceName)
    }

    fun hasApiConfiguration(serviceName: String): Boolean {
        return getApiConfigurationUseCase.hasApiConfiguration(serviceName)
    }

    fun isApiSupportJwt(serviceName: String): Boolean {
        return getApiConfigurationUseCase.isApiSupportJwt(serviceName)
    }
}
