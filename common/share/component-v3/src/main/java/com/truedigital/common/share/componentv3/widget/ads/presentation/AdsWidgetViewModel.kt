package com.truedigital.common.share.componentv3.widget.ads.presentation

import com.google.android.gms.ads.admanager.AdManagerAdView
import com.truedigital.common.share.data.coredata.domain.usecase.GetPreLoadAdsViewUseCase
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.share.data.prasarn.manager.PrasarnManager
import javax.inject.Inject

class AdsWidgetViewModel @Inject constructor(
    private val prasarnManager: PrasarnManager,
    private val getPreLoadAdsViewUseCase: GetPreLoadAdsViewUseCase
) : ScopedViewModel() {
    fun getAdsPPID(): String {
        return prasarnManager.getPPID()
    }

    fun getCacheAdsView(adId: String): AdManagerAdView? {
        return getPreLoadAdsViewUseCase.execute(adId = adId)
    }
}
