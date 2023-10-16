package com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ItemCountdownScheduleCountdownBinding
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.foundation.extension.RESIZE_LARGE
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.visible

class CountdownScheduleCountdownViewHolder(itemView: View) :
    AbstractBannerViewHolder(itemView) {

    companion object {
        private const val SPAN_TEXT_COUNTDOWN_STRING = 1.0f
        private const val SPAN_TEXT_COUNTDOWN_NUMBER = 1.2f
    }

    val binding: ItemCountdownScheduleCountdownBinding by lazy {
        ItemCountdownScheduleCountdownBinding.bind(itemView)
    }

    override fun render(bannerItem: BannerBaseItemModel) {
        val resource = binding.root.context.resources
        val isTablet = resource.getBoolean(R.bool.is_tablet)
        binding.apply {
            if (isTablet) parentView.setPadding(0, 0, 0, 0)
            bannerImageView.transitionName = " - $bindingAdapterPosition}"
            bannerImageView.contentDescription = bannerItem.imageContentDescription

            bannerImageView.load(
                context = itemView.context,
                url = bannerItem.thum,
                placeholder = bannerItem.sportCountdownModel.defaultBackground,
                resizeType = RESIZE_LARGE
            )
            setDateCountdown(bannerItem)
            setMatchDate(bannerItem)
            setVoteBadge(bannerItem)
            playerHomeImageView.apply {
                visible()
                load(
                    context = itemView.context,
                    url = bannerItem.sportCountdownModel.imagePlayerHome,
                    resizeType = RESIZE_LARGE
                )
            }
            playerAwayImageView.apply {
                visible()
                load(
                    context = itemView.context,
                    url = bannerItem.sportCountdownModel.imagePlayerAway,
                    resizeType = RESIZE_LARGE
                )
            }
            homeLogoImageView.apply {
                visible()
                load(
                    context = itemView.context,
                    url = bannerItem.sportCountdownModel.homeLogo,
                    resizeType = RESIZE_LARGE
                )
            }

            awayLogoImageView.apply {
                visible()
                load(
                    context = itemView.context,
                    url = bannerItem.sportCountdownModel.awayLogo,
                    resizeType = RESIZE_LARGE
                )
            }
            homeTeamTextView.apply {
                visible()
                text = bannerItem.sportCountdownModel.homeAka
            }
            awayTeamTextView.apply {
                visible()
                text = bannerItem.sportCountdownModel.awayAka
            }
        }
    }

    private fun ItemCountdownScheduleCountdownBinding.setDateCountdown(
        bannerItem: BannerBaseItemModel
    ) {
        if (bannerItem.sportCountdownModel.dayString.isNotEmpty()) {
            val spannableString = setSpannableString(
                bannerItem.sportCountdownModel.dayString,
                0,
                bannerItem.sportCountdownModel.dayString.length
            )
            DateCountdownTextView.apply {
                visible()
                text = spannableString
            }
        } else if (bannerItem.sportCountdownModel.dayInt > 0) {

            val text = String.format(
                binding.root.context.getString(R.string.text_countdown_shelf_day_to_go),
                bannerItem.sportCountdownModel.dayInt.toString()
            )
            val spanStart = text.indexOf(bannerItem.sportCountdownModel.dayInt.toString())
            val spanEnd = spanStart + bannerItem.sportCountdownModel.dayInt.toString().length

            val spannableString =
                setSpannableString(text, spanStart, spanEnd, SPAN_TEXT_COUNTDOWN_NUMBER)

            DateCountdownTextView.apply {
                visible()
                this.text = spannableString
            }
        }
    }

    private fun ItemCountdownScheduleCountdownBinding.setMatchDate(bannerItem: BannerBaseItemModel) {
        if (bannerItem.sportCountdownModel.matchDate.isNotEmpty()) {
            MatchDateTextView.apply {
                visible()
                text = bannerItem.sportCountdownModel.matchDate
            }
        }
    }

    private fun ItemCountdownScheduleCountdownBinding.setVoteBadge(bannerItem: BannerBaseItemModel) {
        voteBadgeImageView.apply {
            visible()
            load(
                context = itemView.context,
                url = bannerItem.sportCountdownModel.voteImage,
                placeholder = R.mipmap.placeholder_trueidwhite_horizontal,
                resizeType = RESIZE_LARGE
            )
        }
    }

    private fun setSpannableString(
        text: String,
        start: Int,
        end: Int,
        size: Float = SPAN_TEXT_COUNTDOWN_STRING
    ): SpannableString {
        val spannableString = SpannableString(text)
        spannableString.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.yellow_countdown
                )
            ),
            start, end, 0
        )
        spannableString.setSpan(
            RelativeSizeSpan(size),
            start, end, 0
        )
        return spannableString
    }
}
