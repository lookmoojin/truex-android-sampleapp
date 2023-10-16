package com.truedigital.component.widget.viewandlike

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.truedigital.component.R
import com.truedigital.component.databinding.LayoutViewAndLikeContentBinding
import com.truedigital.core.extensions.convertToCurrencyUnitFormat
import com.truedigital.core.extensions.fromMinuteToHour
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.invisible
import com.truedigital.foundation.extension.visible

class ViewAndLikeWidget : LinearLayout {

    companion object {
        const val DEFAULT_INCREASE = "1"
        const val DEFAULT_ZERO = "0"
    }

    private var isInvisibleWidget: Boolean = false
    private val binding: LayoutViewAndLikeContentBinding by lazy {
        LayoutViewAndLikeContentBinding.inflate(LayoutInflater.from(context))
    }

    constructor(context: Context) : super(context) {
        addView(binding.root)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        addView(binding.root)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        addView(binding.root)
    }

    fun setLikeIcon(resourceId: Int) = binding.iconLikeImageView.setImageResource(resourceId)

    fun setViewIcon(resourceId: Int) = binding.iconViewImageView.setImageResource(resourceId)

    fun setTextCountViewColor(resourceColor: Int) =
        binding.countViewTextView.setTextColor(ContextCompat.getColor(context, resourceColor))

    fun setTextCountLikeColor(resourceColor: Int) =
        binding.countLikeTextView.setTextColor(ContextCompat.getColor(context, resourceColor))

    fun setTextDateColor(resourceColor: Int) =
        binding.dateTextView.setTextColor(ContextCompat.getColor(context, resourceColor))

    fun setTextDurationColor(resourceColor: Int) =
        binding.countDurationTextView.setTextColor(ContextCompat.getColor(context, resourceColor))

    fun setLineColor(resourceColor: Int) =
        binding.lineLayout.setBackgroundColor(ContextCompat.getColor(context, resourceColor))

    fun setLine2Color(resourceColor: Int) =
        binding.lineLayout2.setBackgroundColor(ContextCompat.getColor(context, resourceColor))

    fun setLineViewColor(resourceColor: Int) =
        binding.lineViewLayout.setBackgroundColor(ContextCompat.getColor(context, resourceColor))

    fun setInvisibleWidget(isInvisible: Boolean) {
        isInvisibleWidget = isInvisible
    }

    fun setupContentViewAndLike(countView: Int, countLike: Int, date: String) = with(binding) {
        if (date.isNotEmpty()) {
            if (countView != 0 && countLike != 0) {
                showViewAndLike(countView, countLike)
                lineViewLayout.visible()
            } else if (countView != 0 || countLike != 0) {
                if (countView != 0) {
                    showOnlyView(countView)
                }
                if (countLike != 0) {
                    showOnlyLike(countLike)
                }
                lineViewLayout.visible()
            } else {
                hideViewAndLike()
                lineViewLayout.gone()
            }
            showDate(date)
        } else {
            if (countView != 0 && countLike != 0) {
                showViewAndLike(countView, countLike)
            } else if (countView != 0) {
                showOnlyView(countView)
            } else {
                if (isInvisibleWidget) {
                    countViewAndLikeLayout.invisible()
                } else {
                    countViewAndLikeLayout.gone()
                }
                hideViewAndLike()
            }
            hideDate()
        }
        lineLayout2.gone()
        countDurationTextView.gone()
    }

    fun setupContentViewAndLike(
        countView: Int,
        countLike: Int,
        date: String,
        duration: String,
        isSeries: Boolean
    ) = with(binding) {
        if (isSeries) {
            if (date.isNotEmpty()) {
                if (countView != 0 && countLike != 0 && !duration.isNullOrEmpty()) {
                    showViewAndLike(countView, countLike)
                    showDate(date)
                    showAllEpisode(duration)
                    lineViewLayout.visible()
                } else if (countView != 0 && countLike != 0 && duration.isNullOrEmpty()) {
                    showViewAndLike(countView, countLike)
                    showDate(date)
                    hideDuration()
                    lineViewLayout.visible()
                } else if (countView != 0 && countLike == 0 && !duration.isNullOrEmpty()) {
                    showOnlyView(countView)
                    showDate(date)
                    hideDuration()
                } else if (countView != 0 && countLike == 0 && duration.isNullOrEmpty()) {
                    showOnlyView(countView)
                    showDate(date)
                    hideDuration()
                } else if (countView != 0 && !duration.isNullOrEmpty()) {
                    showOnlyView(countView)
                    showDate(date)
                    showAllEpisode(duration)
                    lineViewLayout.visible()
                } else if (!duration.isNullOrEmpty()) {
                    showDate(date)
                    hideViewAndLike()
                    showAllEpisode(duration)
                    lineViewLayout.gone()
                } else {
                    showDate(date)
                    hideViewAndLike()
                    hideDuration()
                    lineViewLayout.gone()
                }
            } else {
                if (countView != 0 && countLike != 0 && !duration.isNullOrEmpty()) {
                    showViewAndLike(countView, countLike)
                    hideDate()
                    showAllEpisode(duration)
                } else if (countView != 0 && countLike != 0 && duration.isNullOrEmpty()) {
                    showViewAndLike(countView, countLike)
                    hideDate()
                    hideDuration()
                } else if (countView != 0 && countLike == 0 && !duration.isNullOrEmpty()) {
                    showOnlyView(countView)
                    hideDate()
                    hideDuration()
                } else if (countView != 0 && countLike == 0 && duration.isNullOrEmpty()) {
                    showOnlyView(countView)
                    hideDate()
                    hideDuration()
                } else if (countView != 0 && !duration.isNullOrEmpty()) {
                    showOnlyView(countView)
                    hideDate()
                    showAllEpisode(duration)
                } else if (!duration.isNullOrEmpty()) {
                    hideDate()
                    hideViewAndLike()
                    showAllEpisode(duration)
                    lineViewLayout.gone()
                } else {
                    countViewAndLikeLayout.gone()
                    hideViewAndLike()
                    hideDuration()
                    hideDate()
                }
            }
        } else {
            if (date.isNotEmpty()) {
                if (countView != 0 && countLike != 0 && !duration.isNullOrEmpty()) {
                    showViewAndLike(countView, countLike)
                    showDate(date)
                    showDuration(duration)
                    lineViewLayout.visible()
                } else if (countView != 0 && !duration.isNullOrEmpty()) {
                    showOnlyView(countView)
                    showDate(date)
                    showDuration(duration)
                    lineViewLayout.visible()
                } else if (!duration.isNullOrEmpty()) {
                    showDate(date)
                    hideViewAndLike()
                    showDuration(duration)
                    lineViewLayout.gone()
                } else {
                    showDate(date)
                    hideViewAndLike()
                    hideDuration()
                    lineViewLayout.gone()
                }
            } else {
                if (countView != 0 && countLike != 0 && !duration.isNullOrEmpty()) {
                    showViewAndLike(countView, countLike)
                    hideDate()
                    showDuration(duration)
                } else if (countView != 0 && !duration.isNullOrEmpty()) {
                    showOnlyView(countView)
                    hideDate()
                    showDuration(duration)
                } else if (!duration.isNullOrEmpty()) {
                    hideDate()
                    hideViewAndLike()
                    showDuration(duration)
                    lineViewLayout.gone()
                } else {
                    countViewAndLikeLayout.gone()
                    hideViewAndLike()
                    hideDuration()
                    hideDate()
                }
            }
        }
    }

    fun setContentViewAndLikeFormatTimeAgo(countView: Int, countLike: Int, dateTimeAgo: String) {
        if (countView != 0 && countLike != 0) {
            showViewAndLike(countView, countLike)
        } else if (countView != 0 || countLike != 0) {
            if (countView != 0) {
                showOnlyView(countView)
            }
            if (countLike != 0) {
                showOnlyLike(countLike)
            }
        } else {
            hideViewAndLike()
        }
        showDateTimeAgo(dateTimeAgo, countView != 0 || countLike != 0)
    }

    fun setCountLike(countLikes: String?) = with(binding) {
        if (countLikes.isNullOrEmpty() || countLikes == DEFAULT_ZERO || countLikes == "null") {
            countLikeTextView.text = DEFAULT_ZERO
        } else {
            countLikeTextView.text = countLikes.toDouble().convertToCurrencyUnitFormat(0)
        }
    }

    fun calculateLike(countLikes: String, isLike: Boolean): String? {
        return try {
            if (isLike) {
                "${countLikes.toInt() + 1}"
            } else {
                if (countLikes.toInt() < 0) DEFAULT_ZERO else "${countLikes.toInt()}"
            }
        } catch (e: NumberFormatException) {
            DEFAULT_INCREASE
        }
    }

    fun setupAndShowOnlyLike(countLikes: String?) = with(binding) {
        if (countLikes.isNullOrEmpty() || countLikes == DEFAULT_ZERO || countLikes == "null") {
            countLikeLayout.gone()
        } else {
            countLikeLayout.visible()
            setCountLike(countLikes)
        }
        dateTextView.gone()
        lineViewLayout.gone()
        lineLayout.gone()
        lineLayout2.gone()
        countDurationTextView.gone()
    }

    private fun showViewAndLike(countView: Int, countLike: Int) = with(binding) {
        countViewTextView.text = countView.toDouble().convertToCurrencyUnitFormat(0)
        countLikeTextView.text = countLike.toDouble().convertToCurrencyUnitFormat(0)
        countViewAndLikeLayout.visible()
        countViewLayout.visible()
        countLikeLayout.visible()
        lineLayout.visible()
    }

    private fun showDate(date: String) = with(binding) {
        dateTextView.text = date
        dateTextView.visible()
    }

    private fun showDuration(duration: String) = with(binding) {
        val durationResult = try {
            Integer.parseInt(duration)
        } catch (e: Exception) {
            0
        }

        if (durationResult != 0) {
            countDurationTextView.visible()
            countDurationTextView.text = durationResult.fromMinuteToHour(
                context.getString(R.string.txt_hour),
                context.getString(R.string.txt_minute)
            )
        } else {
            countDurationTextView.gone()
        }
        countDurationTextView.visible()
        lineLayout2.visible()
    }

    private fun showAllEpisode(countEpisode: String) = with(binding) {
        countDurationTextView.text =
            countEpisode + " " + context.getString(R.string.txt_all_episode)
        countDurationTextView.visible()
        lineLayout2.visible()
    }

    private fun showDateTimeAgo(dateTimeAgo: String, isHasLikeOrView: Boolean) = with(binding) {
        if (dateTimeAgo.isNotEmpty()) {
            countDurationTextView.text = dateTimeAgo
            countDurationTextView.visible()
            if (isHasLikeOrView) {
                lineLayout2.visible()
            } else {
                lineLayout2.gone()
            }
        } else {
            countDurationTextView.gone()
            lineLayout2.gone()
        }
    }

    private fun showOnlyView(countView: Int) = with(binding) {
        countViewTextView.text = countView.toDouble().convertToCurrencyUnitFormat(0)
        countViewAndLikeLayout.visible()
        countViewLayout.visible()
        countLikeLayout.gone()
        lineLayout.gone()
    }

    private fun showOnlyLike(countLikes: Int) = with(binding) {
        countLikeLayout.visible()
        countLikeTextView.text = countLikes.toDouble().convertToCurrencyUnitFormat(0)
        dateTextView.gone()
        lineViewLayout.gone()
        lineLayout.gone()
        lineLayout2.gone()
        countDurationTextView.gone()
    }

    private fun hideViewAndLike() = with(binding) {
        if (isInvisibleWidget) {
            countViewLayout.invisible()
            countLikeLayout.invisible()
            lineLayout.invisible()
        } else {
            countViewLayout.gone()
            countLikeLayout.gone()
            lineLayout.gone()
        }
    }

    private fun hideDate() = with(binding) {
        dateTextView.gone()
        lineViewLayout.gone()
    }

    private fun hideDuration() = with(binding) {
        lineLayout2.gone()
        countDurationTextView.gone()
    }
}
