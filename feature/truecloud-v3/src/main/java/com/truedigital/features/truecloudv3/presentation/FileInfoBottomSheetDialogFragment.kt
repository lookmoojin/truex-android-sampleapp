package com.truedigital.features.truecloudv3.presentation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.TrueCloudv3FileInfoBottomSheetDialogBinding
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.adapter.DataInfoAdapter
import com.truedigital.features.truecloudv3.presentation.viewmodel.FileInfoBottomSheetDialogViewModel
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class FileInfoBottomSheetDialogFragment : BottomSheetDialogFragment() {
    companion object {
        private const val MAX_HEIGHT = 0.6
    }

    private val binding by viewBinding(TrueCloudv3FileInfoBottomSheetDialogBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: FileInfoBottomSheetDialogViewModel by viewModels { viewModelFactory }

    private var dataInfoAdapter = DataInfoAdapter()
    override fun onAttach(context: Context) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        findNavController().navigateUp()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.true_cloudv3_file_info_bottom_sheet_dialog,
            container,
            false
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val parentLayout = findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
                parentLayout.let { bottomSheet ->
                    bottomSheet.setBackgroundResource(R.color.transparent)
                    val behaviour = BottomSheetBehavior.from(bottomSheet)
                    behaviour.isDraggable = false
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            getParcelable<TrueCloudFilesModel.File>(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_INFO_MODEL)?.let {
                viewModel.onViewCreated(it)
            }
        }
        initViews()
        observeViewModel()
    }

    fun observeViewModel() {
        viewModel.onSetMaxHeightView.observe(viewLifecycleOwner) { _dataInfo ->
            binding.recyclerViewInfo.apply {
                val displayHeight = requireContext().resources.displayMetrics.heightPixels
                val maxShow = (displayHeight * MAX_HEIGHT).toInt()
                layoutParams.height = maxShow
            }
        }
        viewModel.onSetUpData.observe(viewLifecycleOwner) { _dataInfo ->
            dataInfoAdapter.setData(_dataInfo)
        }
    }

    private fun initViews() = with(binding) {
        val layoutManager = LinearLayoutManager(context)
        recyclerViewInfo.layoutManager = layoutManager
        recyclerViewInfo.adapter = dataInfoAdapter

        collapseImageView.onClick {
            dismiss()
        }
    }
}
