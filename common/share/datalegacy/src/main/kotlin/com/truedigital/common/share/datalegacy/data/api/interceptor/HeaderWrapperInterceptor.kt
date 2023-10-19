package com.truedigital.common.share.datalegacy.data.api.interceptor

import com.truedigital.common.share.datalegacy.constant.ContentNodeConstant
import com.truedigital.common.share.datalegacy.data.api.di.TokenInterceptor
import com.truedigital.common.share.datalegacy.data.endpoint.ApiConfigurationManager
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderWrapperInterceptor @Inject constructor(
    private val apiConfigurationManager: ApiConfigurationManager,
    private val loginManagerInterface: LoginManagerInterface,
    @TokenInterceptor private val requestTokenInterceptor: Interceptor,
    private val headerInterceptor: HeaderInterceptor,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val pathSegments = chain.request().url.pathSegments
        val featurePath = pathSegments.first()

        val isApiSupportJwt = apiConfigurationManager.isApiSupportJwt(featurePath)
        return if (isApiSupportJwt && loginManagerInterface.isLoggedIn()) {
            if (featurePath == ContentNodeConstant.PATH_CONTENT_CUSTOMER) {
                requestTokenInterceptor.intercept(chain)
            } else requestTokenInterceptor.intercept(chain)
        } else {
            headerInterceptor.intercept(chain)
        }
    }
}
