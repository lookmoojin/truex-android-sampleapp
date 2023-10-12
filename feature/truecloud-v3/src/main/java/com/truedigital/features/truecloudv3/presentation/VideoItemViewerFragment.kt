package com.truedigital.features.truecloudv3.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.spherical.SphericalGLSurfaceView
import com.truedigital.component.base.BaseFragment
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_ORIENTATION
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3VideoItemViewerBinding
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3FileViewerViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.VideoItemViewerViewModel
import com.truedigital.features.truecloudv3.service.TrueCloudV3Player
import com.truedigital.features.truecloudv3.widget.TrueCloudV3VideoControllerView
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.player.model.MediaAsset
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class VideoItemViewerFragment : BaseFragment(R.layout.fragment_true_cloudv3_video_item_viewer) {

    companion object {
        const val REQUEST_KEY_ENTER_FULL_SCREEN = "REQUEST_KEY_ENTER_FULL_SCREEN"
        const val REQUEST_KEY_EXIT_FULL_SCREEN = "REQUEST_KEY_EXIT_FULL_SCREEN"
    }

    private val binding by viewBinding(FragmentTrueCloudv3VideoItemViewerBinding::bind)

    private var player: TrueCloudV3Player? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val videoItemViewModel: VideoItemViewerViewModel by viewModels { viewModelFactory }
    private val fileViewerViewModel: TrueCloudV3FileViewerViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onCreate(savedInstanceState)

        player = TrueCloudV3Player(requireContext())
        player?.addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                videoItemViewModel.onPlayWhenReadyChanged(playWhenReady)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                videoItemViewModel.onPlaybackStateChanged(playbackState)
            }
        })
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
            getParcelable<TrueCloudFilesModel.File>(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_VIEW)?.let {
                fileViewerViewModel.setObjectFile(it)
            }
        }

        initView()
        observeViewModel()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() = with(binding) {
        trueCloudVideoControllerView.visibility = View.GONE
        trueCloudVideoControllerView.onVideoControlListener =
            object : TrueCloudV3VideoControllerView.OnVideoControlListener {
                override fun onEnterFullScreenClick() {
                    videoItemViewModel.onEnterFullScreenClick(player?.videoSize)
                }

                override fun onExitFullScreenClick() {
                    setFragmentResult(REQUEST_KEY_EXIT_FULL_SCREEN, Bundle.EMPTY)
                }
            }
        trueCloudVideoViewProgress.visibility = View.VISIBLE
        if (trueCloudVideoView.videoSurfaceView is SphericalGLSurfaceView) {
            trueCloudVideoView.videoSurfaceView?.setOnTouchListener { _, _ ->
                trueCloudVideoControllerView.toggleControl()
                true
            }
        } else {
            trueCloudVideoView.videoSurfaceView?.onClick {
                trueCloudVideoControllerView.toggleControl()
            }
        }
        trueCloudVideoView.player = player
        trueCloudVideoControllerView.player = player
    }

    private fun observeViewModel() {
        videoItemViewModel.onShowPause.observe(viewLifecycleOwner) {
            binding.trueCloudVideoControllerView.setShowPause()
        }
        videoItemViewModel.onShowPlay.observe(viewLifecycleOwner) {
            binding.trueCloudVideoControllerView.setShowPlay()
        }
        videoItemViewModel.onSetOrientation.observe(viewLifecycleOwner) { orientation ->
            setFragmentResult(
                REQUEST_KEY_ENTER_FULL_SCREEN,
                bundleOf(KEY_BUNDLE_TRUE_CLOUD_ORIENTATION to orientation),
            )
        }
        videoItemViewModel.onHideProgress.observe(viewLifecycleOwner) {
            binding.trueCloudVideoControllerView.visibility = View.VISIBLE
            binding.trueCloudVideoViewProgress.visibility = View.GONE
        }
        videoItemViewModel.onResetPlayer.observe(viewLifecycleOwner) {
            binding.trueCloudVideoControllerView.reset()
        }
        fileViewerViewModel.onShowPreview.observe(viewLifecycleOwner) {
            player?.prepare(
                MediaAsset(
                    1,
                    location = it,
                ),
                false,
            )
        }
    }
}
