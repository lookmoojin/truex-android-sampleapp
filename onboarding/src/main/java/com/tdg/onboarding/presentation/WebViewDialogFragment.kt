package com.tdg.onboarding.presentation

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import com.tdg.onboarding.R
import com.tdg.onboarding.databinding.DialogWebviewBinding
import com.tdg.onboarding.injections.WhatNewComponent
import com.truedigital.core.extensions.viewBinding
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import timber.log.Timber

class WebViewDialogFragment : DialogFragment(R.layout.dialog_webview) {

    companion object {
        private const val JS_ZOOM = "javascript:document.getElementsByName('viewport')[0]" +
                ".setAttribute('content', 'initial-scale=1.0,maximum-scale=10.0');"
        private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Mobile Safari/537.36"
        val TAG = WebViewDialogFragment::class.java.simpleName
        private const val JAVASCRIPT_INTERFACE_NAME = "AndroidWebView"
        private const val KEY_URL = "KEY_URL"

        fun newInstance(
            url: String,
        ): WebViewDialogFragment {
            return WebViewDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_URL, url)
                }
            }
        }
    }

    private val binding: DialogWebviewBinding by viewBinding(DialogWebviewBinding::bind)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        WhatNewComponent.getInstance().inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullscreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWebView()
        initOnClick()
        binding.appWebView.loadUrl(arguments?.getString(KEY_URL).orEmpty())
    }

    private fun initOnClick() = with(binding) {
        webViewCloseImageView.onClick {
            dismiss()
        }

        webViewAppBackwardImageView.onClick {
            binding.appWebView.apply {
                if (canGoBack()) {
                    goBack()
                }
            }
        }

        webViewAppForwardImageView.onClick {
            binding.appWebView.apply {
                if (canGoForward()) {
                    goForward()
                }
            }
        }

        webViewAppRightImageView.apply {
            setOnClickListener { rightImageView ->
                try {
                    val popupMenu = PopupMenu(context, rightImageView)
                    val intent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(appWebView.originalUrl))
                    val mainLauncherList = context.packageManager.queryIntentActivities(intent, 0)
                    popupMenu.menuInflater.inflate(R.menu.webviewapp_more_menu, popupMenu.menu)
                    if (intent.resolveActivity(context.packageManager) != null) {
                        popupMenu.menu.findItem(R.id.webviewapp_open_default_browser).apply {
                            isVisible = true
                            title = getString(
                                R.string.webviewapp_open_browser,
                                context.packageManager.getApplicationLabel(mainLauncherList[0].activityInfo.applicationInfo)
                            )
                        }
                    } else {
                        popupMenu.menu.findItem(R.id.webviewapp_open_default_browser).apply {
                            isVisible = false
                        }
                    }
                    popupMenu.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.webviewapp_refresh -> appWebView.reload()
                            R.id.webviewapp_open_default_browser -> {
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    startActivity(intent)
                                }
                            }
                        }
                        true
                    }
                    popupMenu.show()
                } catch (exception: Exception) {
                    Timber.e(exception)
                }
            }
        }
    }

    private fun initWebView() {
        webViewJavaScripInterface()
        webViewSetting()
        webViewChrome()
        webViewClient()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun webViewSetting() = with(binding) {
        appWebView.apply {
            clearCache(true)
            clearHistory()
            clearSslPreferences()
            settings.apply {
                allowContentAccess = true
                databaseEnabled = true
                mediaPlaybackRequiresUserGesture = false
                WebView.setWebContentsDebuggingEnabled(true)
                allowFileAccess = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                javaScriptEnabled = true
                domStorageEnabled = true
                loadsImagesAutomatically = true
                loadWithOverviewMode = true
                pluginState = WebSettings.PluginState.ON
                useWideViewPort = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                userAgentString = USER_AGENT
            }
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            CookieManager.setAcceptFileSchemeCookies(true)
            CookieManager.getInstance().flush()
        }
    }

    private fun webViewChrome() = with(binding) {
        appWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (isAdded) {
                    binding.webViewAppProgressBar.progress = newProgress
                }
            }
        }
    }

    private fun webViewClient() {
        object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (isAdded) {
                    binding.webViewAppProgressBar.gone()
                    binding.webViewAppHeaderTextView.text = view?.originalUrl.orEmpty()
                    view?.loadUrl(JS_ZOOM)
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (isAdded) {
                    binding.webViewAppProgressBar.visible()
                    binding.webViewAppHeaderTextView.text = view?.originalUrl.orEmpty()
                }
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                Timber.e(Exception("Response WebView Error"))
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }
        }.also { binding.appWebView.webViewClient = it }
    }

    private fun webViewJavaScripInterface() {
        binding.appWebView.addJavascriptInterface(JavascriptInterface(), JAVASCRIPT_INTERFACE_NAME)
    }

    internal inner class JavascriptInterface {
        @android.webkit.JavascriptInterface
        fun reloadWithActualSize(updatedHtml: String) {
            binding.appWebView.loadDataWithBaseURL(
                "file:///android_asset/",
                updatedHtml,
                "text/html",
                "UTF-8",
                ""
            )
        }
    }
}