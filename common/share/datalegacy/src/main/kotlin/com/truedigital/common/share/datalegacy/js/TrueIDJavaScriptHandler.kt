package com.truedigital.common.share.datalegacy.js

import com.google.gson.JsonObject
import com.truedigital.authentication.javascript.JavaScriptHandler
import com.truedigital.common.share.datalegacy.data.repository.login.StateUserLoginRepository
import com.truedigital.common.share.datalegacy.domain.other.usecase.GetNetworkConnectedTypeUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.GetNetworkConnectedTypeUseCaseImpl
import com.truedigital.common.share.datalegacy.injections.DataLegacyComponent
import org.json.JSONObject
import javax.inject.Inject

class TrueIDJavaScriptHandler : JavaScriptHandler {
    private companion object {
        const val KEY_COMMAND = "command"
        const val KEY_MOBILE_NUMBER = "mobile_number"
        const val KEY_COUNTRY_CODE = "country_code"
        const val KEY_CONNECTION_TYPE = "connection_type"

        const val COMMAND_GET_PHONE_NUMBER = "get_phone_number"
        const val COMMAND_CHECK_CELLULAR_NETWORK = "check_cellular_network"

        const val VALUE_THAILAND_CODE = "66"
    }

    @Inject
    lateinit var stateUserLoginRepository: StateUserLoginRepository

    @Inject
    lateinit var getNetworkConnectedTypeUseCase: GetNetworkConnectedTypeUseCase

    init {
        DataLegacyComponent.getInstance().inject(this)
    }

    override fun handleJavaScript(params: String, callback: (payload: String) -> Unit) {
        val jsonParams = JSONObject(params)
        when (val command = jsonParams.optString(KEY_COMMAND)) {
            COMMAND_GET_PHONE_NUMBER -> {
                if (stateUserLoginRepository.isOpenByPass()) {
                    val payload = JsonObject().apply {
                        addProperty(KEY_COMMAND, command)
                        addProperty(KEY_MOBILE_NUMBER, stateUserLoginRepository.getMobileNumber())
                        addProperty(KEY_COUNTRY_CODE, VALUE_THAILAND_CODE)
                    }
                    callback(payload.toString())
                }
            }
            COMMAND_CHECK_CELLULAR_NETWORK -> {
                val payload = JsonObject().apply {
                    addProperty(KEY_COMMAND, command)
                    when (val connectionType = getNetworkConnectedTypeUseCase.execute()) {
                        GetNetworkConnectedTypeUseCaseImpl.CELLULAR,
                        GetNetworkConnectedTypeUseCaseImpl.WIFI -> {
                            addProperty(KEY_CONNECTION_TYPE, connectionType)
                        }
                        else -> {
                            addProperty(KEY_CONNECTION_TYPE, "")
                        }
                    }
                }
                callback(payload.toString())
            }
        }
    }
}
