package com.truedigital.features.truecloudv3.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.core.extensions.viewBinding
import com.truedigital.core.manager.permission.PermissionManager
import com.truedigital.core.manager.permission.PermissionManagerImpl
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_ORIENTATION
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3FileViewerBinding
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.extension.getNotificationPermission
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.adapter.FileViewerAdapter
import com.truedigital.features.truecloudv3.presentation.viewmodel.FileViewerViewModel
import com.truedigital.foundation.presentations.ViewModelFactory
import timber.log.Timber
import javax.inject.Inject

class FileViewerFragment : BaseFragment(R.layout.fragment_true_cloudv3_file_viewer) {

    private val binding by viewBinding(FragmentTrueCloudv3FileViewerBinding::bind)
    private lateinit var fileViewerAdapter: FileViewerAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: FileViewerViewModel by viewModels { viewModelFactory }

    private val permissionManager: PermissionManager.PermissionAction by lazy {
        PermissionManagerImpl(fragmentActivity = this.requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val page =
            arguments?.getInt(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_PAGE, 0) ?: 0
        val folderId =
            arguments?.getString(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FOLDER_ID).orEmpty()
        val categoryType =
            arguments?.getString(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CATEGORY).orEmpty()
        val sortType =
            arguments?.getParcelable(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_SORT_TYPE)
                ?: SortType.SORT_DATE_DESC
        val selectedFilesModel =
            arguments?.getParcelable<TrueCloudFilesModel.File>(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_VIEW)
        val trueCloudFilesModel =
            arguments?.getParcelableArrayList<TrueCloudFilesModel.File>(
                TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_LIST
            )?.toMutableList() ?: mutableListOf<TrueCloudFilesModel.File>()

        viewModel.onViewCreated(
            folderId = folderId,
            categoryType = categoryType,
            sortType = sortType,
            selectedFilesModel = selectedFilesModel,
            filesListModel = trueCloudFilesModel,
            page = page
        )
        initViews()
        initListener()
        observeViewModel()
    }

    private fun initViews() = with(binding) {
        trueCloudHeaderTitle.apply {
            setOnClickBack {
                viewModel.onClickBack()
            }

            setOnClickMoreOption {
                viewModel.onClickMore()
            }
        }
        fileViewerAdapter = FileViewerAdapter(this@FileViewerFragment)
        fileViewPager.apply {
            adapter = fileViewerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewModel.onPageScrolled(position)
                }
            })
        }
    }

    private fun observeViewModel() {
        viewModel.onAddFileList.observe(viewLifecycleOwner) { _fileList ->
            fileViewerAdapter.addFiles(_fileList)
        }
        viewModel.onSetCurrentPosition.observe(viewLifecycleOwner) { _position ->
            binding.fileViewPager.setCurrentItem(_position, false)
        }
        viewModel.onDeleted.observe(viewLifecycleOwner) { _fileModel ->
            fileViewerAdapter.removeFiles(_fileModel)
        }
        viewModel.onSetTitle.observe(viewLifecycleOwner) { _title ->
            binding.trueCloudHeaderTitle.setTitle(_title)
        }
        viewModel.onBackPressed.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        viewModel.onShowSnackbarComplete.observe(viewLifecycleOwner) {
            binding.root.snackBar(
                it,
                R.color.true_cloudv3_color_toast_success
            )
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun initListener() {
        findNavController()
            .getSavedStateHandle<TrueCloudFilesModel.File>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_RENAME
            )
            ?.observe(
                viewLifecycleOwner
            ) { _trueCloudV3File ->
                viewModel.rename(_trueCloudV3File)
            }
        findNavController()
            .getSavedStateHandle<TrueCloudFilesModel.File>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_DELETE
            )
            ?.observe(
                viewLifecycleOwner
            ) { _trueCloudV3File ->
                viewModel.onDeleteFile(_trueCloudV3File)
            }
        findNavController()
            .getSavedStateHandle<TrueCloudFilesModel.File>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_DOWNLOAD
            )
            ?.observe(
                viewLifecycleOwner
            ) { _trueCloudV3File ->
                permissionManager.requestPermission(
                    getNotificationPermission(),
                    object : PermissionManager.PermissionAskListener {
                        override fun onPermissionDisabled() {
                            Timber.i("onPermissionDisabled")
                        }

                        override fun onPermissionGranted() {
                            viewModel.onDownloadFile(_trueCloudV3File)
                        }

                        override fun onPermissionDenied() {
                            Timber.i("onPermissionDenied")
                        }
                    }
                )
            }

        childFragmentManager.setFragmentResultListener(
            VideoItemViewerFragment.REQUEST_KEY_ENTER_FULL_SCREEN,
            viewLifecycleOwner,
        ) { _, bundle ->
            val orientation = bundle.getInt(KEY_BUNDLE_TRUE_CLOUD_ORIENTATION).takeIf { it > 0 }
            binding.trueCloudHeaderTitle.visibility = View.GONE
            binding.fileViewPager.isUserInputEnabled = false
            (context as? Activity)?.requestedOrientation =
                orientation ?: ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            hideSystemUI()
        }
        childFragmentManager.setFragmentResultListener(
            VideoItemViewerFragment.REQUEST_KEY_EXIT_FULL_SCREEN,
            viewLifecycleOwner,
        ) { _, _ ->
            binding.trueCloudHeaderTitle.visibility = View.VISIBLE
            binding.fileViewPager.isUserInputEnabled = true
            (context as? Activity)?.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            showSystemUI()
        }
    }

    private fun hideSystemUI() {
        val window = (context as? Activity)?.window ?: return

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        val window = (context as? Activity)?.window ?: return
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).show(WindowInsetsCompat.Type.systemBars())
    }
}
