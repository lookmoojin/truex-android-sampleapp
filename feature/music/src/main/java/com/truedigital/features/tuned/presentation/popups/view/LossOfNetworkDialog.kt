package com.truedigital.features.tuned.presentation.popups.view

import android.content.Context
import android.os.Bundle
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.databinding.DialogLossOfNetworkBinding
import com.truedigital.features.tuned.presentation.components.PresenterComponent
import com.truedigital.features.tuned.presentation.popups.presenter.LossOfNetworkPresenter
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import javax.inject.Inject

class LossOfNetworkDialog(dialogContext: Context) :
    InfoDialog(dialogContext),
    LossOfNetworkPresenter.ViewSurface,
    LossOfNetworkPresenter.RouterSurface {

    @Inject
    lateinit var presenter: LossOfNetworkPresenter

    private val binding: DialogLossOfNetworkBinding by lazy {
        DialogLossOfNetworkBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(true)

        presenter.onInject(this, this)

        lifecycleComponents.add(PresenterComponent(presenter))

        binding.buttonGoOffline.onClick { presenter.onGoOffline() }
    }

    override fun showUpgradeDialog() {
        UpgradePremiumDialog(context).show()
    }

    override fun showUserOfflineAllowed() {
        binding.llGoOfflineContainer.visible()
        binding.tvReconnecting.gone()
    }

    override fun showNetworkReconnecting() {
        binding.llGoOfflineContainer.gone()
        binding.tvReconnecting.visible()
    }
}
