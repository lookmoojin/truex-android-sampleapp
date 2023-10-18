package com.tdg.login.domain.usecase

import javax.inject.Inject

interface AuthDomainUseCase {
    fun save(isPreProd: Boolean)
    fun execute(): String
}

class AuthDomainUseCaseImpl @Inject constructor() : AuthDomainUseCase {

    companion object {
        var PREPROD_STATE = true
        const val PREPROD = "https://iam.trueid-preprod.net/"
        const val PROD = "https://iam.trueid.net/"
    }

    override fun save(isPreProd: Boolean) {
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
