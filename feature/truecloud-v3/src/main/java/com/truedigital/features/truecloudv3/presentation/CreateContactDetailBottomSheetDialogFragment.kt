package com.truedigital.features.truecloudv3.presentation

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CONTACT_DATA
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ContactDetailsBottomSheetDialogBinding
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.adapter.ContactPhoneNumberAdapter
import com.truedigital.features.truecloudv3.presentation.viewmodel.CreateContactDetailBottomSheetDialogViewModel
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class CreateContactDetailBottomSheetDialogFragment : BottomSheetDialogFragment() {
    companion object {
        private const val MAX_HEIGHT = 0.7
        private const val MIN_HEIGHT = 0.12
        private const val MAX_SHOW_SIZE = 5
        private const val MIN_SHOW_SIZE = 1
        private const val PHONE_INDEX = 0
        private const val SECONDARY_PHONE_INDEX = 1
        private const val TERTIARY_PHONE_INDEX = 2
        private const val QUALITY_100 = 100
    }

    private val binding by viewBinding(TrueCloudv3ContactDetailsBottomSheetDialogBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: CreateContactDetailBottomSheetDialogViewModel by viewModels { viewModelFactory }
    private var phoneNumberAdapter = ContactPhoneNumberAdapter()

    override fun onAttach(context: Context) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.true_cloudv3_contact_details_bottom_sheet_dialog,
            container,
            false
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val parentLayout = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                parentLayout.let { bottomSheet ->
                    bottomSheet.setBackgroundResource(com.truedigital.component.R.color.transparent)
                    val behaviour = BottomSheetBehavior.from(bottomSheet)
                    behaviour.isDraggable = false
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        observeViewModel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(
                KEY_BUNDLE_TRUE_CLOUD_CONTACT_DATA,
                ContactTrueCloudModel::class.java
            )?.let { model ->
                viewModel.onViewCreated(model)
            }
        } else {
            arguments?.getParcelable<ContactTrueCloudModel>(KEY_BUNDLE_TRUE_CLOUD_CONTACT_DATA)
                ?.let { model ->
                    viewModel.onViewCreated(model)
                }
        }
    }

    private fun base64ToByteArray(base64String: String): ByteArray {
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, QUALITY_100, stream)
        return stream.toByteArray()
    }

    private fun initListener() {
        phoneNumberAdapter.onCallClicked = {
            viewModel.onCallClicked(it)
        }
        phoneNumberAdapter.onCopyClicked = {
            viewModel.onCopyClicked(it)
        }
        binding.collapseImageView.onClick {
            findNavController().navigateUp()
        }
        binding.editImageView.onClick {
            viewModel.onEditClicked()
        }
        binding.downloadImageView.onClick {
            viewModel.onDownloadClicked()
        }
    }

    private fun observeViewModel() {
        viewModel.onSetupView.observe(viewLifecycleOwner) {
            binding.titleNameTextView.text = it.firstName
            phoneNumberAdapter.addPhoneNumber(it.tel)

            binding.trueCloudNumberRecyclerView.apply {
                val displayHeight = context.resources.displayMetrics.heightPixels
                val maxShow = (displayHeight * MAX_HEIGHT).toInt()
                val unit = (displayHeight * MIN_HEIGHT).toInt()

                layoutParams.height =
                    if (it.tel.size >= MAX_SHOW_SIZE) maxShow else it.tel.size * unit
                itemAnimator = null
                isNestedScrollingEnabled = it.tel.size >= MIN_SHOW_SIZE
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = phoneNumberAdapter
            }
        }
        viewModel.onCallContact.observe(viewLifecycleOwner) {
            findNavController().setSavedStateHandle(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CONTACT_CAll, it
            )
            findNavController().navigateUp()
        }
        viewModel.onCopyContact.observe(viewLifecycleOwner) { _number ->
            val clipboardManager =
                context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText(_number, _number))
            binding.root.snackBar(
                getString(R.string.true_cloudv3_copy_success),
                R.color.true_cloudv3_color_toast_success
            )
        }
        viewModel.onSaveContact.observe(viewLifecycleOwner) { _contentData ->
            val intent = Intent(ContactsContract.Intents.Insert.ACTION)
            intent.type = ContactsContract.RawContacts.CONTENT_TYPE
            if (_contentData.picture.isNotEmpty()) {
                val photoData = ContentValues()
                val photo = ArrayList<ContentValues>()
                photoData.put(
                    ContactsContract.Contacts.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                )
                photoData.put(
                    ContactsContract.CommonDataKinds.Photo.PHOTO,
                    base64ToByteArray(_contentData.picture)
                )
                photo.add(photoData)
                intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, photo)
                intent.putExtra(
                    ContactsContract.CommonDataKinds.Photo.PHOTO,
                    base64ToByteArray(_contentData.picture)
                )
            }
            intent.putExtra(ContactsContract.Intents.Insert.NAME, _contentData.firstName)
            _contentData.tel.forEachIndexed { index, contactPhoneNumberModel ->
                val key = when (index) {
                    PHONE_INDEX -> ContactsContract.Intents.Insert.PHONE
                    SECONDARY_PHONE_INDEX -> ContactsContract.Intents.Insert.SECONDARY_PHONE
                    TERTIARY_PHONE_INDEX -> ContactsContract.Intents.Insert.TERTIARY_PHONE
                    else -> ContactsContract.Intents.Insert.DATA
                }
                intent.putExtra(key, contactPhoneNumberModel.number)
            }
            startActivity(intent)
        }
    }
}
