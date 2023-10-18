package com.truedigital.features.music.widget.ads

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ViewMusicAdsBannerBinding
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.extension.visibleOrGone
import com.truedigital.foundation.presentations.ViewModelFactory
import com.truedigital.share.data.prasarn.constant.PrasarnConstant
import com.truedigital.share.data.prasarn.manager.PrasarnManager
import javax.inject.Inject

class MusicAdsBannerWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), LifecycleObserver {

    private val binding: ViewMusicAdsBannerBinding by lazy {
        ViewMusicAdsBannerBinding.inflate(LayoutInflater.from(context))
    }

    @Inject
    lateinit var prasarnManager: PrasarnManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val musicAdsBannerViewModel: MusicAdsBannerViewModel by lazy {
        viewModelFactory.create(MusicAdsBannerViewModel::class.java)
    }

    private val lifecycleOwner = (context as? LifecycleOwner)
    private var adsView: AdManagerAdView? = null
    private var adsId = ""

    init {
        MusicComponent.getInstance().inject(this)

        addView(binding.root)
        lifecycleOwner?.lifecycle?.addObserver(this)
        observeViewModel()
    }

    fun render(adsId: String, mobileSize: String, tabletSize: String) {
        this.adsId = adsId
        val isTablet = context?.resources?.getBoolean(R.bool.is_tablet) ?: false
        musicAdsBannerViewModel.renderAds(isTablet, mobileSize, tabletSize)
    }

    private fun observeViewModel() {
        lifecycleOwner?.let { _lifecycleOwner ->
            musicAdsBannerViewModel.onShowAdsView().observe(_lifecycleOwner) { isShow ->
                binding.musicAdsBannerLayout.visibleOrGone(isShow)
            }
            musicAdsBannerViewModel.onRenderAdsView().observe(_lifecycleOwner) { adsSizeList ->
                renderAdsBanner(adsSizeList)
            }
        }
    }

    private fun renderAdsBanner(adsSizeList: List<AdSize>) {
        if (adsView == null) {
            val ppId = prasarnManager.getPPID()
            val adsRequest = AdManagerAdRequest
                .Builder()
                .setPublisherProvidedId(ppId)
                .addCustomTargeting(PrasarnConstant.PRASARN_ID_KEY, ppId)
                .build()

            adsView = AdManagerAdView(context).apply {
                adUnitId = adsId
                setAdSizes(*adsSizeList.map { it }.toTypedArray())

                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        binding.musicAdsBannerLayout.visible()
                        binding.musicAdsBannerLayout.addView(adsView)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        super.onAdFailedToLoad(error)
                        binding.musicAdsBannerLayout.gone()
                    }
                }
                loadAd(adsRequest)
            }
        }
    }
}
