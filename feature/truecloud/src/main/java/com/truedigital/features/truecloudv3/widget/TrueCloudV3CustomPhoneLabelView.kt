package com.truedigital.features.truecloudv3.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.truedigital.core.utils.KeyboardUtils
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3CustomPhoneLabelViewBinding
import com.truedigital.features.truecloudv3.domain.model.CustomPhoneLabelModel
import com.truedigital.foundation.extension.onClick

class TrueCloudV3CustomPhoneLabelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val customPhoneLabelModel: CustomPhoneLabelModel,
    private val onFocusOut: (customPhoneLabelModel: CustomPhoneLabelModel) -> Unit
) : FrameLayout(context, attrs, defStyleAttr), TextWatcher {

    private val binding: TrueCloudv3CustomPhoneLabelViewBinding =
        TrueCloudv3CustomPhoneLabelViewBinding.inflate(
            LayoutInflater.from(context),
            this,
            false
        )

    init {
        addView(binding.root)
        tag = customPhoneLabelModel.tagId
        binding.trueCloudLabelTextInputLayout.editText?.setText(customPhoneLabelModel.label)
        binding.trueCloudPhoneNumberTextInputLayout.editText?.setText(customPhoneLabelModel.number)
        binding.trueCloudPhoneNumberTextInputEditText.addTextChangedListener(this)
        setFocus()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // Do nothing
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // Do nothing
    }

    override fun afterTextChanged(number: Editable?) {
        customPhoneLabelModel.number = number.toString()
        onFocusOut.invoke(customPhoneLabelModel)
    }

    fun updateCustomPhoneLabelModel(customPhoneLabelModel: CustomPhoneLabelModel) {
        binding.trueCloudLabelTextInputLayout.editText?.setText(customPhoneLabelModel.label)
    }

    fun setOnClickLabel(onClickLabel: (customPhoneLabelModel: CustomPhoneLabelModel) -> Unit) {
        binding.trueCloudLabeTextInputEditText.onClick {
            onClickLabel.invoke(customPhoneLabelModel)
        }
    }

    fun setOnClickRemove(onClickRemove: (customPhoneLabelModel: CustomPhoneLabelModel) -> Unit) {
        binding.removeImageView.onClick {
            onClickRemove.invoke(customPhoneLabelModel)
        }
    }

    private fun setFocus() {
        binding.trueCloudPhoneNumberTextInputLayout.requestFocus()
        KeyboardUtils.forceKeyboard(binding.trueCloudPhoneNumberTextInputLayout, true)
        binding.trueCloudPhoneNumberTextInputLayout.callOnClick()
    }
}
