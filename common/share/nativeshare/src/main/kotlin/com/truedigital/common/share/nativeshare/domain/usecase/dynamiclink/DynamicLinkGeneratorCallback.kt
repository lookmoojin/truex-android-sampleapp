package com.truedigital.common.share.nativeshare.domain.usecase.dynamiclink

interface DynamicLinkGeneratorCallback {
    fun onLoading()
    fun onSuccess(shortUrl: String)
    fun onFailure(errorMessage: String)
}
