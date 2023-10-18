package com.truedigital.core.extensions

import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView

/**
 * Created by nut.tang on 12/14/2017 AD.
 */

fun WebView.applyTrueIDSetting() {
    this.settings.allowFileAccessFromFileURLs = true
    this.settings.allowUniversalAccessFromFileURLs = true
    this.settings.loadsImagesAutomatically = true
    this.settings.pluginState = WebSettings.PluginState.ON
    this.settings.javaScriptEnabled = true
    this.settings.domStorageEnabled = true
    this.settings.javaScriptCanOpenWindowsAutomatically = true
    this.webChromeClient = WebChromeClient()
    this.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
}
