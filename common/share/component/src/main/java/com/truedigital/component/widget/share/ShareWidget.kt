package com.truedigital.component.widget.share

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LifecycleOwner
import com.truedigital.component.R
import com.truedigital.component.injections.TIDComponent
import com.truedigital.foundation.extension.onClick
import javax.inject.Inject

class ShareWidget : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @Inject
    lateinit var viewModel: ShareWidgetViewModel

    init {
        TIDComponent.getInstance().inject(this)

        setImageResource(R.drawable.ic_share)
    }

    fun start(
        lifecycleOwner: LifecycleOwner,
        shareWidgetModel: ShareWidgetModel,
        isOneLink: Boolean = false
    ) {
        initView(isOneLink)
        setupViewModel(lifecycleOwner)
        viewModel.setShareWidgetModel(shareWidgetModel)
    }

    private fun initView(isOneLink: Boolean) {
        onClick {
            if (isOneLink) {
                viewModel.generateOneLink()
            } else {
                viewModel.generateDynamicLink()
            }
        }
    }

    private fun setupViewModel(lifecycleOwner: LifecycleOwner) {
        viewModel.onShareSocial().observe(
            lifecycleOwner,
            { shortenUrl ->
                showShareSocial(shortenUrl)
            }
        )
        viewModel.onShowShareErrorMessage().observe(
            lifecycleOwner,
            { errorMessage ->
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun showShareSocial(shortenUrl: String) {
        if (shortenUrl.isNotEmpty()) {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_TEXT, shortenUrl)
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }
    }

    fun setCustomImageResource(resId: Int) {
        setImageResource(resId)
    }
}
