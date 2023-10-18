package com.truedigital.common.share.datalegacy.domain.login.usecase

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.truedigital.common.share.datalegacy.data.repository.login.LoginCdnRepository
import com.truedigital.core.BuildConfig
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.extensions.environmentCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetLoginUrlUseCase {
    fun execute(): Flow<String>
}

class GetLoginUrlUseCaseImpl @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
    private val loginCdnRepository: LoginCdnRepository
) : GetLoginUrlUseCase {

    companion object {
        private const val KEY_REMOTE_CONFIG_AAA_SIGNIN_URL = "aaa_signin_flow"
    }

    override fun execute(): Flow<String> {
        return flow {
            val remoteConfigUrl = firebaseRemoteConfig.getString(KEY_REMOTE_CONFIG_AAA_SIGNIN_URL)

            if (remoteConfigUrl.isNotEmpty()) {
                emit(remoteConfigUrl)
            } else {
                loginCdnRepository.getCdnLoginUrl().collectSafe { response ->
                    val config = response.getOrNull()
                    val url = BuildConfig.ENVIRONMENT.environmentCase(
                        staging = {
                            config?.alpha?.signInUrl
                        },
                        preProd = {
                            config?.preprod?.signInUrl
                        },
                        prod = {
                            config?.production?.signInUrl
                        }
                    )

                    url?.let {
                        emit(url)
                    } ?: run {
                        emit(BuildConfig.REMOTE_CONFIG_DEFAULT_AAA_SIGNIN_URL)
                    }
                }
            }
        }
    }
}
