package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.truedigital.features.truecloudv3.R
import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticModel
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.features.truecloudv3.common.FileMimeTypeManager
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CONTACT_DATA
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic.getLinkDescFromMimeType
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import com.truedigital.features.truecloudv3.domain.model.AlphabetItemModel
import com.truedigital.features.truecloudv3.domain.model.Contact
import com.truedigital.features.truecloudv3.domain.model.ContactPhoneNumberModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import com.truedigital.features.truecloudv3.domain.usecase.CheckContactUpdateUseCase
import com.truedigital.features.truecloudv3.domain.usecase.CompleteUploadContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.DownloadContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.ExportContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetContactDataFromSelectorUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetContactListFromPathUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetGroupAlphabetContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetGroupContactUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetLastUpdateContactPathUseCase
import com.truedigital.features.truecloudv3.domain.usecase.HasContactSyncedUseCase
import com.truedigital.features.truecloudv3.domain.usecase.SetContactSyncedUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UploadContactUseCase
import com.truedigital.features.truecloudv3.navigation.ContactToContactDetailBottomSheet
import com.truedigital.features.truecloudv3.navigation.ContactToOptionContactBottomSheet
import com.truedigital.features.truecloudv3.navigation.ContactToPermission
import com.truedigital.features.truecloudv3.navigation.ContactToSyncContactBottomSheet
import com.truedigital.features.truecloudv3.navigation.router.ContactListRouterUseCase
import com.truedigital.features.truecloudv3.presentation.ContactFragment
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import org.jetbrains.annotations.VisibleForTesting
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class ContactViewModel @Inject constructor(
    private val router: ContactListRouterUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val getContactUseCase: GetContactUseCase,
    private val getGroupContactUseCase: GetGroupContactUseCase,
    private val getGroupAlphabetContactUseCase: GetGroupAlphabetContactUseCase,
    private val checkContactUpdateUseCase: CheckContactUpdateUseCase,
    private val getLastUpdateContactPathUseCase: GetLastUpdateContactPathUseCase,
    private val getContactListFromPathUseCase: GetContactListFromPathUseCase,
    private val uploadContactUseCase: UploadContactUseCase,
    private val completeUploadContactUseCase: CompleteUploadContactUseCase,
    private val getContactDataFromSelectorUseCase: GetContactDataFromSelectorUseCase,
    private val exportContactUseCase: ExportContactUseCase,
    private val downloadContactUseCase: DownloadContactUseCase,
    private val contextDataProviderWrapper: ContextDataProviderWrapper,
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface,
    private val hasContactSyncedUseCase: HasContactSyncedUseCase,
    private val setContactSyncedUseCase: SetContactSyncedUseCase
) : ScopedViewModel() {

    companion object {
        private const val ACTION_GET_CONTACT = "ACTION_GET_CONTACT"
    }

    val updateContactData = MutableLiveData<List<Contact>>()
    val groupAlphabetLiveData = MutableLiveData<List<AlphabetItemModel>>()
    val onBackPressed = MutableLiveData<Boolean>()
    val onContactNotfound = MutableLiveData<Unit>()
    val showEmptyContact = SingleLiveEvent<Unit>()
    val onIntentActionGetContact = SingleLiveEvent<Boolean>()
    val onShowSnackbarExportContactError = SingleLiveEvent<String>()
    val onShowSnackbarSuccess = SingleLiveEvent<String>()
    val onShowSnackbarError = SingleLiveEvent<String>()
    val callToNumber = SingleLiveEvent<String>()
    val onGetContactError = SingleLiveEvent<Pair<String, String>>()
    val onShowDialogDeleteAll = SingleLiveEvent<Pair<String, String>>()
    val onShowDialogSyncAll = SingleLiveEvent<Pair<String, String>>()
    private var contactKey = ""
    private var editContact: ContactTrueCloudModel? = null

    @VisibleForTesting
    val contactList = mutableListOf<ContactTrueCloudModel>()

    @VisibleForTesting
    val transferListener = object : TrueCloudV3TransferObserver.TrueCloudV3TransferListener {
        override fun onStateChanged(id: Int, state: TrueCloudV3TransferState?) {
            if (TrueCloudV3TransferState.COMPLETED == state) {
                completeUpload(contactKey)
            }
        }

        override fun onProgressChanged(
            id: Int,
            bytesCurrent: Long,
            bytesTotal: Long
        ) {
            Timber.i("id: " + id + ", Progress : " + bytesCurrent / bytesTotal)
        }

        override fun onError(id: Int, ex: Exception?) {
            Timber.e("id: " + id + ", Exception : " + ex)
        }
    }

    fun onSelectSyncAllContact() {
        hasContactSyncedUseCase.execute()
            .flowOn(coroutineDispatcher.io())
            .onEach {
                if (it) {
                    onShowDialogSyncAll.value =
                        Pair(
                            contextDataProviderWrapper.get()
                                .getString(R.string.true_cloudv3_dialog_title_confirm_sync_all_contacts),
                            contextDataProviderWrapper.get()
                                .getString(R.string.true_cloudv3_dialog_subtitle_confirm_sync_all_contacts)
                        )
                } else {
                    syncAllContact()
                }
            }
            .launchIn(this)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_SYNC_ALL_CONTACTS
            )
        )
    }

    fun exportToDevice() {
        exportContactUseCase.execute(contactList)
            .flowOn(coroutineDispatcher.io())
            .catch {
                onShowSnackbarExportContactError.value =
                    contextDataProviderWrapper.get().getString(
                        R.string.true_cloudv3_export_failed
                    )
            }
            .onEach {
                onShowSnackbarSuccess.value =
                    contextDataProviderWrapper.get().getString(
                        R.string.true_cloudv3_export_complete
                    )
            }
            .launchIn(this)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_EXPORT_TO_DEVICE
            )
        )
    }

    fun onViewCreated() {
        trackOnScreen()
    }

    fun onClickCall(phone: ContactPhoneNumberModel) {
        callToNumber.value = phone.number
    }

    fun performClickIntroPermission(bundle: Bundle) {
        router.execute(ContactToPermission, bundle)
    }

    fun deleteAllContact() {
        onShowDialogDeleteAll.value = Pair(
            contextDataProviderWrapper.get().getString(
                R.string.true_cloudv3_dialog_title_confirm_delete_all_contacts
            ),
            contextDataProviderWrapper.get().getString(
                R.string.true_cloudv3_dialog_subtitle_confirm_delete_all_contacts
            )
        )
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_DELETE_ALL_CONTRACTS
            )
        )
    }

    fun onClickConfirmDeleteAllDialog() {
        uploadEmptyContact()
    }

    fun onClickConfirmSyncAllDialog() {
        syncAllContact()
    }

    fun selectContactPermissionAlready() {
        onIntentActionGetContact.value = true
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_SELECT_CONTACT_UPLOAD
            )
        )
    }

    fun updateContact(contactTrueCloudModel: ContactTrueCloudModel?) {
        contactTrueCloudModel?.let { contactData ->
            val same = editContact?.areContentsTheSame(contactData) ?: false
            if (!same) {
                contactList.remove(editContact)
                contactList.add(contactData)
                sortContact()
                uploadContact()
            }
        }
    }

    fun deleteContact() {
        contactList.remove(editContact)
        sortContact()
        uploadContactUseCase.execute(
            contactList = contactList,
            contactKey = contactKey,
            folderId = null
        ).flowOn(coroutineDispatcher.io())
            .onEach {
                if (TrueCloudV3TransferState.COMPLETED == it.getState()) {
                    completeDeleteContact()
                } else {
                    it.setTransferListener(
                        object : TrueCloudV3TransferObserver.TrueCloudV3TransferListener {
                            override fun onStateChanged(id: Int, state: TrueCloudV3TransferState?) {
                                if (TrueCloudV3TransferState.COMPLETED == state) {
                                    completeDeleteContact()
                                }
                            }

                            override fun onProgressChanged(
                                id: Int,
                                bytesCurrent: Long,
                                bytesTotal: Long
                            ) {
                                Timber.i("id: " + id + ", Progress : " + bytesCurrent / bytesTotal)
                            }

                            override fun onError(id: Int, ex: Exception?) {
                                Timber.e("id: " + id + ", Exception : " + ex)
                                onShowSnackbarError.value = contextDataProviderWrapper.get()
                                    .getString(R.string.true_cloudv3_can_not_delete_contact)
                            }
                        })
                }
            }
            .catch {
                onShowSnackbarError.value = contextDataProviderWrapper.get()
                    .getString(R.string.true_cloudv3_can_not_delete_contact)
            }
            .launchSafeIn(this)
    }

    private fun completeDeleteContact() {
        completeUpload(contactKey)
        onShowSnackbarSuccess.value = contextDataProviderWrapper.get()
            .getString(R.string.true_cloudv3_delete_contact_successfully)
    }

    fun onActivityResult(result: Intent?) {
        getContactDataFromSelectorUseCase.execute(result)
            .flowOn(coroutineDispatcher.io())
            .onEach { _contactData ->
                addContact(_contactData)
            }
            .launchSafeIn(viewModelScope)
    }

    fun getContactList() {
        checkContactUpdateUseCase.execute().flatMapLatest { _contactData ->
            contactKey = _contactData.id
            if (_contactData.size == null) {
                error(TrueCloudV3ErrorMessage.ERROR_CONTACT_NOT_FOUND)
            } else {
                getLastUpdateContactPathUseCase.execute(_contactData)
            }
        }
            .catch { _error ->
                handleGetContactError(_error.message.orEmpty())
            }
            .onEach {
                if (it.first) {
                    getLastUpdate(it.second)
                } else {
                    downloadLoadContact(it.second)
                }
            }
            .flowOn(coroutineDispatcher.io())
            .launchSafeIn(this)
    }

    private fun addContact(contact: List<ContactTrueCloudModel>) {
        contactList.addAll(contact)
        uploadContactUseCase.execute(
            contactList = contactList,
            contactKey = contactKey,
            folderId = null
        ).flowOn(coroutineDispatcher.io())
            .map {
                if (TrueCloudV3TransferState.COMPLETED == it.getState()) {
                    completeUpload(contactKey)
                } else {
                    it.setTransferListener(transferListener)
                }
                sortContact()
                onShowSnackbarSuccess.value = contextDataProviderWrapper.get().getString(
                    R.string.true_cloudv3_added_contact_successfully
                )
            }
            .catch {
                onShowSnackbarError.value = contextDataProviderWrapper.get().getString(
                    R.string.true_cloudv3_can_not_add_contact_to_true_cloud
                )
            }
            .launchSafeIn(this)
    }

    private fun handleGetContactError(errorMessage: String) {
        when (errorMessage) {
            TrueCloudV3ErrorMessage.ERROR_CONTACT_NOT_FOUND -> {
                onContactNotfound.postValue(Unit)
            }

            else -> {
                onGetContactError.postValue(
                    Pair(errorMessage, ACTION_GET_CONTACT)
                )
            }
        }
    }

    private fun getLastUpdate(path: String) {
        getContactListFromPathUseCase.execute(path)
            .flatMapLatest { _list ->
                contactList.clear()
                contactList.addAll(_list)
                sortContact()
                getGroupContactUseCase.execute(contactList)
            }
            .flowOn(coroutineDispatcher.io())
            .catch {
                onContactNotfound.value = Unit
            }
            .onEach { _result ->
                groupContactByAlphabet(_result)
                if (_result.isNotEmpty()) {
                    updateContactData.value = _result
                } else {
                    showEmptyContact.value = Unit
                }
            }.launchSafeIn(this)
    }

    private fun uploadEmptyContact() {
        uploadContactUseCase.execute(
            contactList = listOf(),
            contactKey = contactKey,
            folderId = null
        ).flowOn(coroutineDispatcher.io())
            .map {
                if (TrueCloudV3TransferState.COMPLETED == it.getState()) {
                    completeUpload(contactKey)
                } else {
                    it.setTransferListener(transferListener)
                }
            }
            .onEach {
                onShowSnackbarSuccess.value = contextDataProviderWrapper.get()
                    .getString(R.string.true_cloudv3_delete_contact_successfully)
            }
            .catch { ex ->
                onShowSnackbarError.value = contextDataProviderWrapper.get()
                    .getString(R.string.true_cloudv3_can_not_delete_contact)
            }
            .onCompletion {
                trackEventDelete()
            }
            .launchSafeIn(this)
    }

    fun uploadContact() {
        uploadContactUseCase.execute(
            contactList = contactList,
            contactKey = contactKey,
            folderId = null
        ).flowOn(coroutineDispatcher.io())
            .map {
                if (TrueCloudV3TransferState.COMPLETED == it.getState()) {
                    completeUpload(contactKey)
                } else {
                    it.setTransferListener(transferListener)
                }
            }.catch { ex ->
                ex.printStackTrace()
            }.launchSafeIn(this)
    }

    fun completeUpload(key: String) {
        completeUploadContactUseCase.execute(key)
            .flowOn(coroutineDispatcher.io())
            .onEach {
                getContactList()
                trackEventUploadSuccess(it.mimeType)
            }
            .launchSafeIn(this)
    }

    fun onClickBack() {
        onBackPressed.value = true
    }

    fun onClickSync() {
        router.execute(ContactToSyncContactBottomSheet)
    }

    fun onClickMoreOption() {
        router.execute(ContactToOptionContactBottomSheet)
    }

    fun onContactClicked(contactTrueCloudModel: ContactTrueCloudModel) {
        editContact = contactTrueCloudModel
        router.execute(
            ContactToContactDetailBottomSheet,
            Bundle().apply {
                putParcelable(
                    KEY_BUNDLE_TRUE_CLOUD_CONTACT_DATA,
                    contactTrueCloudModel
                )
            }
        )
    }

    @VisibleForTesting
    fun groupContactByAlphabet(contacts: List<Contact>) {
        getGroupAlphabetContactUseCase.execute(contacts)
            .flowOn(coroutineDispatcher.default())
            .onEach { alphabets ->
                groupAlphabetLiveData.value = alphabets
            }.launchSafeIn(this)
    }

    private fun downloadLoadContact(path: String) {
        downloadContactUseCase.execute(contactKey, path)
            .flowOn(coroutineDispatcher.io())
            .onEach {
                it.setTransferListener(
                    object : TrueCloudV3TransferObserver.TrueCloudV3TransferListener {
                        override fun onStateChanged(
                            id: Int,
                            state: TrueCloudV3TransferState?
                        ) {
                            if (state == TrueCloudV3TransferState.COMPLETED) {
                                getLastUpdate(path)
                            }
                        }

                        override fun onProgressChanged(
                            id: Int,
                            bytesCurrent: Long,
                            bytesTotal: Long
                        ) {
                            // DO NOTHING
                        }

                        override fun onError(id: Int, ex: Exception?) {
                            // DO NOTHING
                        }
                    }
                )
            }.launchSafeIn(this)
    }

    private fun sortContact() {
        contactList.sortWith(compareBy({ it.firstName }, { it.lastName }, { it.email }))
    }

    fun checkRetryState(action: String) {
        if (action == ACTION_GET_CONTACT) getContactList()
    }

    private fun trackEventUploadSuccess(mimeType: String) {
        val fileMimeType = FileMimeTypeManager.getMimeType(mimeType)
        val linkDesc = getLinkDescFromMimeType(fileMimeType)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_UPLOAD,
                TrueCloudV3TrackAnalytic.PARAMS_FILE_TYPE to linkDesc
            )
        )
    }

    private fun trackEventDelete() {
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_DELETE,
                TrueCloudV3TrackAnalytic.PARAMS_FILE_TYPE to TrueCloudV3TrackAnalytic.LINK_DESC_CONTACTS
            )
        )
    }

    private fun trackOnScreen() {
        analyticManagerInterface.trackScreen(
            PlatformAnalyticModel().apply {
                screenClass = ContactFragment::class.java.canonicalName as String
                screenName = TrueCloudV3TrackAnalytic.SCREEN_CONTACTS
            }
        )
    }

    private fun syncAllContact() {
        getContactUseCase.execute()
            .flowOn(coroutineDispatcher.io())
            .flatMapLatest { contact ->
                contactList.addAll(contact)
                sortContact()
                uploadContactUseCase.execute(
                    contactList = contact,
                    contactKey = contactKey,
                    folderId = null
                )
            }
            .onEach {
                if (TrueCloudV3TransferState.COMPLETED == it.getState()) {
                    completeUpload(contactKey)
                } else {
                    it.setTransferListener(transferListener)
                }
                onShowSnackbarSuccess.value = contextDataProviderWrapper.get().getString(
                    R.string.true_cloudv3_all_contact_were_synsed_to_true_cloud
                )
            }
            .catch {
                onShowSnackbarError.value = contextDataProviderWrapper.get().getString(
                    R.string.true_cloudv3_can_not_sync_all_contacts_to_true_cloud
                )
            }
            .onCompletion {
                setContactSyncedUseCase.execute()
            }
            .launchSafeIn(this)
    }
}
