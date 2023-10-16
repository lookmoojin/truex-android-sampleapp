package com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ItemBannerMultipleSquareBinding
import com.truedigital.common.share.componentv3.extension.ExclusiveBadgeUtils
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.foundation.extension.RESIZE_LARGE
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.invisible
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.visible

class MultipleSquareViewHolder(itemView: View) : AbstractBannerViewHolder(itemView) {

    val binding: ItemBannerMultipleSquareBinding by lazy {
        ItemBannerMultipleSquareBinding.bind(itemView)
    }

    private var contentTypeMap = listOf("movie", "clip", "livetv", "sportclip")

    companion object {
        private const val TYPE_TVOD = "tvod"
        private const val TYPE_SVOD = "svod"
        private const val TYPE_SUPSCRIPTION_TIERS_TRUEID_PLUS = "trueid_plus"
        private const val TYPE_CTV = "c-tv"
    }

    override fun render(bannerItem: BannerBaseItemModel) {
        binding.apply {
            bannerImageView.transitionName = " - $bindingAdapterPosition}"
            val resource = binding.root.context.resources
            val isTablet = resource.getBoolean(R.bool.is_tablet)
            if (bannerItem.thum.isEmpty() && bannerItem.thumResource != null) {
                bannerItem.thumResource?.let {
                    bannerImageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            it
                        )
                    )
                }
            } else {
                setupImage(bannerItem = bannerItem, isTablet = isTablet)
            }

            bannerItem.movieBannerModel?.let { _movieBannerModel ->
                if (bannerItem.isNewDesign) {
                    loadDetailNewDesign(
                        bannerItem = bannerItem,
                        movieItemModel = _movieBannerModel,
                        isTablet = isTablet
                    )
                } else {
                    loadDetailOldDesign(
                        bannerItem = bannerItem,
                        isTablet = isTablet
                    )
                }
                checkBadge(_movieBannerModel)
                checkPlayButton(_movieBannerModel.contentType)
            } ?: if (bannerItem.isTextVisible) {
                textConstraintGroup.visible()
                titleTextView.text = bannerItem.title
                descriptionTextView.text = bannerItem.detail.capitalize()
            } else {
                textConstraintGroup.invisible()
            }
            gradientView.isVisible = bannerItem.isGradientViewVisible
            secGradientView.isVisible = bannerItem.isSecondGradientViewVisible
        }
    }

    private fun ItemBannerMultipleSquareBinding.setupImage(
        bannerItem: BannerBaseItemModel,
        isTablet: Boolean
    ) {
        if (isTablet) {
            checkLandScapeImage(bannerItem)
        } else {
            checkSquareImage(bannerItem)
        }
    }

    private fun ItemBannerMultipleSquareBinding.checkLandScapeImage(bannerItem: BannerBaseItemModel) {
        if (bannerItem.landscapeImage.isEmpty()) {
            loadImage(bannerItem.thum)
        } else {
            loadImage(bannerItem.landscapeImage)
        }
    }

    private fun ItemBannerMultipleSquareBinding.checkSquareImage(
        bannerItem: BannerBaseItemModel
    ) {
        if (bannerItem.squareImage.isNotEmpty()) {
            loadImage(bannerItem.squareImage)
        } else {
            loadImage(bannerItem.thum)
        }
    }

    private fun ItemBannerMultipleSquareBinding.loadImage(
        imageUrl: String
    ) {
        bannerImageView.load(
            context = itemView.context,
            url = imageUrl,
            placeholder = R.mipmap.placeholder_trueidwhite_horizontal,
            resizeType = RESIZE_LARGE
        )
    }

    private fun ItemBannerMultipleSquareBinding.loadDetailNewDesign(
        bannerItem: BannerBaseItemModel,
        movieItemModel: BannerBaseItemModel.MovieBannerModel,
        isTablet: Boolean
    ) {
        textConstraintGroup.gone()
        if (movieItemModel.caption.isNotEmpty()) {
            titleNewDesignTextView.apply {
                text = movieItemModel.caption
                visible()
            }
        } else {
            titleNewDesignTextView.gone()
        }
        if (movieItemModel.captionDetail.isNotEmpty()) {
            descriptionNewDesignTextView.apply {
                text = movieItemModel.captionDetail
                visible()
            }
        } else {
            descriptionNewDesignTextView.gone()
        }
        if (movieItemModel.titleLogo.isEmpty()) {
            titleLogoImageView.gone()
            movieTitleTextView.apply {
                if (isTablet) {
                    gone()
                } else {
                    visible()
                    movieTitleTextView.text = bannerItem.title
                }
            }
        } else {
            movieTitleTextView.gone()
            titleLogoImageView.apply {
                load(
                    context = itemView.context,
                    url = movieItemModel.titleLogo,
                    resizeType = RESIZE_LARGE
                )
                if (isTablet) {
                    gone()
                } else {
                    visible()
                }
            }
        }
        descriptionTextView.gone()
        titleTextView.text = bannerItem.title
        titleTextView.gone()
    }

    private fun ItemBannerMultipleSquareBinding.loadDetailOldDesign(
        bannerItem: BannerBaseItemModel,
        isTablet: Boolean
    ) {
        if (bannerItem.title.isNotEmpty()) {
            titleTextView.apply {
                if (isTablet) {
                    gone()
                } else {
                    text = bannerItem.title
                    visible()
                }
            }
        } else {
            titleTextView.gone()
        }
        titleNewDesignTextView.gone()
        descriptionTextView.gone()
        descriptionNewDesignTextView.gone()
        titleLogoImageView.gone()
        movieTitleTextView.gone()
    }

    private fun checkBadge(movieBannerModel: BannerBaseItemModel.MovieBannerModel) {

        // badge rent(premium)
        if (movieBannerModel.contentRights == TYPE_TVOD) {
            binding.premiumBadgeImageView.visible()
        } else {
            binding.premiumBadgeImageView.gone()
        }

        // badge trueId plus
        if (movieBannerModel.contentRights.contains(TYPE_SVOD) &&
            movieBannerModel.subscriptionTiers?.contains(TYPE_SUPSCRIPTION_TIERS_TRUEID_PLUS) == true
        ) {
            binding.trueIdPlusImageView.visible()
        } else {
            binding.trueIdPlusImageView.gone()
        }

        if (movieBannerModel.contentRights.contains(TYPE_CTV)) {
            binding.exclusiveBadgeImageView.apply {
                visible()
                setImageResource(R.drawable.badge_true_visions)
            }
        } else {
            // exclusive badge
            binding.exclusiveBadgeImageView.apply {
                isVisible = movieBannerModel.exclusiveBadgeNotExpired
                getExclusiveBadge(movieBannerModel)
            }
        }
    }

    private fun checkPlayButton(contentType: String) {
        if (contentTypeMap.contains(contentType)) {
            binding.playButton.visible()
        } else {
            binding.playButton.gone()
        }
    }

    private fun AppCompatImageView.getExclusiveBadge(movieItemModel: BannerBaseItemModel.MovieBannerModel) {
        when (movieItemModel.badgeType) {
            ExclusiveBadgeUtils.TRUE_ID_ORIGINAL_EXCLUSIVE_BADGE -> {
                setImageResource(R.drawable.ic_badge_true_id_original)
            }
            ExclusiveBadgeUtils.TRUE_ID_ONLY_EXCLUSIVITY_BADGE -> {
                setImageResource(R.drawable.ic_badge_true_id_only)
            }
            ExclusiveBadgeUtils.TRUE_ID_FIRST_WINDOW_BADGE -> {
                setImageResource(R.drawable.ic_badge_true_id_first)
            }
            else -> {
                gone()
            }
        }
    }
}
