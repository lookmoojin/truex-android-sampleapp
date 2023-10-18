package com.truedigital.features.music.widget.ghostloading

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.faltenreich.skeletonlayout.createSkeleton
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ViewGhostLoadingSearchTrendingBinding
import com.truedigital.foundation.extension.gone

class MusicTrendingGhostLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ViewGhostLoadingSearchTrendingBinding =
        ViewGhostLoadingSearchTrendingBinding.inflate(LayoutInflater.from(context))

    init {
        addView(binding.root)
    }

    companion object {
        private const val CIRCLE_RADIUS = 150f
        private const val CORNER_RADIUS = 20f
    }

    fun start() = with(binding) {
        circleHeaderGhostLoadingView.root.createSkeleton(
            cornerRadius = 0f,
            maskColor = ContextCompat.getColor(context, R.color.ghost_loading_dark_grey)
        ).showSkeleton()
        circleItemListGhostLoadingView.start(CIRCLE_RADIUS)
        circleTitleListGhostLoadingView.start()
        cornerHeaderGhostLoadingView.root.createSkeleton(
            cornerRadius = 0f,
            maskColor = ContextCompat.getColor(context, R.color.ghost_loading_dark_grey)
        ).showSkeleton()
        cornerItemListGhostLoadingView.start(CORNER_RADIUS)
        cornerTitleListGhostLoadingView.start()
        cornerSubTitleListGhostLoadingView.start()
        cornerHeaderGhostLoadingView2.root.createSkeleton(
            cornerRadius = 0f,
            maskColor = ContextCompat.getColor(context, R.color.ghost_loading_dark_grey)
        ).showSkeleton()
        cornerItemListGhostLoadingView2.start(CORNER_RADIUS)
        cornerTitleListGhostLoadingView2.start()
        cornerSubTitleListGhostLoadingView2.start()
    }

    fun stop() = with(binding) {
        circleHeaderGhostLoadingView.root.gone()
        circleItemListGhostLoadingView.stop()
        circleTitleListGhostLoadingView.stop()
        cornerHeaderGhostLoadingView.root.gone()
        cornerItemListGhostLoadingView.stop()
        cornerTitleListGhostLoadingView.stop()
        cornerSubTitleListGhostLoadingView.stop()
        cornerHeaderGhostLoadingView2.root.gone()
        cornerItemListGhostLoadingView2.stop()
        cornerTitleListGhostLoadingView2.stop()
        cornerSubTitleListGhostLoadingView2.stop()
    }
}
