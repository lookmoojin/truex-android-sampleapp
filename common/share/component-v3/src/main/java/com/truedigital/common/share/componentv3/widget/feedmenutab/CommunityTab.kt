package com.truedigital.common.share.componentv3.widget.feedmenutab

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleObserver
import com.amity.socialcloud.sdk.social.community.AmityCommunity
import com.amity.socialcloud.sdk.social.feed.AmityPost
import com.amity.socialcloud.uikit.AmityUIKitClient
import com.amity.socialcloud.uikit.feed.settings.AmityCommunityShareClickListener
import com.amity.socialcloud.uikit.feed.settings.AmityPostShareClickListener
import com.amity.socialcloud.uikit.feed.settings.AmityPostSharingSettings
import com.amity.socialcloud.uikit.feed.settings.AmityPostSharingTarget
import com.truedigital.common.share.communityshare.presentation.viewmodel.CommunityShareViewModel
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.CommonCommunityTabBinding
import com.truedigital.common.share.componentv3.injections.ComponentV3Component
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.model.CommunityTabDataModel
import com.truedigital.common.share.componentv3.widget.feedmenutab.presentation.CommunityTabEnum
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class CommunityTab : FrameLayout, LifecycleObserver {

    interface SelectFeedTabMenuListener {
        fun onTabSelected(tabSelected: CommunityTabEnum, titleTab: String)
    }

    companion object {
        private const val EXTRA_PADDING_TEXT = 10
        private const val SELECTED_BUTTON_HEIGHT = 40
        private const val UNSELECTED_BUTTON_HEIGHT = 40
        private const val SELECTED_BUTTON_TOP_MARGIN = 2
        private const val UNSELECTED_BUTTON_TOP_MARGIN = 2
    }

    private val binding: CommonCommunityTabBinding by lazy {
        CommonCommunityTabBinding.inflate(LayoutInflater.from(context))
    }
    private var selectFeedTabMenuListener: SelectFeedTabMenuListener? = null
    private var selectedButton: AppCompatButton? = null
    private var unselectedButton: AppCompatButton? = null
    private var selectedButtonParams: ConstraintLayout.LayoutParams? = null
    private var unselectedButtonParams: ConstraintLayout.LayoutParams? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val communityShareViewModel: CommunityShareViewModel by lazy {
        viewModelFactory.create(CommunityShareViewModel::class.java)
    }

    constructor(context: Context) : super(context) {
        initialView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialView()
    }

    init {
        ComponentV3Component.getInstance().inject(this)
    }

    fun onSwitchTab(tabSelected: CommunityTabEnum) {
        val paramsForYouButton =
            binding.todayForYouButton.layoutParams as ConstraintLayout.LayoutParams
        val paramsPopularButton =
            binding.todayPopularButton.layoutParams as ConstraintLayout.LayoutParams

        val forYouButton = binding.todayForYouButton
        val popularButton = binding.todayPopularButton

        if (tabSelected === CommunityTabEnum.FOR_YOU) {
            setupForYouButtonTab(
                paramsForYouButton,
                paramsPopularButton,
                forYouButton,
                popularButton
            )
        } else {
            setupPopularButtonTab(
                paramsForYouButton,
                paramsPopularButton,
                forYouButton,
                popularButton
            )
        }
        binding.todayPopularButton.layoutParams = paramsPopularButton
        binding.todayForYouButton.layoutParams = paramsForYouButton

        setupUnSelectedButton()
        setupSelectedButton()
    }

    fun setSelectFeedTabMenuListener(selectFeedTabMenuListener: SelectFeedTabMenuListener) {
        this.selectFeedTabMenuListener = selectFeedTabMenuListener
    }

    fun setCommunityTabTitle(communityTabData: CommunityTabDataModel) {
        binding.todayForYouButton.text = communityTabData.forYouTitle
        binding.todayPopularButton.text = communityTabData.communityTitle
    }

    private fun initialView() {
        setupView()
        initialAmityPostSharing()
    }

    private fun setupView() {
        addView(binding.root)
        binding.todayForYouButton.setOnClickListener {
            selectFeedTabMenuListener?.onTabSelected(
                tabSelected = CommunityTabEnum.FOR_YOU,
                titleTab = binding.todayForYouButton.text.toString()
            )
        }

        binding.todayPopularButton.setOnClickListener {
            selectFeedTabMenuListener?.onTabSelected(
                tabSelected = CommunityTabEnum.POPULAR,
                titleTab = binding.todayPopularButton.text.toString()
            )
        }
    }

    private fun initialAmityPostSharing() {
        val setting = AmityPostSharingSettings()
        setting.myFeedPostSharingTarget = enumValues<AmityPostSharingTarget>().toList()
        AmityUIKitClient.socialUISettings.apply {
            postSharingSettings = setting
            postShareClickListener =
                object : AmityPostShareClickListener {
                    override fun shareToExternal(
                        context: Context,
                        post: AmityPost,
                        community: AmityCommunity?
                    ) {
                        super.shareToExternal(context, post, community)
                        communityShareViewModel.shareDeeplinkPostPage(
                            postId = post.getPostId(),
                            communityId = community?.getCommunityId().toString(),
                            context = context
                        )
                    }
                }
            communityShareClickListener = object : AmityCommunityShareClickListener {
                override fun shareToExternal(context: Context, communityId: String?) {
                    super.shareToExternal(context, communityId)
                    communityId?.let { _communityId ->
                        communityShareViewModel.shareDeeplinkPostPage(
                            postId = "",
                            communityId = _communityId,
                            context = context
                        )
                    }
                }
            }
        }
    }

    private fun setupUnSelectedButton() {
        unselectedButtonParams?.height = dp2px(UNSELECTED_BUTTON_HEIGHT)
        unselectedButtonParams?.topMargin = dp2px(UNSELECTED_BUTTON_TOP_MARGIN)
        unselectedButton?.setPadding(0, 0, 0, 0)
        ContextCompat.getDrawable(
            context,
            R.drawable.bg_round_gray_30dp
        )?.let {
            unselectedButton?.setBackground(
                it
            )
        }
        unselectedButton?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.tv_current_program_bg
            )
        )
    }

    private fun setupSelectedButton() {
        selectedButtonParams?.height = dp2px(SELECTED_BUTTON_HEIGHT)
        selectedButtonParams?.topMargin = dp2px(SELECTED_BUTTON_TOP_MARGIN)
        selectedButton?.setPadding(0, 0, 0, 0)
        ContextCompat.getDrawable(
            context,
            R.drawable.bg_round_black_30dp
        )?.let {
            selectedButton?.setBackground(
                it
            )
        }
        selectedButton?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.white
            )
        )
    }

    private fun setupForYouButtonTab(
        paramsForYouButton: ConstraintLayout.LayoutParams,
        paramsPopularButton: ConstraintLayout.LayoutParams,
        forYouButton: AppCompatButton,
        popularButton: AppCompatButton
    ) {
        binding.todayForYouButton.isClickable = false
        binding.todayPopularButton.isClickable = true
        popularButton.setPadding(
            (Resources.getSystem().displayMetrics.widthPixels / 2) - dp2px(EXTRA_PADDING_TEXT),
            0,
            0,
            0
        )
        selectedButton = forYouButton
        unselectedButton = popularButton
        selectedButtonParams = paramsForYouButton
        unselectedButtonParams = paramsPopularButton
    }

    private fun setupPopularButtonTab(
        paramsForYouButton: ConstraintLayout.LayoutParams,
        paramsPopularButton: ConstraintLayout.LayoutParams,
        forYouButton: AppCompatButton,
        popularButton: AppCompatButton
    ) {
        binding.todayForYouButton.isClickable = true
        binding.todayPopularButton.isClickable = false
        forYouButton.setPadding(
            0,
            0,
            (Resources.getSystem().displayMetrics.widthPixels / 2) - dp2px(EXTRA_PADDING_TEXT),
            0
        )
        selectedButton = popularButton
        unselectedButton = forYouButton
        selectedButtonParams = paramsPopularButton
        unselectedButtonParams = paramsForYouButton
    }

    private fun dp2px(dpValue: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}
