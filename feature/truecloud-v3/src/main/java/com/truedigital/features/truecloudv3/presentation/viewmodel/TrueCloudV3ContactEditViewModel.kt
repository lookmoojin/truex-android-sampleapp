package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CONTACT_EDIT_PHONE_LABEL_DATA
import com.truedigital.features.truecloudv3.domain.model.ContactPhoneNumberModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudDisplayModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.domain.model.CustomPhoneLabelModel
import com.truedigital.features.truecloudv3.navigation.EditContactToContactSelectLabelBottomSheet
import com.truedigital.features.truecloudv3.navigation.router.ContactEditRouterUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.VisibleForTesting
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.random.Random

class TrueCloudV3ContactEditViewModel @Inject constructor(
    private val router: ContactEditRouterUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider
) : ScopedViewModel() {
    companion object {
        private const val FIRST_INDEX = 0
        private const val DEFAULT_ZERO = 0
        private const val QUALITY_100 = 100
    }

    val onBackPressed = MutableLiveData<Boolean>()
    val onSetContactData = MutableLiveData<ContactTrueCloudDisplayModel>()
    val onShowSaved = MutableLiveData<ContactTrueCloudModel>()
    val onDeleteContact = MutableLiveData<Unit>()
    val showConfirmDialogDelete = MutableLiveData<Unit>()
    val addAllCustomLabel = MutableLiveData<List<CustomPhoneLabelModel>>()
    val addCustomLabel = MutableLiveData<CustomPhoneLabelModel>()
    val onUpdatePhoneLabelView = MutableLiveData<CustomPhoneLabelModel>()
    val onRemoveCustomLabel = MutableLiveData<Int>()
    val onLaunchPickMedia = SingleLiveEvent<Unit>()

    @VisibleForTesting
    val customPhoneLabelList = mutableListOf<CustomPhoneLabelModel>()

    fun setContactData(contactData: ContactTrueCloudModel) {
        CoroutineScope(coroutineDispatcher.main()).launchSafe {
            val displayContactData = ContactTrueCloudDisplayModel(
                picture = if (contactData.picture.isNotEmpty()) base64ToBitmap(contactData.picture) else null,
                firstName = contactData.firstName,
                lastName = contactData.lastName,
                email = contactData.email,
                tel = contactData.tel
            )
            onSetContactData.value = displayContactData
            contactData.tel.forEach {
                val customlabel = CustomPhoneLabelModel(
                    tagId = Random.nextInt(),
                    label = it.type,
                    number = it.number
                )
                customPhoneLabelList.add(customlabel)
            }
            addAllCustomLabel.value = customPhoneLabelList
        }
    }

    fun onClickBack() {
        onBackPressed.value = true
    }

    fun onClickAddCustomLabel() {
        val customlabel = CustomPhoneLabelModel(
            tagId = System.currentTimeMillis().toInt()
        )
        customPhoneLabelList.add(customlabel)
        addCustomLabel.value = customlabel
    }

    fun onClickDeleteContact() {
        showConfirmDialogDelete.value = Unit
    }

    fun onClickConfirmDeleteContact() {
        onDeleteContact.value = Unit
    }

    fun updateCustomPhoneLabel(customPhoneLabelModel: CustomPhoneLabelModel) {
        val orgCustomPhoneLabel =
            customPhoneLabelList.firstOrNull { it.tagId == customPhoneLabelModel.tagId }
        if (orgCustomPhoneLabel != null &&
            (
                orgCustomPhoneLabel.label != customPhoneLabelModel.label ||
                    orgCustomPhoneLabel.number != customPhoneLabelModel.number
                )
        ) {
            orgCustomPhoneLabel.label = customPhoneLabelModel.label
            orgCustomPhoneLabel.number = customPhoneLabelModel.number
        }
        onUpdatePhoneLabelView.value = orgCustomPhoneLabel ?: customPhoneLabelModel
    }

    fun onEditTextNumberFocusOut(customPhoneLabelModel: CustomPhoneLabelModel) {
        val index = customPhoneLabelList.indexOfFirst { it.tagId == customPhoneLabelModel.tagId }
        if (index >= FIRST_INDEX) {
            customPhoneLabelList[index] = customPhoneLabelModel
        }
    }

    fun onClickLabel(tagId: Int) {
        router.execute(
            EditContactToContactSelectLabelBottomSheet,
            Bundle().apply {
                putParcelable(
                    KEY_BUNDLE_TRUE_CLOUD_CONTACT_EDIT_PHONE_LABEL_DATA,
                    customPhoneLabelList.firstOrNull { it.tagId == tagId }
                )
            }
        )
    }

    fun onClickSave(
        picture: Bitmap?,
        firstname: String,
        lastname: String,
        email: String
    ) {
        CoroutineScope(coroutineDispatcher.main()).launchSafe {
            val contactTrueCloudModel = ContactTrueCloudModel(
                picture = if (picture != null) bitmapToBase64(picture) else "",
                firstName = firstname,
                lastName = lastname,
                email = email,
                tel = customPhoneLabelList.filter {
                    it.number.isNotEmpty()
                }.map {
                    ContactPhoneNumberModel(
                        type = it.label,
                        number = it.number
                    )
                }
            )
            onShowSaved.value = contactTrueCloudModel
        }
    }

    fun onClickRemove(customPhoneLabelModel: CustomPhoneLabelModel) {
        customPhoneLabelList.remove(customPhoneLabelModel)
        onRemoveCustomLabel.value = customPhoneLabelModel.tagId
    }

    fun onThumbnailImageViewClicked() {
        onLaunchPickMedia.value = Unit
    }

    private suspend fun base64ToBitmap(base64String: String): Bitmap = coroutineScope {
        withContext(coroutineDispatcher.io()) {
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
            return@withContext BitmapFactory.decodeByteArray(
                imageBytes,
                DEFAULT_ZERO,
                imageBytes.size
            )
        }
    }

    private suspend fun bitmapToBase64(bitmap: Bitmap): String = coroutineScope {
        withContext(coroutineDispatcher.io()) {
            val byteArray = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_100, byteArray)
            return@withContext Base64.encodeToString(byteArray.toByteArray(), Base64.DEFAULT)
        }
    }
}
