package com.truedigital.common.share.datalegacy.domain.endpoint.usecase

import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.share.data.firestoreconfig.domainconfig.repository.DomainRepository
import javax.inject.Inject

interface GetApiConfigurationUseCase {
    fun getServiceUrl(serviceName: String): String

    fun getServiceUrl(isLoggedIn: Boolean, serviceName: String): String

    fun getServiceToken(serviceName: String): String

    fun hasApiConfiguration(serviceName: String): Boolean

    fun isApiSupportJwt(serviceName: String): Boolean
}

class GetApiConfigurationUseCaseImpl @Inject constructor(
    private val apiConfigurationRepository: DomainRepository,
    private val loginManagerInterface: LoginManagerInterface
) : GetApiConfigurationUseCase {

    override fun hasApiConfiguration(serviceName: String): Boolean {
        val apiServiceData = apiConfigurationRepository.getApiServiceData(serviceName)
        return apiServiceData != null
    }

    override fun isApiSupportJwt(serviceName: String): Boolean {
        return apiConfigurationRepository.getApiServiceData(serviceName)?.useJwt ?: false
    }

    override fun getServiceUrl(serviceName: String): String {
        val apiServiceData = apiConfigurationRepository.getApiServiceData(serviceName)

        return if (apiServiceData != null) {
            val isLogin = loginManagerInterface.isLoggedIn()
            val nonLoginUrl = apiServiceData.nonLoginUrl ?: ""
            val loginUrl = apiServiceData.loginUrl ?: ""

            if (!isLogin) {
                nonLoginUrl
            } else {
                loginUrl
            }
        } else {
            ""
        }
    }

    override fun getServiceUrl(isLoggedIn: Boolean, serviceName: String): String {
        val apiServiceData = apiConfigurationRepository.getApiServiceData(serviceName)

        return if (apiServiceData != null) {
            val nonLoginUrl = apiServiceData.nonLoginUrl ?: ""
            val loginUrl = apiServiceData.loginUrl ?: ""

            if (!isLoggedIn) {
                nonLoginUrl
            } else {
                loginUrl
            }
        } else {
            ""
        }
    }

    override fun getServiceToken(serviceName: String): String {
        val apiServiceData = apiConfigurationRepository.getApiServiceData(serviceName)
        return apiServiceData?.apiToken ?: ""
    }
}
