package com.truedigital.features.music.widget.ghostloading

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.faltenreich.skeletonlayout.createSkeleton
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ViewGhostLoadingTitleListBinding
import com.truedigital.foundation.extension.gone

class MusicTrendingTitleListGhostLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ViewGhostLoadingTitleListBinding =
        ViewGhostLoadingTitleListBinding.inflate(LayoutInflater.from(context))

    init {
        addView(binding.root)
    }

    companion object {
        private const val ALPHA = "alpha"
        private const val ALPHA_DURATION = 300L
        private const val START_DELAY = 500L
    }

    fun start() {
        binding.root.createSkeleton(
            cornerRadius = 0f,
            maskColor = ContextCompat.getColor(context, R.color.ghost_loading_dark_grey)
        ).showSkeleton()
    }

    fun stop() {
        val alpha = ObjectAnimator.ofFloat(binding.root, ALPHA, 0f).apply {
            duration = ALPHA_DURATION
            startDelay = START_DELAY
        }

        AnimatorSet().apply {
            play(alpha)
            start()
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.root.gone()
                }
            })
        }
    }
}
