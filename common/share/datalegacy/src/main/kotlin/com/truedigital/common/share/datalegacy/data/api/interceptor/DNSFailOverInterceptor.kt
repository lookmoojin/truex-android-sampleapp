package com.truedigital.common.share.datalegacy.data.api.interceptor

import com.truedigital.core.utils.networkconnection.ConnectivityStateHolder
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

const val ERROR_CODE_SERVICE_INTERNAL = 500
const val ERROR_CODE_NO_INTERNET_CONNECTION = 12163
const val ERROR_CODE_SERVICE_GATEWAY_TIMEOUT = 504

class DNSFailOverInterceptor(private val connectionPool: ConnectionPool) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val response = chain.proceed(chain.request())
            when {
                !ConnectivityStateHolder.isConnected -> {
                    getErrorResponse(chain, "No network connected")
                }
                response.code in ERROR_CODE_SERVICE_INTERNAL..ERROR_CODE_SERVICE_GATEWAY_TIMEOUT && isRetryAllowed(
                    chain.request()
                ) -> {
                    connectionPool.evictAll()
                    response.close()
                    chain.proceed(chain.request())
                }
                else -> response
            }
        } catch (e: IOException) {
            getErrorResponse(chain, e.message ?: "Exception No network connected")
        } catch (e: OutOfMemoryError) {
            getErrorResponse(chain, e.message ?: "Exception Out of memory")
        }
    }

    // TODO workaround and need to refactor disallow retry some api
    private fun isRetryAllowed(request: Request): Boolean {
        return request.url.pathSegments.first() != "tyclm-redemption"
    }

    // TODO workaround
    // https://stackoverflow.com/questions/37540616/do-we-have-any-possibility-to-stop-request-in-okhttp-interceptor/37546919#37546919
    private fun getErrorResponse(chain: Interceptor.Chain, message: String): Response {
        return Response.Builder()
            .code(ERROR_CODE_NO_INTERNET_CONNECTION)
            .body(message.toResponseBody(null))
            .protocol(Protocol.HTTP_2)
            .message(message)
            .request(chain.request())
            .build()
    }
}
