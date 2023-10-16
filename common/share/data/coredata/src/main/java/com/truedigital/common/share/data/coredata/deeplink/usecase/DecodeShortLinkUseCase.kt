package com.truedigital.common.share.data.coredata.deeplink.usecase

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.appsflyer.AppsFlyerLib
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants
import com.truedigital.core.extensions.addOnCanceledListenerWithNewExecutor
import com.truedigital.core.extensions.addOnFailureListenerWithNewExecutor
import com.truedigital.core.extensions.addOnSuccessListenerWithNewExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.net.URI
import javax.inject.Inject

interface DecodeShortLinkUseCase {
    fun execute(stringUrl: String, context: Context?): Flow<String>
}

class DecodeShortLinkUseCaseImpl @Inject constructor(
    private val firebaseDynamicLinks: FirebaseDynamicLinks,
    private val appsFlyerLib: AppsFlyerLib,
    private val isDynamicUrlUseCase: IsDynamicUrlUseCase,
    private val isPrivilegeUrlUseCase: IsPrivilegeUrlUseCase,
) : DecodeShortLinkUseCase {

    override fun execute(stringUrl: String, context: Context?): Flow<String> {
        return callbackFlow {
            when {
                isPrivilegeUrlUseCase.execute(stringUrl) -> {
                    trySendBlocking(stringUrl)
                }

                isDynamicUrlUseCase.execute(stringUrl) -> {
                    val uri = Uri.parse(stringUrl)
                    firebaseDynamicLinks.apply {
                        getDynamicLink(Intent())
                        getDynamicLink(uri)
                            .addOnSuccessListenerWithNewExecutor { pendingDynamicLinkData ->
                                trySendBlocking(pendingDynamicLinkData?.link?.toString() ?: "")
                            }
                            .addOnFailureListenerWithNewExecutor {
                                trySendBlocking(stringUrl)
                            }.addOnCanceledListenerWithNewExecutor {
                                trySendBlocking(stringUrl)
                            }
                    }
                }
                stringUrl.contains(DeeplinkConstants.DeeplinkConstants.HOST_ONELINK) ||
                    stringUrl.contains(DeeplinkConstants.DeeplinkConstants.HOST_ONELINK_TTID) ||
                    stringUrl.contains("af_dp") ||
                    stringUrl.contains("deep_link_value") -> {
                    context?.let { _context ->
                        appsFlyerLib.performOnAppAttribution(_context, URI.create(stringUrl))
                    }
                    trySendBlocking("")
                }
                else -> {
                    trySendBlocking(stringUrl)
                }
            }
            awaitClose { channel.close() }
        }
    }
}
