package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.truedigital.core.base.ScopedViewModel
import com.truedigital.features.truecloudv3.domain.model.CustomPhoneLabelModel
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject

class CreateContactCustomLabelDialogViewModel @Inject constructor() : ScopedViewModel() {

    val onLableCustomFinish = SingleLiveEvent<CustomPhoneLabelModel>()
    var customPhoneLabelModel: CustomPhoneLabelModel? = null
    fun setPhoneLabel(model: CustomPhoneLabelModel) {
        customPhoneLabelModel = model
    }

    fun onClickCreateLabel(label: String) {
        label.isNotEmpty().let {
            customPhoneLabelModel?.label = label
            onLableCustomFinish.value = customPhoneLabelModel
        }
    }
}
