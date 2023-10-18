package com.truedigital.features.tuned.presentation.popups.presenter

import android.os.Bundle
import com.truedigital.features.tuned.domain.facade.lostnetwork.LossOfNetworkFacade
import com.truedigital.features.tuned.presentation.common.Presenter
import javax.inject.Inject

class LossOfNetworkPresenter @Inject constructor(
    private val lossOfNetworkFacade: LossOfNetworkFacade
) : Presenter {

    private lateinit var router: RouterSurface
    private lateinit var view: ViewSurface

    fun onInject(view: ViewSurface, router: RouterSurface) {
        this.view = view
        this.router = router
    }

    override fun onStart(arguments: Bundle?) {
        super.onStart(arguments)
        if (lossOfNetworkFacade.isUserAllowedOffline()) {
            view.showUserOfflineAllowed()
        } else
            view.showNetworkReconnecting()
    }

    fun onGoOffline() {
        if (lossOfNetworkFacade.hasOfflineRight()) {
//            lossOfNetworkFacade.goOffline()
//            router.navigateToMyDownloadsPage()
        } else {
            view.showUpgradeDialog()
        }
    }

    interface ViewSurface {
        fun showUpgradeDialog()
        fun showUserOfflineAllowed()
        fun showNetworkReconnecting()
    }

    interface RouterSurface
}
