package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.os.Bundle
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CONTACT_DATA
import com.truedigital.features.truecloudv3.domain.model.ContactPhoneNumberModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.navigation.ContactDetailToEditContactFragment
import com.truedigital.features.truecloudv3.navigation.router.ContactDetailRouterUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject

class CreateContactDetailBottomSheetDialogViewModel @Inject constructor(
    private val router: ContactDetailRouterUseCase
) : ScopedViewModel() {
    val onSetupView = SingleLiveEvent<ContactTrueCloudModel>()
    val onSaveContact = SingleLiveEvent<ContactTrueCloudModel>()
    val onCallContact = SingleLiveEvent<ContactPhoneNumberModel>()
    val onCopyContact = SingleLiveEvent<String>()
    private var contactInfo = ContactTrueCloudModel()
    fun onViewCreated(contactInfo: ContactTrueCloudModel) {
        this.contactInfo = contactInfo
        onSetupView.value = contactInfo
    }

    fun onEditClicked() {
        router.execute(
            ContactDetailToEditContactFragment,
            Bundle().apply {
                putParcelable(
                    KEY_BUNDLE_TRUE_CLOUD_CONTACT_DATA,
                    contactInfo
                )
            }
        )
    }
    fun onDownloadClicked() {
        onSaveContact.value = contactInfo
    }

    fun onCallClicked(contactPhoneNumberModel: ContactPhoneNumberModel) {
        onCallContact.value = contactPhoneNumberModel
    }
    fun onCopyClicked(contactPhoneNumberModel: ContactPhoneNumberModel) {
        onCopyContact.value = contactPhoneNumberModel.number
    }
}
