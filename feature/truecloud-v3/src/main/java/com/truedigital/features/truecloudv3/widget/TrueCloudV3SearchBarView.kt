package com.truedigital.features.truecloudv3.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.tdg.truecloud.databinding.TrueCloudv3SearchBarViewBinding
import java.util.Timer
import java.util.TimerTask

class TrueCloudV3SearchBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: TrueCloudv3SearchBarViewBinding = TrueCloudv3SearchBarViewBinding
        .inflate(
            LayoutInflater.from(context),
            this,
            false
        )

    init {
        addView(binding.root)
    }

    private var searchString = ""

    fun setOnInputSearchString(action: (searchString: String) -> Unit) {
        binding.inputEditText.apply {
            addTextChangedListener(
                object : TextWatcher {
                    private var timer: Timer? = null
                    private val delay: Long = 500L
                    override fun afterTextChanged(s: Editable?) {
                        timer = Timer()
                        timer?.schedule(
                            object : TimerTask() {
                                override fun run() {
                                    action.invoke(searchString)
                                    timer?.cancel()
                                }
                            },
                            delay
                        )
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        // Do Nothing
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        searchString = s.toString()
                        timer?.cancel()
                    }
                }
            )
        }
    }

    fun clearSearchInput() {
        binding.inputEditText.apply {
            clearFocus()
            text?.clear()
        }
    }
}
