package com.truedigital.features.truecloudv3.presentation

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.FragmentShareControlAccessBinding
import com.truedigital.component.base.BaseFragment
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.ShareControlAccessViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class ShareControlAccessFragment : BaseFragment(R.layout.fragment_share_control_access) {

    private val binding by viewBinding(FragmentShareControlAccessBinding::bind)
    private val calendar: Calendar = Calendar.getInstance()

    companion object {
        const val DEFAULT_HOUR_OF_DAY = 16
        const val DEFAULT_MINUTE = 59
        const val DEFAULT_SECOND = 59
        const val DEFAULT_MILLISECOND = 0
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ShareControlAccessViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
        arguments?.apply {
            getParcelable<TrueCloudFilesModel.File>(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_OPTION_FILE_MODEL)?.let {
                viewModel.setFileModel(it)
            }
        }
    }

    private fun initViews() = with(binding) {
        trueCloudBackImageView.onClick {
            activity?.onBackPressed()
        }
        trueCloudShareTextView.onClick {
            viewModel.getShareLink()
        }
        trueCloudUpdateShareTextView.onClick {
            val password = trueCloudSharePasswordTextInputEditText.text.toString()
            viewModel.updateConfig(password = password)
        }
        privateSwitch.onClick {
            viewModel.switchIsPrivate()
        }
        expireDateSwitch.onClick {
            viewModel.switchExpiration()
        }
        passwordSwitch.onClick {
            viewModel.switchSetPassword()
        }
        trueCloudEditPasswordImageView.onClick {
            viewModel.clickEditPassword()
        }
        trueCloudSharePasswordTextInputEditText.doAfterTextChanged { _editable ->
            viewModel.passwordChanged(_editable.toString())
        }
        trueCloudExpireTimeChooseDateTextView.setOnClickListener {
            showDateTimePicker()
        }
        trueCloudCountPasswordTextView.text =
            getString(R.string.true_cloudv3_d24, 0)
    }

    private fun showDateTimePicker() {
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.HOUR_OF_DAY, DEFAULT_HOUR_OF_DAY)
        calendar.set(Calendar.MINUTE, DEFAULT_MINUTE)
        calendar.set(Calendar.SECOND, DEFAULT_SECOND)
        calendar.set(Calendar.MILLISECOND, DEFAULT_MILLISECOND)

        val locale: Locale = resources.configuration.locale
        Locale.setDefault(locale)

        // Create and show the date picker dialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerDialog,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                // Update the selected date
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                viewModel.updateExpireTime(calendar.time)
            },
            currentYear,
            currentMonth,
            currentDay
        )
        datePickerDialog.setOnCancelListener {
            viewModel.onCancelDatePickerDialog()
        }
        // Show the date picker dialog
        datePickerDialog.show()
    }

    private fun observeViewModel() {
        viewModel.onUpdateView.observe(viewLifecycleOwner) { (_isPrivate, _password, _expireAt) ->
            updateView(_isPrivate, _expireAt, _password)
        }
        viewModel.onShowUpdateButton.observe(viewLifecycleOwner) {
            binding.trueCloudShareTextView.gone()
            binding.trueCloudUpdateShareTextView.visible()
        }
        viewModel.onShowShareButton.observe(viewLifecycleOwner) {
            binding.trueCloudShareTextView.visible()
            binding.trueCloudUpdateShareTextView.gone()
        }
        viewModel.openShareIntent.observe(viewLifecycleOwner) { _url ->
            openShareIntent(_url)
        }
        viewModel.onShowPasswordView.observe(viewLifecycleOwner) {
            showPasswordView(it)
        }
        viewModel.onShowSnackbarComplete.observe(viewLifecycleOwner) { _msg ->
            binding.root.snackBar(
                _msg,
                R.color.true_cloudv3_color_toast_success
            )
        }
        viewModel.onShowSnackbarError.observe(viewLifecycleOwner) { _msg ->
            binding.root.snackBar(
                _msg,
                R.color.true_cloudv3_color_toast_error
            )
        }
        viewModel.onUpdateExpireTime.observe(viewLifecycleOwner) { _expireTime ->
            binding.trueCloudExpireTimeChooseDateTextView.text = _expireTime
        }

        viewModel.onUpdateExpirationView.observe(viewLifecycleOwner) { _enable ->
            updateExpirationView(_enable)
        }
        viewModel.onEditPassword.observe(viewLifecycleOwner) {
            editPassword()
        }
        viewModel.onShowDatePickerView.observe(viewLifecycleOwner) {
            showDateTimePicker()
        }
        viewModel.onExpireAtSwitchOffView.observe(viewLifecycleOwner) {
            binding.expireDateSwitch.isChecked = false
        }
        viewModel.onUpdatePassword.observe(viewLifecycleOwner) { _limitedText ->
            binding.trueCloudSharePasswordTextInputEditText.setText(_limitedText)
        }
        viewModel.onUpdatePasswordCount.observe(viewLifecycleOwner) { _passwordCount ->
            binding.trueCloudCountPasswordTextView.text = _passwordCount
        }
    }

    private fun editPassword() {
        binding.trueCloudSharePasswordTextInputEditText.text = null
        binding.trueCloudPasswordTextInputContainer.visible()
        binding.trueCloudUpdateShareTextView.visible()
        binding.trueCloudEditPasswordContainer.gone()
        binding.trueCloudShareTextView.gone()
    }

    private fun updateExpirationView(enable: Boolean) {
        if (enable) {
            binding.trueCloudExpireTimeChooseDateTextView.visible()
            binding.trueCloudExpireTimeTextView.gone()
        } else {
            binding.trueCloudExpireTimeChooseDateTextView.text =
                getString(R.string.true_cloudv3_choose_date)
            binding.trueCloudExpireTimeChooseDateTextView.gone()
            binding.trueCloudExpireTimeTextView.visible()
        }
    }

    private fun showPasswordView(show: Boolean) {
        if (show) {
            binding.trueCloudSharePasswordTextInputEditText.text = null
            binding.trueCloudPasswordTextInputContainer.visible()
        } else {
            binding.trueCloudSharePasswordTextInputEditText.text = null
            binding.trueCloudPasswordTextInputContainer.gone()
        }
        binding.trueCloudEditPasswordContainer.gone()
    }

    private fun openShareIntent(url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, url)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.true_cloudv3_share_via)))
    }

    private fun updateView(isPrivate: Boolean?, expireAt: String?, password: String?) {
        binding.privateSwitch.isChecked = isPrivate ?: false
        binding.expireDateSwitch.isChecked = expireAt != null && expireAt.isNotEmpty()
        if (expireAt != null && expireAt.isNotEmpty()) {
            binding.trueCloudExpireTimeChooseDateTextView.text = expireAt
            binding.trueCloudExpireTimeChooseDateTextView.visible()
            binding.trueCloudExpireTimeTextView.gone()
        } else {
            binding.trueCloudExpireTimeChooseDateTextView.text =
                getString(R.string.true_cloudv3_choose_date)
            binding.trueCloudExpireTimeChooseDateTextView.gone()
            binding.trueCloudExpireTimeTextView.visible()
        }

        binding.passwordSwitch.isChecked = password != null && password.isNotEmpty()
        if (password != null && password.isNotEmpty()) {
            binding.trueCloudEditPasswordContainer.visible()
            binding.trueCloudSharePasswordTextInputEditText.text = null
        } else {
            binding.trueCloudEditPasswordContainer.gone()
        }
        binding.trueCloudPasswordTextInputContainer.gone()
    }
}
