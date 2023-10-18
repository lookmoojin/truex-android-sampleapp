package com.truedigital.features.music.presentation.forceloginbanner

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.login.LoginTunedViewModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.extensions.toast
import com.truedigital.features.tuned.databinding.FragmentForceLoginBannerBinding
import com.truedigital.foundation.extension.RESIZE_NONE
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class ForceLoginBannerFragment : Fragment(R.layout.fragment_force_login_banner) {

    private val binding by viewBinding(FragmentForceLoginBannerBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val loginTunedViewModel by viewModels<LoginTunedViewModel> { viewModelFactory }
    private val forceLoginBannerViewModel: ForceLoginBannerViewModel by viewModels(
        { requireActivity() }) { viewModelFactory }
    private var isAlreadyLoggedIn: Boolean = false

    companion object {
        fun newInstance() = ForceLoginBannerFragment()
        private const val ALREADY_LOGGED_IN = "already_logged_in"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initOnClick()

        forceLoginBannerViewModel.handlerAuthentication(isAlreadyLoggedIn)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(ALREADY_LOGGED_IN, isAlreadyLoggedIn)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        savedInstanceState?.let {
            isAlreadyLoggedIn = it.getBoolean(ALREADY_LOGGED_IN, false)
        }
    }

    private fun initOnClick() {
        binding.musicLoginButton.onClick {
            forceLoginBannerViewModel.navigateToLogin()
        }
    }

    private fun observeViewModel() {
        with(forceLoginBannerViewModel) {
            onShowLoading().observe(viewLifecycleOwner) {
                showLoading()
            }
            onLoginTunedMusic().observe(viewLifecycleOwner) { (userId, accessToken) ->
                loginTunedViewModel.loginTunedMusic(userId, accessToken)
            }
            onShowLoginErrorToast().observe(viewLifecycleOwner) {
                showLoginErrorToast()
            }
            onLoadForceLoginThumb().observe(viewLifecycleOwner) { imageUrl ->
                displayBannerImage(imageUrl)
            }
            onDisplayForceLogin().observe(viewLifecycleOwner) {
                showForceLogin()
            }
            onDisplayGeoBlock().observe(viewLifecycleOwner) {
                showGeoBlock()
            }
        }
        with(loginTunedViewModel) {
            onLoginTunedError().observe(viewLifecycleOwner) {
                showForceLogin()
                showLoginErrorToast()
            }
            onLoginTunedSuccess().observe(viewLifecycleOwner) {
                isAlreadyLoggedIn = true
                forceLoginBannerViewModel.navigateToMusicLanding()
            }
        }
    }

    private fun showLoginErrorToast() {
        requireContext().toast("Error log in. Please try again.")
    }

    private fun displayBannerImage(imageUrl: String) = with(binding) {
        musicBannerImageView.load(
            context = context,
            url = imageUrl,
            placeholder = R.drawable.placeholder_white,
            scaleType = ImageView.ScaleType.CENTER_CROP,
            resizeType = RESIZE_NONE
        )
    }

    private fun showLoading() = with(binding) {
        forceLoginBannerLoadingGroup.visible()
        musicLoginButton.gone()
        geoBlockIncludeView.root.gone()
    }

    private fun showForceLogin() = with(binding) {
        forceLoginBannerLoadingGroup.gone()
        musicLoginButton.visible()
        geoBlockIncludeView.root.gone()
    }

    private fun showGeoBlock() = with(binding) {
        forceLoginBannerLoadingGroup.gone()
        musicLoginButton.gone()
        geoBlockIncludeView.root.visible()
    }
}
