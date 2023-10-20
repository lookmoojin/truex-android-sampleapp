package com.truedigital.features.truecloudv3.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.Player
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3AudioItemViewerBinding
import com.truedigital.component.base.BaseFragment
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3FileViewerViewModel
import com.truedigital.features.truecloudv3.service.TrueCloudV3Player
import com.truedigital.foundation.player.model.MediaAsset
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class AudioItemViewerFragment : BaseFragment(R.layout.fragment_true_cloudv3_audio_item_viewer) {
    private val binding by viewBinding(FragmentTrueCloudv3AudioItemViewerBinding::bind)

    private var player: TrueCloudV3Player? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: TrueCloudV3FileViewerViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onCreate(savedInstanceState)

        player = TrueCloudV3Player(requireContext())
        player?.addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playWhenReady) {
                    binding.trueCloudAudioControllerView.setShowPause()
                } else {
                    binding.trueCloudAudioControllerView.setShowPlay()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        binding.trueCloudAudioControllerView.visibility = View.VISIBLE
                        binding.trueCloudAudioViewProgress.visibility = View.GONE
                    }

                    Player.STATE_ENDED -> {
                        binding.trueCloudAudioControllerView.reset()
                    }
                }
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
                viewModel.setObjectFile(it)
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
        player = null
    }

    private fun initView() = with(binding) {
        trueCloudAudioControllerView.visibility = View.GONE
        trueCloudAudioViewProgress.visibility = View.VISIBLE
        trueCloudAudioControllerView.player = player
    }

    private fun observeViewModel() {
        viewModel.onShowPreview.observe(viewLifecycleOwner) {
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
