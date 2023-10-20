package com.truedigital.features.truecloudv3.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3ImageItemViewerBinding
import com.truedigital.component.base.BaseFragment
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_VIEW
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3FileViewerViewModel
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class ImageItemViewerFragment : BaseFragment(R.layout.fragment_true_cloudv3_image_item_viewer) {
    private val binding by viewBinding(FragmentTrueCloudv3ImageItemViewerBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: TrueCloudV3FileViewerViewModel by viewModels { viewModelFactory }

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
        arguments?.apply {
            getParcelable<TrueCloudFilesModel.File>(KEY_BUNDLE_TRUE_CLOUD_FILE_VIEW)?.let {
                viewModel.setObjectFile(it)
            }
        }

        initView()
        observeViewModel()
    }

    private fun initView() {
        binding.trueCloudImageViewProgress.visibility = View.VISIBLE
    }

    private fun observeViewModel() {
        viewModel.onShowPreview.observe(viewLifecycleOwner) {
            binding.trueCloudImageViewProgress.visibility = View.GONE
            binding.trueCloudImageView.load(context, it)
        }
    }
}
