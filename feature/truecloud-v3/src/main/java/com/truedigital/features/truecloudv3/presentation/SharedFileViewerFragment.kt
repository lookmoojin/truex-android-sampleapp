package com.truedigital.features.truecloudv3.presentation

import android.app.DownloadManager
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.core.content.getSystemService
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.FragmentTrueCloudv3SharedFileViewerBinding
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.component.dialog.trueid.DialogManager
import com.truedigital.component.dialog.trueid.DialogTopNavigationType
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_SHARED_FILE_ID
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.domain.model.SharedFileModel
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.FilesTrueCloudViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.SharedFileViewerViewModel
import com.truedigital.features.truecloudv3.receiver.FileDownloaderCompletedReceiver
import com.truedigital.features.truecloudv3.receiver.FilePreDownloaderCompletedReceiver
import com.truedigital.features.truecloudv3.util.SimpleDownloadNotification
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class SharedFileViewerFragment : BaseFragment(R.layout.fragment_true_cloudv3_shared_file_viewer) {

    private val binding by viewBinding(FragmentTrueCloudv3SharedFileViewerBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: SharedFileViewerViewModel by viewModels { viewModelFactory }

    private lateinit var preLoadBroadcastReceiver: FilePreDownloaderCompletedReceiver
    private lateinit var loadFileBroadcastReceiver: FileDownloaderCompletedReceiver
    private lateinit var executors: ExecutorService
    private lateinit var handler: Handler
    private var downloadTaskId: Long = DEFAULT_DOWNLOAD_ID
    private var allowDownloadAction = false

    companion object {
        private const val ZERO_LONG = 0L
        private const val NOTIFICATION_UPDATE_PROGRESS = 1001
        private const val MAX_PROGRESS = 100
        private const val ZERO_INT = 0
        private const val MIN_PROGRESS = 0
        private const val DEFAULT_DOWNLOAD_ID = 0L
        private const val THREAD_POOL = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TrueCloudV3Component.getInstance().inject(this)
        viewModel.setRouterToNavController(findNavController())
        super.onCreate(savedInstanceState)
        arguments?.getString(KEY_BUNDLE_TRUE_CLOUD_SHARED_FILE_ID)?.let { sharedId ->
            viewModel.setSharedObjectId(sharedId)
            viewModel.validateObjectAccess()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListener()
        observeViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver()
    }

    private fun uploadFileOnDownloadCompleted(uri: Uri) {
        viewModel.uploadFile(uri)
    }

    private fun initViews() = with(binding) {
        trueCloudButtonCancel.onClick {
            viewModel.onClickBack()
        }

        trueCloudButtonSubmit.onClick {
            val password = trueCloudEditTextPassword.text.toString()
            viewModel.submitPassword(password)
        }

        trueCloudFileHeaderTitle.apply {
            setOnClickBack {
                viewModel.onClickBack()
            }

            setOnClickMoreOption {
                allowDownloadAction = true
                viewModel.onClickMore()
            }
        }

        trueCloudBackImageView.onClick {
            viewModel.onClickBack()
        }
    }

    private fun observeViewModel() {

        viewModel.sharedObject.observe(viewLifecycleOwner) {
            closeProgressBar()
        }
        viewModel.onRequirePassword.observe(viewLifecycleOwner) {
            showPrivateFileView()
        }

        viewModel.onLinkExpired.observe(viewLifecycleOwner) {
            showErrorLinkExpired()
        }

        viewModel.onSuccess.observe(viewLifecycleOwner) { sharedFile ->
            closeProgressBar()
            setSharedObjectView(sharedFile)
        }
        viewModel.sharedObjectAccessToken.observe(viewLifecycleOwner) { accessToken ->
            accessToken?.let {
                viewModel.loadSharedObjectData(accessToken)
            }
        }

        viewModel.onShowSnackBarError.observe(viewLifecycleOwner) { msg ->
            showSnackBarError(msg)
        }

        viewModel.onLoadDataError.observe(viewLifecycleOwner) { (message: String, action: String) ->
            showErrorStateFullScreenDialog(message = message, action = action)
        }

        viewModel.onBackPressed.observe(viewLifecycleOwner) {
            viewModel.navigateBackToMain()
            FilesTrueCloudViewModel.stackFolderIds.clear()
        }

        viewModel.onUploadError.observe(viewLifecycleOwner) {
            closeProgressBar()
            binding.root.snackBar(
                it,
                R.color.true_cloudv3_color_toast_error
            )
        }

        viewModel.onShowSnackbarComplete.observe(viewLifecycleOwner) {
            closeProgressBar()
            binding.root.snackBar(
                it,
                R.color.true_cloudv3_color_toast_success
            )
        }

        viewModel.onSaveFileToDevice.observe(viewLifecycleOwner) { sharedFile ->
            downloadFileToDevice(sharedFile)
        }

        viewModel.onSaveFileToCloud.observe(viewLifecycleOwner) { sharedFile ->
            preDownloadSaveToCloud(sharedFile)
        }
    }

    private fun showSnackBarError(msg: String) {
        binding.displayPlain.snackBar(
            msg,
            R.color.true_cloudv3_color_toast_error
        )
    }

    private fun closeProgressBar() {
        binding.trueCloudFileHeaderTitle.isClickable = true
        unregisterReceiver()
    }

    private fun unregisterReceiver() {
        try {
            if (this::preLoadBroadcastReceiver.isInitialized) {
                activity?.unregisterReceiver(preLoadBroadcastReceiver)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (this::loadFileBroadcastReceiver.isInitialized) {
                activity?.unregisterReceiver(loadFileBroadcastReceiver)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shutdownDownloadNotification() {
        try {
            if (this::executors.isInitialized) {
                executors.shutdown()
            }
            if (this::handler.isInitialized) {
                handler.removeCallbacksAndMessages(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showErrorLinkExpired() {
        binding.trueCloudProgressBarContainer.gone()
        binding.trueCloudPrivateFileContainer.visible()
        binding.trueCloudTextViewError.visible()
        binding.trueCloudEditTextLayout.gone()
        binding.trueCloudButtonContainer.gone()
        binding.trueCloudFileContainer.gone()
        binding.trueCloudBackImageView
    }

    private fun showPrivateFileView() {
        binding.trueCloudProgressBarContainer.gone()
        binding.trueCloudPrivateFileContainer.visible()
        binding.trueCloudFileContainer.gone()
    }

    private fun setSharedObjectView(sharedObject: SharedFileModel?) {
        sharedObject?.let { sharedFile ->
            binding.trueCloudSharedFileView.setDisplayItem(sharedFile, ::closeLoadingScreen)
            binding.trueCloudFileHeaderTitle.setTitle(sharedFile.name)
            binding.trueCloudPrivateFileContainer.gone()
            binding.trueCloudFileContainer.visible()
        }
    }

    private fun closeLoadingScreen() {
        binding.trueCloudProgressBarContainer.gone()
    }

    private fun showErrorStateFullScreenDialog(message: String, action: String) {
        DialogManager.getFullScreenDialog(
            context = requireContext(),
            icon = DialogIconType.WARNING,
            title = getString(R.string.true_cloudv3_dialog_title_sorry),
            subTitle = getString(R.string.true_cloudv3_dialog_subtitle_something_wrong, message)
        ) {
            setTopNavigationType(DialogTopNavigationType.BACK_BUTTON)
            setBackButtonListener {
                it.dismiss()
            }
            setPrimaryButton(getString(R.string.true_cloudv3_button_retry)) { dialog ->
                dialog.dismiss()
                viewModel.checkRetryState(action)
            }
        }.show(childFragmentManager)
    }

    private fun initListener() {
        findNavController()
            .getSavedStateHandle<Pair<String?, String?>>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_SAVE_TO_DEVICE
            )
            ?.observe(
                viewLifecycleOwner
            ) {
                if (allowDownloadAction) {
                    loadFileBroadcastReceiver =
                        FileDownloaderCompletedReceiver(::closeProgressBar)
                    IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE).also { intentFilter ->
                        activity?.registerReceiver(loadFileBroadcastReceiver, intentFilter)
                    }
                    binding.trueCloudProgressBarContainer.visible()
                    binding.trueCloudFileHeaderTitle.isClickable = false
                    viewModel.saveFileToDevice(it)
                }
                allowDownloadAction = false
            }
        findNavController()
            .getSavedStateHandle<Pair<String?, String?>>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_SAVE_TO_CLOUD
            )
            ?.observe(
                viewLifecycleOwner
            ) {
                if (allowDownloadAction) {
                    preLoadBroadcastReceiver =
                        FilePreDownloaderCompletedReceiver(::uploadFileOnDownloadCompleted)
                    IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE).also { intentFilter ->
                        activity?.registerReceiver(preLoadBroadcastReceiver, intentFilter)
                    }
                    binding.trueCloudProgressBarContainer.visible()
                    binding.trueCloudFileHeaderTitle.isClickable = false
                    viewModel.saveFileToCloud(it)
                }
                allowDownloadAction = false
            }
    }

    private fun downloadFileToDevice(sharedFile: SharedFileModel?) {
        sharedFile?.let {
            val downloadManager = activity?.getSystemService<DownloadManager>()
            val request = DownloadManager.Request(Uri.parse(sharedFile.fileUrl))
                .setMimeType(sharedFile.mimeType)
                .setAllowedOverMetered(true)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    sharedFile.name.trim()
                )
            downloadTaskId = downloadManager?.enqueue(request) ?: ZERO_LONG
            val downloadNotification: SimpleDownloadNotification =
                SimpleDownloadNotification(requireContext())
            downloadNotification.show(
                downloadTaskId.toInt(),
                downloadTaskId.toString(),
                Environment.DIRECTORY_DOWNLOADS
            )
            executors = Executors.newFixedThreadPool(THREAD_POOL)
            handler = Handler(
                Looper.getMainLooper(),
                object : Handler.Callback {
                    override fun handleMessage(msg: Message): Boolean {
                        if (msg.what == NOTIFICATION_UPDATE_PROGRESS) {
                            val downloadProgress = msg.arg1
                            downloadNotification.updateProgress(
                                downloadTaskId.toInt(),
                                downloadProgress
                            )
                            if (downloadProgress == MAX_PROGRESS) {
                                downloadNotification.updateProgress(
                                    downloadTaskId.toInt(),
                                    downloadProgress
                                )
                                downloadNotification.downloadComplete(downloadTaskId.toInt())
                                binding.root.snackBar(
                                    getString(R.string.ture_cloudv3_save_shared_to_my_device_success),
                                    R.color.true_cloudv3_color_toast_success
                                )
                                shutdownDownloadNotification()
                            }
                        }
                        return true
                    }
                }
            )
            executors.execute {
                val id = downloadTaskId
                var progress = MIN_PROGRESS
                var isDownloadFinished = false
                var prevProgress = MIN_PROGRESS
                while (!isDownloadFinished) {
                    val cursor: Cursor = downloadManager!!.query(
                        DownloadManager.Query().setFilterById(id)
                    )
                    if (cursor.moveToFirst()) {
                        val downloadStatus: Int =
                            cursor.getInt(
                                cursor.getColumnIndex(DownloadManager.COLUMN_STATUS) ?: ZERO_INT
                            )
                        when (downloadStatus) {
                            DownloadManager.STATUS_RUNNING -> {
                                val totalBytes: Long =
                                    cursor.getLong(
                                        cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                                            ?: ZERO_INT
                                    )
                                if (totalBytes > MIN_PROGRESS) {
                                    val downloadedBytes: Long = cursor.getLong(
                                        cursor.getColumnIndex(
                                            DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR
                                        ) ?: ZERO_INT
                                    )
                                    progress = (downloadedBytes * MAX_PROGRESS / totalBytes).toInt()
                                }
                            }

                            DownloadManager.STATUS_SUCCESSFUL -> {
                                progress = MAX_PROGRESS
                                isDownloadFinished = true
                            }
                        }
                        val message = Message.obtain()
                        message.what = NOTIFICATION_UPDATE_PROGRESS
                        message.arg1 = progress
                        if (progress > prevProgress) {
                            prevProgress = progress
                            handler.sendMessage(message)
                        }
                    }
                }
            }
        }
    }

    private fun preDownloadSaveToCloud(sharedFile: SharedFileModel?) {
        sharedFile?.let {
            val downloadManager = activity?.getSystemService<DownloadManager>()
            val request = DownloadManager.Request(Uri.parse(sharedFile.fileUrl))
                .setAllowedOverMetered(true)
                .setMimeType(sharedFile.mimeType)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    sharedFile.name.trim()
                )
            downloadManager?.enqueue(request)
        }
    }
}
