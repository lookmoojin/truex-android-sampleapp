package com.truedigital.common.share.data.coredata.domain.usecase

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.newrelic.agent.android.NewRelic
import com.truedigital.common.share.data.R
import com.truedigital.common.share.data.coredata.data.repository.PreLoadAdsCacheRepository
import com.truedigital.core.data.ShelfSkeleton
import com.truedigital.share.data.prasarn.manager.PrasarnManager
import javax.inject.Inject

interface PreLoadAdsViewUseCase {
    fun execute(adItem: List<ShelfSkeleton>)
}

class PreLoadAdsViewUseCaseImpl @Inject constructor(
    private val context: Context,
    private val prasarnManager: PrasarnManager,
    private val preLoadAdsCacheRepository: PreLoadAdsCacheRepository
) : PreLoadAdsViewUseCase {
    companion object {
        private const val PRASARN_ID = "prasarnid"
    }

    override fun execute(adItem: List<ShelfSkeleton>) {
        if (adItem.isNotEmpty()) {
            val ppId = prasarnManager.getPPID()
            val isTablet = context.resources.getBoolean(R.bool.is_tablet)
            adItem.forEach { _adItem ->
                val adsId = _adItem.adsId.orEmpty()
                val adsSize = if (isTablet) {
                    _adItem.adsSizeTablet?.let { customAdsSize(it) }
                } else {
                    _adItem.adsSize?.let { customAdsSize(it) }
                }
                val adManagerAdView = AdManagerAdView(context)
                adsSize?.let {
                    adManagerAdView.creteAdManagerAdView(
                        adsId = adsId,
                        adSizeList = it
                    )
                }
                adManagerAdView.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        preLoadAdsCacheRepository.addAdsCache(
                            adId = adsId,
                            adView = adManagerAdView
                        )
                    }

                    override fun onAdFailedToLoad(err: LoadAdError) {
                        val handlingExceptionMap = mapOf(
                            "Key" to "Ad_DFP_Error",
                            "Value" to "Error code: ${err.code} Info => adsID: ${adManagerAdView.adUnitId}"
                        )
                        NewRelic.recordHandledException(
                            Exception("Request Ads DFP error (PreLoad AdsViewUseCase)"),
                            handlingExceptionMap
                        )
                        super.onAdFailedToLoad(err)
                    }
                }
                adManagerAdView.loadAd(
                    AdManagerAdRequest.Builder()
                        .setPublisherProvidedId(ppId)
                        .addCustomTargeting(PRASARN_ID, ppId)
                        .build()
                )
            }
        }
    }

    private fun customAdsSize(adsSize: String): List<AdSize> {
        val adsSizeList = mutableListOf<AdSize>()
        val customSize = mutableListOf<Int>()
        var width = 0
        var height = 0

        val checkSize = adsSize.replace("[", "").replace("]", "").replace(" ", "")
        checkSize.split(",").forEach { adsSizeStr ->
            when (adsSizeStr) {
                "FLUID" -> adsSizeList.add(AdSize.FLUID)
                "LARGE_BANNER" -> adsSizeList.add(AdSize.LARGE_BANNER)
                "LEADERBOARD" -> adsSizeList.add(AdSize.LEADERBOARD)
                "BANNER" -> adsSizeList.add(AdSize.BANNER)
                else -> {
                    adsSizeStr.split("x").forEach { adsSize ->
                        adsSize.toIntOrNull()?.let {
                            customSize.add(it)
                        }
                    }
                }
            }
        }

        customSize.forEachIndexed { index, _adsSize ->
            if (index % 2 == 0) {
                width = _adsSize
            } else {
                height = _adsSize
                if (width != 0 && height != 0) {
                    adsSizeList.add(AdSize(width, height))
                }
            }
        }
        return adsSizeList
    }

    private fun AdManagerAdView.creteAdManagerAdView(adsId: String, adSizeList: List<AdSize>) {
        this.apply {
            adUnitId = adsId
            setAdSizes(*adSizeList.map { it }.toTypedArray())
        }
    }
}
