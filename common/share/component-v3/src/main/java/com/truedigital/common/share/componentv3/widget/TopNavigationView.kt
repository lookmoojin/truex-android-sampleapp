package com.truedigital.common.share.componentv3.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayout
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ItemTopNavigationIconOrTextBinding
import com.truedigital.common.share.componentv3.databinding.ItemTopNavigationIconTextBinding
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.loadWithImageCallback
import com.truedigital.foundation.extension.visible

class TopNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TabLayout(
    context,
    attrs,
    defStyleAttr
) {
    var onItemSelected: ((position: Int, slug: String) -> Unit)? = null
    var onItemReSelected: ((position: Int, slug: String) -> Unit)? = null

    private var itemContentDescription: String = ""
    private var itemImageViewContentDescription: String = ""
    private var itemTextViewContentDescription: String = ""

    init {
        setupStyleable(attrs)
        initView()
    }

    fun addItem(
        label: String,
        slug: String,
        iconUrl: String,
        isFirstItem: Boolean,
        isLastItem: Boolean,
        contentDesc: String = ""
    ) {
        val tab = createTabView(label, slug, iconUrl, contentDesc)
        addItemTabView(
            tab,
            isFirstItem = isFirstItem,
            isLastItem = isLastItem,
        )
    }

    fun addItemIconOrText(
        label: String,
        slug: String,
        iconUrl: String,
        isFirstItem: Boolean,
        isLastItem: Boolean
    ) {
        val tab = createTabIconOrTextView(label, slug, iconUrl)
        addItemTabView(
            tab,
            isFirstItem = isFirstItem,
            isLastItem = isLastItem,
        )
    }

    fun addItems(dataList: List<Pair<String, String>>) {
        for (i in dataList.indices) {
            val data = dataList[i]
            if (!getTabAt(i)?.tag?.toString().equals(data.second, true)) {
                val tab = createTabView(
                    label = data.first,
                    slug = data.second,
                    iconUrl = ""
                )
                addItemTabView(
                    tab,
                    isFirstItem = i == dataList.indices.first,
                    isLastItem = i == dataList.indices.last
                )
            }
        }
    }

    private fun addItemTabView(
        tab: Tab,
        isFirstItem: Boolean,
        isLastItem: Boolean
    ) {
        addTab(tab)
        val marginParent =
            resources.getDimensionPixelSize(R.dimen.top_navigation_space_between_parent)
        val marginItem =
            resources.getDimensionPixelSize(R.dimen.top_navigation_space_between_item)
        tab.apply {
            disableToolTip()
            val marginStart = if (isFirstItem) marginParent else marginItem
            val marginEnd = if (isLastItem) marginParent else marginItem
            (view.layoutParams as? MarginLayoutParams)?.setMargins(marginStart, 0, marginEnd, 0)
            view.requestLayout()
        }
    }

    fun clearItems() {
        removeAllTabs()
    }

    fun setPagePosition(position: Int) {
        postDelayed(
            {
                getTabAt(position)?.select()
            },
            100
        )
    }

    private fun setupStyleable(attrs: AttributeSet?) {
        attrs.let { _attrs ->
            val typedArray = context?.obtainStyledAttributes(
                _attrs,
                R.styleable.TopNavigationView
            )
            itemContentDescription = typedArray?.getString(
                R.styleable.TopNavigationView_item_content_description
            ).orEmpty()
            itemImageViewContentDescription = typedArray?.getString(
                R.styleable.TopNavigationView_item_image_view_content_description
            ).orEmpty()
            itemTextViewContentDescription = typedArray?.getString(
                R.styleable.TopNavigationView_item_text_view_content_description
            ).orEmpty()
            typedArray?.recycle()
        }
    }

    private fun initView() {
        addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab) {
                onItemSelected?.invoke(tab.position, tab.tag.toString())
            }

            override fun onTabUnselected(tab: Tab) {
                Unit
            }

            override fun onTabReselected(tab: Tab) {
                tab.view.isSelected = true
                onItemReSelected?.invoke(tab.position, tab.tag.toString())
            }
        })
    }

    private fun createTabView(
        label: String,
        slug: String,
        iconUrl: String,
        contentDesc: String = ""
    ): Tab {
        return newTab().apply {
            this.tag = slug
            this.contentDescription = contentDesc
            this.view.children.forEach { child ->
                if (child is TextView) {
                    child.contentDescription = contentDesc
                }
            }
            if (label.isNotEmpty() && iconUrl.isNotEmpty()) {
                val binding = ItemTopNavigationIconTextBinding.inflate(LayoutInflater.from(context))
                    .apply {
                        topNavIconImageView.load(context, iconUrl)
                        topNavTitleTextView.text = label

                        contentDescription = itemContentDescription
                        topNavIconImageView.contentDescription = itemImageViewContentDescription
                        topNavTitleTextView.contentDescription = itemTextViewContentDescription
                    }
                this.customView = binding.root
            } else if (iconUrl.isEmpty()) {
                this.text = label
            } else {
                val img = ImageView(context)
                img.contentDescription = itemImageViewContentDescription
                img.load(context, iconUrl, placeholder = R.drawable.ic_logo_true_id)
                this.customView = img
            }
        }
    }

    private fun createTabIconOrTextView(
        label: String,
        slug: String,
        iconUrl: String
    ): Tab {
        return newTab().apply {
            this.tag = slug
            this.contentDescription = itemContentDescription
            val binding = ItemTopNavigationIconOrTextBinding.inflate(LayoutInflater.from(context))
            val topNavIconImageView = binding.topNavIconImageView
            val topNavTitleTextView = binding.topNavTitleTextView

            val hasIconUrl = iconUrl.isNotEmpty()

            topNavTitleTextView.apply {
                text = label
                isVisible = !hasIconUrl
            }

            if (hasIconUrl) {
                topNavIconImageView.apply {
                    visible()
                    loadWithImageCallback(
                        context, iconUrl, onSuccess = {
                            topNavIconImageView.visible()
                            topNavTitleTextView.gone()
                        }, onError = {
                            topNavIconImageView.gone()
                            topNavTitleTextView.visible()
                        }
                    )
                }
            }

            contentDescription = itemContentDescription
            topNavIconImageView.contentDescription = itemImageViewContentDescription
            topNavTitleTextView.contentDescription = itemTextViewContentDescription

            this.customView = binding.root
        }
    }

    private fun Tab.disableToolTip() {
        this.view.setOnLongClickListener {
            true
        }
    }
}
