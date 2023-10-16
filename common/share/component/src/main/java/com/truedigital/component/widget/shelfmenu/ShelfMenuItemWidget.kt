package com.truedigital.component.widget.shelfmenu

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.truedigital.component.databinding.WidgetShelfMenuItemBinding
import com.truedigital.component.view.AppTextView
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.invisible
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.visible

class ShelfMenuItemWidget : FrameLayout {

    private val widgetShelfMenuItemBinding: WidgetShelfMenuItemBinding by lazy {
        WidgetShelfMenuItemBinding.inflate(LayoutInflater.from(context))
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addView(widgetShelfMenuItemBinding.root)
    }

    fun setShelfMenuText(text: String) {
        widgetShelfMenuItemBinding.shelfMenuTextView.text = text
    }

    fun setFont(font: String) {
        widgetShelfMenuItemBinding.shelfMenuTextView.setFont(font)
    }

    fun setShelfMenuTextSize(unit: Int, size: Float) {
        widgetShelfMenuItemBinding.shelfMenuTextView.setTextSize(unit, size)
    }

    fun setShelfMenuTextColor(color: Int) {
        widgetShelfMenuItemBinding.shelfMenuTextView.setTextColor(color)
    }

    fun setShelfMenuTextBackground(color: Int) {
        widgetShelfMenuItemBinding.shelfMenuTextView.setBackgroundColor(color)
    }

    fun setShelfMenuImageView(url: String) {
        runCatching {
            widgetShelfMenuItemBinding.shelfMenuImageView.load(context, url)
            widgetShelfMenuItemBinding.shelfMenuImageView.visible()
        }
    }

    fun hideShelfMenuImageView() {
        widgetShelfMenuItemBinding.shelfMenuImageView.gone()
    }

    fun showShelfMenuTextView() {
        widgetShelfMenuItemBinding.shelfMenuTextView.visible()
    }

    fun hideShelfMenuTextView() {
        widgetShelfMenuItemBinding.shelfMenuTextView.invisible()
    }

    fun hideRedLine() {
        widgetShelfMenuItemBinding.redLineView.invisible()
    }

    fun showRedLine() {
        widgetShelfMenuItemBinding.redLineView.visible()
    }

    fun getShelfMenuTextView(): AppTextView = widgetShelfMenuItemBinding.shelfMenuTextView

    fun getShelfMenuImageView(): ImageView = widgetShelfMenuItemBinding.shelfMenuImageView
}
