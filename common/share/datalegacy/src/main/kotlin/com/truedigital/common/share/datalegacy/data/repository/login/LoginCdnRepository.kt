package com.truedigital.common.share.datalegacy.data.repository.login

import com.truedigital.common.share.datalegacy.data.login.api.LoginCdnApiInterface
import com.truedigital.common.share.datalegacy.data.login.model.DefaultUrlConfigResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface LoginCdnRepository {
    fun getCdnLoginUrl(): Flow<Result<DefaultUrlConfigResponse?>>
    fun clearCache()
}

class LoginCdnRepositoryImpl @Inject constructor(
    private val api: LoginCdnApiInterface
) : LoginCdnRepository {

    companion object {
        private const val CDN_ERROR = "Failed to get CDN API"
    }

    private var cache: DefaultUrlConfigResponse? = null

    override fun getCdnLoginUrl(): Flow<Result<DefaultUrlConfigResponse?>> {
        return flow {
            cache?.let {
                emit(Result.success(cache))
            } ?: run {
                val response = api.getCdnUrlConfig()
                if (response.isSuccessful) {
                    val data = response.body()
                    cache = data
                    emit(Result.success(data))
                } else {
                    emit(Result.failure(IllegalStateException(CDN_ERROR)))
                }
            }
        }
    }

    override fun clearCache() {
        cache = null
    }
}
