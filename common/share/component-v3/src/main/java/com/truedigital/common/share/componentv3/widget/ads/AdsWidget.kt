package com.truedigital.common.share.componentv3.widget.ads

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.newrelic.agent.android.NewRelic
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ViewComponentAdviewWidgetBinding
import com.truedigital.common.share.componentv3.injections.ComponentV3Component
import com.truedigital.common.share.componentv3.widget.ads.presentation.AdsWidgetViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import timber.log.Timber
import javax.inject.Inject

class AdsWidget : FrameLayout {

    companion object {
        private const val PRASARN_ID = "prasarnid"
        private const val TAG = "ads-result"
        private const val AD_TABLET_SIZE_HEIGHT = 250
        private const val AD_TABLET_SIZE_WIDTH = 640
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val adsWidgetViewModel: AdsWidgetViewModel by lazy {
        viewModelFactory.create(AdsWidgetViewModel::class.java)
    }

    private lateinit var adManagerAdView: AdManagerAdView

    private val binding: ViewComponentAdviewWidgetBinding by lazy {
        ViewComponentAdviewWidgetBinding.inflate(LayoutInflater.from(context))
    }
    private var isActiveTabletAds = true
    var onAdClick: (() -> Unit)? = null

    constructor(context: Context) : super(context) {
        initInflater(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initInflater(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initInflater(context)
    }

    init {
        ComponentV3Component.getInstance().inject(this)
    }

    private fun initInflater(context: Context) {
        initialAd(context)
        addView(binding.root)
    }

    private fun initialAd(context: Context) {
        adManagerAdView = AdManagerAdView(context)
        setView(adManagerAdView)
    }

    @SuppressLint("MissingPermission")
    fun setupAdsManager(adsId: String, adSize: List<AdSize>) {
        binding.componentAdProgress.visible()
        val getCacheAds = adsWidgetViewModel.getCacheAdsView(adId = adsId)
        if (getCacheAds != null) {
            binding.componentAdProgress.gone()
            getCacheAds.parent?.let {
                (getCacheAds.parent as ViewGroup).removeView(getCacheAds)
            }
            binding.containerAdFrameLayout.addView(getCacheAds)
            binding.containerAdFrameLayout.onClick {
                onAdClick?.invoke()
            }
        } else {
            adManagerAdView.createAdManagerAdView(adsId, adSize)
            adManagerAdView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    binding.componentAdProgress.gone()
                    if (adManagerAdView.parent != null) {
                        (adManagerAdView.parent as ViewGroup).removeView(adManagerAdView)
                    }
                    binding.containerAdFrameLayout.removeAllViews()
                    binding.containerAdFrameLayout.addView(adManagerAdView)
                    Timber.d(
                        TAG,
                        "Requesting Ads DONE!! adsID: ${adManagerAdView.adUnitId}"
                    )
                }

                override fun onAdOpened() {
                    onAdClick?.invoke()
                }

                override fun onAdFailedToLoad(err: LoadAdError) {
                    binding.componentAdProgress.gone()
                    binding.containerAdFrameLayout.gone()
                    Timber.d(
                        TAG,
                        "Requesting Ads Error!! code: ${err.code} \n${err.message} \nadsID: ${adManagerAdView.adUnitId}"
                    )
                    val handlingExceptionMap = mapOf(
                        "Key" to "Ad_DFP_Error",
                        "Value" to "Error code: ${err.code} Info => adsID: ${adManagerAdView.adUnitId}"
                    )
                    NewRelic.recordHandledException(
                        Exception("Request Ads DFP error"),
                        handlingExceptionMap
                    )
                    super.onAdFailedToLoad(err)
                }
            }
            val ppId = adsWidgetViewModel.getAdsPPID()
            adManagerAdView.loadAd(
                AdManagerAdRequest.Builder()
                    .setPublisherProvidedId(ppId)
                    .addCustomTargeting(PRASARN_ID, ppId)
                    .build()
            )
        }
    }

    fun setActiveAdsTablet(isActive: Boolean) {
        isActiveTabletAds = isActive
    }

    private fun setView(view: View) {
        binding.containerAdFrameLayout.addView(view)
    }

    private fun AdManagerAdView.createAdManagerAdView(adsId: String, adSizeList: List<AdSize>) {
        this.apply {
            adUnitId = adsId
            val isTablet: Boolean = context.resources.getBoolean(R.bool.is_tablet)
            if (isTablet && isActiveTabletAds) {
                setAdSizes(AdSize(AD_TABLET_SIZE_WIDTH, AD_TABLET_SIZE_HEIGHT))
            } else {
                setAdSizes(*adSizeList.map { it }.toTypedArray())
            }
        }
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
        adManagerAdView.destroy()
    }
}
