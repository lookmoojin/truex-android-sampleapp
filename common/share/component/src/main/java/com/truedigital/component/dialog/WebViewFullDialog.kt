package com.truedigital.component.dialog

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import com.truedigital.common.share.datalegacy.navigation.CoreArgs
import com.truedigital.common.share.datalegacy.navigation.navArguments
import com.truedigital.component.R
import com.truedigital.component.base.BaseDialogFragment
import com.truedigital.component.databinding.DialogFriendConditionBinding
import com.truedigital.foundation.extension.onClick
import org.apache.http.util.EncodingUtils

class WebViewFullDialog : BaseDialogFragment() {

    private val dialogFriendConditionBinding: DialogFriendConditionBinding by lazy {
        DialogFriendConditionBinding.inflate(LayoutInflater.from(context))
    }
    private var url: String = ""
    private var params: String = ""
    private var progressDialog: TrueIDProgressDialog? = null

    private val coreArgs: CoreArgs by navArguments()

    companion object {

        private const val KEY_WEBVIEW_URL = "KEY_WEBVIEW_URL"
        private const val KEY_WEBVIEW_POST_PARAMS = "KEY_WEBVIEW_POST_PARAMS"

        fun newInstance(url: String): WebViewFullDialog {
            val dialog = WebViewFullDialog()
            val bundle = Bundle().apply {
                putString(KEY_WEBVIEW_URL, url)
            }
            dialog.arguments = bundle
            return dialog
        }

        fun newInstance(url: String, postParams: String): WebViewFullDialog {
            return WebViewFullDialog().apply {
                arguments = Bundle().apply {
                    putString(KEY_WEBVIEW_URL, url)
                    putString(KEY_WEBVIEW_POST_PARAMS, postParams)
                }
            }
        }
    }

    init {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TrueChannelTheme)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { arguments ->
            coreArgs.shelfModel?.let { shelf ->
                url = shelf.info?.apiUrl.orEmpty()
                params = shelf.info?.params.orEmpty()
            }

            if (arguments.containsKey(KEY_WEBVIEW_URL)) {
                url = arguments.getString(KEY_WEBVIEW_URL, "")
            }

            if (arguments.containsKey(KEY_WEBVIEW_POST_PARAMS)) {
                params = arguments.getString(KEY_WEBVIEW_POST_PARAMS, "")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return dialogFriendConditionBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogFriendConditionBinding.closeButton.onClick {
            dismiss()
        }

        dialogFriendConditionBinding.webView.settings.apply {
            domStorageEnabled = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            loadsImagesAutomatically = true
            pluginState = WebSettings.PluginState.ON
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        dialogFriendConditionBinding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    hideProgressDialog()
                }
            }
        }
        dialogFriendConditionBinding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if (isAdded) {
                    showProgressDialog(resources.getString(R.string.loading))
                }
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                hideProgressDialog()
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                hideProgressDialog()
                super.onReceivedError(view, request, error)
            }
        }

        if (params.isEmpty()) {
            dialogFriendConditionBinding.webView.loadUrl(url)
        } else {
            dialogFriendConditionBinding.webView.postUrl(
                url,
                EncodingUtils.getBytes(params, "BASE64")
            )
        }
    }

    fun isShowing(): Boolean {
        val dialog = dialog
        return dialog?.isShowing == true
    }

    private fun showProgressDialog(msg: String) {
        activity?.let { activity ->
            if (!activity.isFinishing) {
                if (progressDialog?.isShowing == true) {
                    hideProgressDialog()
                }
                context?.let {
                    progressDialog = TrueIDProgressDialog(it).apply {
                        setCancelable(false)
                    }
                }
                if (msg.isNotEmpty()) {
                    progressDialog?.show(msg)
                } else {
                    progressDialog?.show()
                }
            }
        }
    }

    private fun hideProgressDialog() {
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }
}
