package com.truedigital.features.truecloudv3.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.truedigital.common.share.componentv3.extension.safePopBackStack
import com.truedigital.component.base.BaseFragment
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.FragmentIntroTrueCloudBinding
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.IntroTrueCloudViewModel
import com.truedigital.foundation.extension.RESIZE_NONE
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class IntroTrueCloudFragment : BaseFragment(R.layout.fragment_intro_true_cloud) {

    private val binding by viewBinding(FragmentIntroTrueCloudBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: IntroTrueCloudViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observeViewModel()

        val isTablet = context?.resources?.getBoolean(androidx.mediarouter.R.bool.is_tablet) ?: false
        viewModel.checkAuthenticationState(isTablet)
    }

    private fun initViews() = with(binding) {
        closeButton.onClick {
            viewModel.onClickCloseButton()
        }

        loginTextView.onClick {
            viewModel.openAuthenticationPage()
        }
    }

    private fun observeViewModel() = with(binding) {
        viewModel.onDisplayIntroImage.observe(
            viewLifecycleOwner
        ) { introImageUrl ->
            introLoginImageView.visible()
            loginTextView.visible()
            loadingProgressBar.root.gone()
            introLoginImageView.context?.let { _context ->
                introLoginImageView.load(
                    context = _context,
                    url = introImageUrl,
                    placeholder = null,
                    scaleType = ImageView.ScaleType.CENTER_CROP,
                    resizeType = RESIZE_NONE
                )
            }
        }
        viewModel.onClosePage.observe(viewLifecycleOwner) {
            findNavController().safePopBackStack()
        }

        viewModel.onOpenMainTrueCloud.observe(viewLifecycleOwner) {
            viewModel.loginSuccess()
        }

        viewModel.setRouterToNavController(findNavController())
    }
}
