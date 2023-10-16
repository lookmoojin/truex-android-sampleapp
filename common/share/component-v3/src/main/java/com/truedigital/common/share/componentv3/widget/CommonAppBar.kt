package com.truedigital.common.share.componentv3.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.amity.socialcloud.uikit.community.newsfeed.activity.AmityPostTargetPickerActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.common.IconGravity
import com.truedigital.common.share.componentv3.data.CommonAppbarViewType
import com.truedigital.common.share.componentv3.databinding.CommonAppbarBinding
import com.truedigital.common.share.componentv3.injections.ComponentV3Component
import com.truedigital.common.share.componentv3.widget.feedmenutab.CommunityTab
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.model.CommunityTabDataModel
import com.truedigital.common.share.componentv3.widget.feedmenutab.presentation.CommunityTabEnum
import com.truedigital.common.share.componentv3.widget.searchanimation.model.SearchAnimationData
import com.truedigital.common.share.componentv3.widget.searchanimation.presentation.SearchAnimationViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.extension.visibleAnimation
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject
import kotlin.math.abs

class CommonAppBar : AppBarLayout, LifecycleObserver {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val searchAnimationViewModel: SearchAnimationViewModel by lazy {
        viewModelFactory.create(SearchAnimationViewModel::class.java)
    }

    private var largeTitle = ""
    private var contentDescriptionTitle = ""
    private var isIconVisible = true
    private var iconList: List<Triple<View, IconGravity, Int>> = mutableListOf()

    var isShowTitle = true
    var onClickAnimation: ((String) -> Unit)? = null
    var onClickSearch: (() -> Unit)? = null
    var onClickScan: (() -> Unit)? = null
    var onClickClear: (() -> Unit)? = null
    var onNavigateToAmityPost: ((AmityPostTargetPickerActivity.CreationType) -> Unit)? = null
    var navigateToCommunityTab: (() -> Unit)? = null
    var keepShowingCommonButton = false
    var collapsedTextGravity = Gravity.CENTER_VERTICAL

    companion object {
        private const val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.4f
        private const val COLLAPSING_SIZE = 115
        private const val COLLAPSING_40 = 40
        private const val COLLAPSING_50 = 50
    }

    private val binding: CommonAppbarBinding by lazy {
        CommonAppbarBinding.inflate(LayoutInflater.from(context), this, false)
    }

    constructor(context: Context) : super(context) {
        initialView(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialView(attrs)
    }

    init {
        ComponentV3Component.getInstance().inject(this)
    }

    private fun buildViewSpace(): View {
        return View(context).apply {
            layoutParams = LinearLayoutCompat.LayoutParams(0, 1, 1f)
        }
    }

    private fun getStyledAttributes(attrs: AttributeSet?) {
        attrs?.let { _attrs ->
            val typedArray =
                context?.obtainStyledAttributes(_attrs, R.styleable.CommonAppBar)
            largeTitle = typedArray?.getString(
                R.styleable.CommonAppBar_cab_title
            ).orEmpty()
            contentDescriptionTitle = typedArray?.getString(
                R.styleable.CommonAppBar_cab_title_content_description
            ).orEmpty()
            collapsedTextGravity = typedArray?.getInt(
                R.styleable.CommonAppBar_cab_collapse_title_gravity,
                collapsedTextGravity
            ) ?: collapsedTextGravity
            typedArray?.recycle()
        }
    }

    private fun handleIconVisibility(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (isIconVisible) {
                iconList.filter { (_, _, visibility) ->
                    visibility != View.VISIBLE
                }.forEach { (view, _, _) ->
                    view.visibility = View.GONE
                }
                isIconVisible = false
            }
        } else {
            if (isIconVisible.not()) {
                iconList.filter { (_, _, visibility) ->
                    visibility != View.VISIBLE
                }.forEach { (view, _, _) ->
                    view.visibility = View.VISIBLE
                }
                isIconVisible = true
            }
        }
    }

    private fun handleTitle(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (isShowTitle.not()) {
                binding.appCollapsingToolbar.setCollapsedTitleTextColor(Color.TRANSPARENT)
                binding.toolbar.visibleAnimation(View.INVISIBLE)
            }
        } else {
            if (isShowTitle.not()) {
                binding.appCollapsingToolbar.setCollapsedTitleTextColor(Color.BLACK)
            }
        }
    }

    private fun handleTitleImageAnimation(
        percentage: Float,
        fromHeight: Int,
        toHeight: Int,
        fromStartMargin: Int,
        toStartMargin: Int
    ) {
        val layoutParams = binding.titleImage.layoutParams as MarginLayoutParams

        // Update Height
        val currentHeight = layoutParams.height
        val newHeight =
            calValueByPercentage(percentage, fromHeight.toFloat(), toHeight.toFloat()).toInt()

        if (newHeight != currentHeight) {
            layoutParams.height = newHeight
            binding.titleImage.layoutParams = layoutParams
        }

        // Update Margin
        val currentStartMargin = layoutParams.marginStart
        val newStartMargin = calValueByPercentage(
            percentage,
            fromStartMargin.toFloat(),
            toStartMargin.toFloat()
        ).toInt()

        if (newStartMargin != currentStartMargin) {
            layoutParams.marginStart = newStartMargin
            binding.titleImage.layoutParams = layoutParams
        }
    }

    private fun handleCommonButtonFloatAnimation(
        percentage: Float,
        fromHeight: Int,
        toHeight: Int,
        fromStartMargin: Int,
        toStartMargin: Int
    ) {
        val layoutParams = binding.commonBtn.layoutParams as MarginLayoutParams

        // Update Height
        val currentHeight = layoutParams.height
        val newHeight =
            calValueByPercentage(percentage, fromHeight.toFloat(), toHeight.toFloat()).toInt()

        if (newHeight != currentHeight) {
            layoutParams.height = newHeight
            binding.commonBtn.layoutParams = layoutParams
        }

        // Update Margin
        val currentStartMargin = layoutParams.marginStart
        val newStartMargin = calValueByPercentage(
            percentage,
            fromStartMargin.toFloat(),
            toStartMargin.toFloat()
        ).toInt()

        if (newStartMargin != currentStartMargin) {
            layoutParams.marginStart = newStartMargin
            binding.commonBtn.layoutParams = layoutParams
        }
    }

    private fun handleCommonButtonAnimation(percentage: Float, fromValue: Float, toValue: Float) {
        val currentValue = binding.commonBtn.alpha
        val newValue = calValueByPercentage(percentage, fromValue, toValue)

        if (newValue != currentValue) {
            binding.commonBtn.alpha = newValue
        }
    }

    private fun calValueByPercentage(percentage: Float, fromValue: Float, toValue: Float): Float {
        return if (fromValue < toValue) {
            fromValue + ((toValue - fromValue) * percentage)
        } else {
            fromValue - ((fromValue - toValue) * percentage)
        }
    }

    private fun initialView(attrs: AttributeSet?) {
        getStyledAttributes(attrs)
        setupView()
        setupListener()
        setupOffsetListener()
    }

    private fun setAnimationVisible() {
        binding.commonSearchBar.setAnimationVisible()
    }

    private fun setCollapsedMarginTitle(listIconView: List<Triple<View, IconGravity, Int>>) {
        val defaultWidth = 32 * resources.displayMetrics.density.toInt()
        var marginLeft = 16 * resources.displayMetrics.density.toInt()
        listIconView.filter { (_, iconGravity, visibility) ->
            iconGravity == IconGravity.LEFT && visibility == View.VISIBLE
        }.forEach { (view, _, _) ->
            marginLeft += if (view.layoutParams.width > 0) view.layoutParams.width else defaultWidth
        }.also {
            val marginRight = when (collapsedTextGravity) {
                Gravity.CENTER,
                Gravity.CENTER_HORIZONTAL -> marginLeft
                else -> 0
            }
            binding.toolbar.setContentInsetsAbsolute(marginLeft, marginRight)
        }
    }

    private fun setPlayAndHideAnimationAfterFinishedShowing() {
        binding.commonSearchBar.setPlayAndHideAnimationAfterFinishedShowing()
    }

    private fun setSearchAnimationFile(adsUrl: String) {
        binding.commonSearchBar.setSearchAnimationFile(adsUrl)
    }

    private fun setupListener() {
        binding.apply {
            commonSearchBar.apply {
                onClickSearch = {
                    this@CommonAppBar.onClickSearch?.invoke()
                }
                onClickScan = {
                    this@CommonAppBar.onClickScan?.invoke()
                }
                onClickClear = {
                    this@CommonAppBar.onClickClear?.invoke()
                }
            }
            communityShortCut.apply {
                onNavigateToCommunityPage = {
                    this@CommonAppBar.navigateToCommunityTab?.invoke()
                }
                onNavigateToAmityPost = {
                    this@CommonAppBar.onNavigateToAmityPost?.invoke(it)
                }
            }
            scanIconImageView.onClick {
                this@CommonAppBar.onClickScan?.invoke()
            }
            searchIconImageView.onClick {
                this@CommonAppBar.onClickSearch?.invoke()
            }
        }
    }

    private fun setupOffsetListener() {
        val layoutParamsImageTitle = binding.titleImage.layoutParams as MarginLayoutParams
        val fromHeightImageTitle = layoutParamsImageTitle.height
        val toHeightImageTitle = (24 * resources.displayMetrics.density).toInt()
        val fromStartMarginImageTitle = layoutParamsImageTitle.marginStart
        val toStartMarginImageTitle = (32 * resources.displayMetrics.density).toInt()

        val layoutPagerCommonButton = binding.commonBtn.layoutParams as MarginLayoutParams
        val fromHeightCommonButton = layoutPagerCommonButton.height
        val toHeightCommonButton = (32 * resources.displayMetrics.density).toInt()
        val fromStartMarginCommonButton = layoutPagerCommonButton.marginStart

        this.addOnOffsetChangedListener(
            OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val maxScroll = appBarLayout.totalScrollRange
                val percentage: Float = abs(verticalOffset).toFloat() / maxScroll.toFloat()
                handleIconVisibility(percentage)
                handleTitle(percentage)

                if (binding.titleImage.isVisible) {
                    handleTitleImageAnimation(
                        percentage,
                        fromHeightImageTitle,
                        toHeightImageTitle,
                        fromStartMarginImageTitle,
                        toStartMarginImageTitle
                    )
                }

                if (binding.commonBtn.isVisible) {
                    if (keepShowingCommonButton) {
                        handleCommonButtonFloatAnimation(
                            percentage,
                            fromHeightCommonButton,
                            toHeightCommonButton,
                            fromStartMarginCommonButton,
                            fromStartMarginCommonButton
                        )
                    } else {
                        handleCommonButtonAnimation(percentage, 1f, 0f)
                    }
                }
            }
        )
    }

    private fun setupSearchAnimation(searchAnimationData: SearchAnimationData) {
        val adsUrl: String = searchAnimationData.adsUrl
        val deeplink: String = searchAnimationData.deeplink
        this@CommonAppBar.apply {
            setAnimationVisible()
            setSearchAnimationFile(adsUrl)
            setPlayAndHideAnimationAfterFinishedShowing()
        }
        binding.commonSearchBar.apply {
            onClickAnimation = {
                this@CommonAppBar.onClickAnimation?.invoke(deeplink)
            }
        }
    }

    private fun setupView() {
        addView(binding.root)
        setTitle(largeTitle)
        setTitleConTentDescription(contentDescriptionTitle)
        this.setBackgroundResource(R.color.transparent)
    }

    fun addIconView(listIconView: List<Triple<View, IconGravity, Int>>) {
        iconList = listIconView
        if (iconList.isEmpty()) {
            binding.appCollapsingToolbar.gone()
        } else {
            binding.let {
                it.appCollapsingToolbar.visible()
                it.iconLinearLayoutCompat.removeAllViews()
            }

            val countIconLeft = iconList.filter { (_, iconGravity, _) ->
                iconGravity == IconGravity.LEFT
            }.size

            iconList.sortedBy {
                IconGravity.LEFT
            }.map {
                it.first
            }.forEachIndexed { index, iconView ->
                if (index == countIconLeft) {
                    binding.iconLinearLayoutCompat.addView(
                        buildViewSpace()
                    )
                    binding.iconLinearLayoutCompat.addView(iconView)
                } else {
                    binding.iconLinearLayoutCompat.addView(iconView)
                }
            }

            setCollapsedMarginTitle(iconList)
        }
    }

    fun setContentDescription(contentDescription: String) {
        binding.commonSearchBar.contentDescription = contentDescription
    }

    fun setEditText(keyword: String) {
        binding.commonSearchBar.setEditText(keyword)
        binding.commonSearchBar.setHideQRScan()
        binding.commonSearchBar.setShowClearTextButton()
    }

    fun setHintText(hintText: String) {
        binding.commonSearchBar.setHintText(hintText)
        binding.commonSearchBar.setHideQRScan()
    }

    fun setSearchAnimation(pageKey: String, lifecycleOwner: LifecycleOwner) {
        with(searchAnimationViewModel) {
            onShowSearchAnimationData().observe(
                lifecycleOwner
            ) { searchAnimationData ->
                searchAnimationData?.let {
                    setupSearchAnimation(it)
                }
            }
            getSearchAnimation(pageKey)
        }
    }

    fun setCommunityTab(lifecycleOwner: LifecycleOwner) {
        binding.communityShortCut.setObserveViewModel(lifecycleOwner)
    }

    fun setSearchListener(listener: CommonSearchBar.OnSearchInteraction) {
        binding.commonSearchBar.setSearchListener(listener)
    }

    fun setTitle(title: String) {
        largeTitle = title
        binding.appCollapsingToolbar.run {
            this.title = title
            collapsedTitleGravity = collapsedTextGravity
        }
    }

    fun setTitleConTentDescription(contentDescription: String) {
        contentDescriptionTitle = contentDescription
        binding.appCollapsingToolbar.contentDescription = contentDescription
    }

    fun setTitleImage(imageUrl: String?) {
        if (imageUrl.isNullOrEmpty()) {
            binding.titleImage.isVisible = false
            binding.appCollapsingToolbar.title = largeTitle
        } else {
            binding.titleImage.load(context, imageUrl)
            binding.titleImage.isVisible = true
            binding.appCollapsingToolbar.title = ""
        }
    }

    fun renderViewCommonAppbar(commonAppbarViewTypeList: List<CommonAppbarViewType>) =
        with(binding) {
            var sizeCollapsing = COLLAPSING_SIZE
            commonAppbarViewTypeList.forEach { typeView ->
                when (typeView) {
                    CommonAppbarViewType.SHOW_ICON_ROW -> {
                        iconSecondRowLinearLayoutCompat.visible()
                    }
                    CommonAppbarViewType.SHOW_COMMUNITY_SHORTCUT -> {
                        communityShortCut.visible()
                        sizeCollapsing += COLLAPSING_40
                    }
                    CommonAppbarViewType.SHOW_SEARCH -> {
                        commonSearchBar.visible()
                        sizeCollapsing += COLLAPSING_40
                    }
                    CommonAppbarViewType.SHOW_COMMUNITY_TAB -> {
                        todayCommunityTabLayout.visible()
                        sizeCollapsing += COLLAPSING_50
                    }
                }
            }

            val pixels: Float = sizeCollapsing * context.resources.displayMetrics.density
            appCollapsingToolbar.layoutParams.height = pixels.toInt()
        }

    fun clearSearchText() {
        binding.commonSearchBar.clearSearchText()
    }

    fun setHideClearButton() {
        binding.commonSearchBar.setHideClearButton()
    }

    fun setupEdittextAfterSearch() {
        binding.commonSearchBar.setupEdittextAfterSearch()
    }

    fun setTimeAutoComplete(time: Long) {
        binding.commonSearchBar.setTimeInterval(time)
    }

    fun setCommonButton(displayText: String, isShow: Boolean, keepShowing: Boolean) {
        binding.commonBtn.text = displayText
        binding.commonBtn.isVisible = isShow
        keepShowingCommonButton = keepShowing
    }

    fun onCommonButtonClick(callback: (() -> Unit)) {
        binding.commonBtn.onClick {
            callback.invoke()
        }
    }

    fun getTextFromEditText(): String = binding.commonSearchBar.getTextFromEditText()

    fun requestFocusAndShowKeyboard() {
        binding.commonSearchBar.requestFocusAndShowKeyboard()
    }

    fun clearFocusAndHideKeyboard() {
        binding.commonSearchBar.clearFocusAndHideKeyboard()
    }

    fun setLockCollapsible() {
        val param = binding.appCollapsingToolbar.layoutParams as LayoutParams
        binding.appCollapsingToolbar.layoutParams = param.apply {
            scrollFlags = LayoutParams.SCROLL_FLAG_SNAP
        }
    }

    fun setCommunityTabTitle(communityTabData: CommunityTabDataModel) {
        binding.todayCommunityTabLayout.setCommunityTabTitle(communityTabData)
    }

    fun onSwitchTabCommunityTab(tabType: CommunityTabEnum) {
        binding.todayCommunityTabLayout.onSwitchTab(tabType)
    }

    fun setSelectFeedTabMenuListener(selectFeedTabMenuListener: CommunityTab.SelectFeedTabMenuListener) {
        binding.todayCommunityTabLayout.setSelectFeedTabMenuListener(selectFeedTabMenuListener)
    }
}
