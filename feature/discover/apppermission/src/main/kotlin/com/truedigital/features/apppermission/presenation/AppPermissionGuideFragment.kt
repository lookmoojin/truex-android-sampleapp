package com.truedigital.features.apppermission.presenation

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.BannerLoopType
import com.truedigital.core.extensions.viewBinding
import com.truedigital.core.manager.permission.PermissionManager
import com.truedigital.core.manager.permission.PermissionManagerImpl
import com.truedigital.core.view.CoreFragment
import com.truedigital.features.apppermission.injections.AppPerMissionComponent
import com.truedigital.features.onboarding.R
import com.truedigital.features.onboarding.databinding.FragmentAppPermissionGuideBinding
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class AppPermissionGuideFragment : CoreFragment(R.layout.fragment_app_permission_guide) {

    companion object {
        private val permissionsList = arrayListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    @Inject
    lateinit var viewModelProvider: ViewModelFactory

    private val appPermissionViewModel: AppPermissionGuideViewModel by viewModels {
        viewModelProvider
    }
    private val binding: FragmentAppPermissionGuideBinding by viewBinding(
        FragmentAppPermissionGuideBinding::bind
    )
    private val permissionManager: PermissionManager.PermissionAction by lazy {
        PermissionManagerImpl(this.requireActivity())
    }
    private var allPermissionList = arrayOf<String>()
    private var onForceLogin: ((Boolean) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppPerMissionComponent.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        setupView()
    }

    private fun afterManagePermission() {
        appPermissionViewModel.checkForceLogin()
        appPermissionViewModel.savePermissionFlag()
        appPermissionViewModel.firebaseTrackAnalytic(
            MeasurementConstant.Discover.LinkDesc
                .LINK_DESC_ALLOW_PERMISSION
        )
    }

    private fun bindViewModel() {
        viewLifecycleOwner.let { lifecycleOwner ->
            with(appPermissionViewModel) {
                onShowAppPermission.observe(
                    lifecycleOwner
                ) { listItem ->
                    binding.permissionDetailViewpager.setupBannerNotScrollBanner(
                        listItem = listItem, width = 3,
                        height = 5,
                        bannerLoopType = BannerLoopType.TYPE_DEFAULT_PERMISSION
                    )
                }
                onShowAgreeButton.observe(
                    lifecycleOwner
                ) { agreeButtonText ->
                    binding.agreePermissionTextView.text = agreeButtonText
                }
                isForceLogin.observe(
                    lifecycleOwner
                ) { isForceLogin ->
                    onForceLogin?.invoke(isForceLogin)
                }
                onAndroidAboveAndroidQ.observe(
                    lifecycleOwner
                ) {
                    // DO NOTHING
                }

                onAndroidBelowAndroidQ.observe(
                    lifecycleOwner
                ) {
                    checkPermissionNotGrantBelowAndroidQ(it)
                }
            }
        }
    }

    private fun checkPermissionNotGrantBelowAndroidQ(permissionNotGrantedList: ArrayList<String>) {
        val arrayPermission = permissionNotGrantedList.toArray(arrayOf<String>())
        val permissionGrantedList =
            permissionManager.checkPermissionConditions(
                arrayPermission
            )
        if (permissionGrantedList.isEmpty()) {
            appPermissionViewModel.checkForceLogin()
        } else {
            appPermissionViewModel.loadPermissionNotGranted(permissionGrantedList)
        }
    }

    private fun requestStorageAndLocationPermission(
        permissionList: Array<String>,
        block: () -> Unit
    ) {
        permissionManager.requestPermission(
            permissionList,
            object : PermissionManager.PermissionAskListener {
                override fun onPermissionDisabled() {
                    block()
                }

                override fun onPermissionGranted() {
                    block()
                }

                override fun onPermissionDenied() {
                    block()
                }
            }
        )
    }

    private fun requestPermission() {
        requestStorageAndLocationPermission(allPermissionList) {
            afterManagePermission()
        }
    }

    private fun setupView() {
        allPermissionList = permissionsList.toTypedArray()
        appPermissionViewModel.onGetAgreeButtonText()
        val isOpenPermissionAlready = appPermissionViewModel.isFirstOpenPermission()

        if (!isOpenPermissionAlready) {
            appPermissionViewModel.loadPermissionNotGranted(
                allPermissionList.toCollection(
                    ArrayList()
                )
            )
        } else {
            appPermissionViewModel.checkCurrentAndroidVersion(
                allPermissionList.toCollection(
                    ArrayList()
                )
            )
        }
        binding.agreePermissionTextView.onClick {
            requestPermission()
        }
    }
}
