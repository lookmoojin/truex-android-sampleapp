package com.truedigital.features.tuned.presentation.player.view

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.databinding.ActivityPlayerSettingsBinding
import com.truedigital.features.tuned.presentation.components.LifecycleComponentActivity
import com.truedigital.features.tuned.presentation.components.PresenterComponent
import com.truedigital.features.tuned.presentation.player.presenter.PlayerSettingPresenter
import javax.inject.Inject

class PlayerSettingActivity :
    LifecycleComponentActivity(),
    PlayerSettingPresenter.ViewSurface,
    PlayerSettingPresenter.RouterSurface {

    private val binding by viewBinding(ActivityPlayerSettingsBinding::inflate)

    @Inject
    lateinit var presenter: PlayerSettingPresenter

    @Inject
    lateinit var config: Configuration

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        presenter.onInject(this, this)

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarLayout.toolbar.setTitleTextColor(
            ContextCompat.getColor(
                this,
                android.R.color.white
            )
        )
        binding.toolbarLayout.toolbar.navigationIcon?.colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(this, android.R.color.white),
            PorterDuff.Mode.SRC_ATOP
        )

        lifecycleComponents.add(PresenterComponent(presenter))

        binding.llSectionSleepTimer.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            ActivityCompat.finishAfterTransition(this)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun setMobileStreamingAllowed(isAllowed: Boolean) = with(binding) {
        swAllowMobileData.setOnCheckedChangeListener(null)
        swAllowMobileData.isChecked = isAllowed
        swAllowMobileData.setOnCheckedChangeListener { _, isChecked ->
            presenter.onToggleMobileStreaming(
                isChecked
            )
        }
    }

    override fun setHighQualityAudioAllowed(isAllowed: Boolean) = with(binding) {
        swAllowAudioQuality.setOnCheckedChangeListener(null)
        swAllowAudioQuality.isChecked = isAllowed
        swAllowAudioQuality.setOnCheckedChangeListener { _, isChecked ->
            presenter.onToggleHighQualityAudio(
                isChecked
            )
        }
    }
}
