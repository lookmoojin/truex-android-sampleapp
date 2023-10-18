package com.truedigital.features.truecloudv3.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.TrueCloudv3ViewPlayerSeekBarBinding
import com.truedigital.features.tuned.common.extensions.toDurationString

class TrueCloudV3PlayerSeekBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    interface OnSeekListener {
        fun onSeekTo(progress: Long)
    }

    private val binding = TrueCloudv3ViewPlayerSeekBarBinding.inflate(
        LayoutInflater.from(context),
        this,
        false
    )
    private var blockProgressUpdate = false
    var onSeekListener: OnSeekListener? = null

    init {
        addView(binding.root)
        binding.playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.currentTimeTextView.text = progress.toLong().toDurationString()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                blockProgressUpdate = true
                getContext()?.let { _context ->
                    seekBar?.thumb = ContextCompat.getDrawable(
                        _context,
                        R.drawable.ic_true_cloudv3_seekbar_thumb_pressed
                    )
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                blockProgressUpdate = false
                getContext()?.let { _context ->
                    seekBar?.thumb = ContextCompat.getDrawable(
                        _context,
                        R.drawable.ic_true_cloudv3_seekbar_thumb
                    )
                }
                onSeekListener?.onSeekTo(seekBar?.progress?.toLong() ?: 0L)
            }
        })
    }

    fun setPlaybackTime(position: Long, duration: Long) = with(binding) {
        if (!blockProgressUpdate) {
            playerSeekBar.max = duration.toInt()
            playerSeekBar.progress = position.toInt()
            maxTimeTextView.text = duration.toDurationString()
            currentTimeTextView.text = position.toDurationString()
        }
    }

    fun reset() = with(binding) {
        if (!blockProgressUpdate) {
            playerSeekBar.progress = 0
            currentTimeTextView.text = 0L.toDurationString()
        }
    }
}
