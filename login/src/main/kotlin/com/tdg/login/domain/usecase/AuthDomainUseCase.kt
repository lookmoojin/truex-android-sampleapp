package com.tdg.login.domain.usecase

import com.truedigital.common.share.datalegacy.domain.endpoint.usecase.GetApiConfigurationUseCase
import javax.inject.Inject

interface AuthDomainUseCase {
    fun save(isPreProd: Boolean)
    fun execute(): String
}

class AuthDomainUseCaseImpl @Inject constructor(
    private val getApiConfigurationUseCase: GetApiConfigurationUseCase
) : AuthDomainUseCase {

    companion object {
        var PREPROD_STATE = true
        const val PREPROD = "https://iam.trueid-preprod.net/"
        const val PROD = "https://iam.trueid.net/"
    }

    override fun save(isPreProd: Boolean) {
        getApiConfigurationUseCase.saveEnvPreprod(isPreProd)
        PREPROD_STATE = isPreProd
    }

    override fun execute(): String {
        return if (PREPROD_STATE) {
            PREPROD
        } else {
            PROD
        }
    }
}
