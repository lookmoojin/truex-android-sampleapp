package com.truedigital.common.share.datalegacy.navigation

import android.content.Intent
import com.truedigital.foundation.extension.SingleLiveEvent

object NavigationManager {

    private val actionRequest = SingleLiveEvent<NavigationRequest>()
    private val actionIntentRequest = SingleLiveEvent<Intent>()

    private val actionReplaceFragment = SingleLiveEvent<Unit>()
    private val actionPopFragment = SingleLiveEvent<Unit>()

    fun getActionRequest(): SingleLiveEvent<NavigationRequest> {
        return actionRequest
    }

    fun getActionIntentRequest(): SingleLiveEvent<Intent> {
        return actionIntentRequest
    }

    fun setActionRequest(request: NavigationRequest) {
        actionRequest.value = request
    }

    fun setActionIntentRequest(intent: Intent) {
        actionIntentRequest.value = intent
    }

    fun setActionReplaceFragment() {
        actionReplaceFragment.value = Unit
    }

    fun getActionReplaceFragment(): SingleLiveEvent<Unit> {
        return actionReplaceFragment
    }

    fun setActionPopFragment() {
        actionPopFragment.value = Unit
    }

    fun getActionPopFragment(): SingleLiveEvent<Unit> {
        return actionPopFragment
    }
}
