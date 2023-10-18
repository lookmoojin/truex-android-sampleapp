package com.truedigital.features.music.widget.ads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.AdSize
import javax.inject.Inject

class MusicAdsBannerViewModel @Inject constructor() : ViewModel() {

    companion object {
        const val FLUID = "FLUID"
        const val LARGE_BANNER = "LARGE_BANNER"
        const val LEADERBOARD = "LEADERBOARD"
        const val BANNER = "BANNER"
    }

    private val showAdsView = MutableLiveData<Boolean>()
    private val renderAdsView = MutableLiveData<List<AdSize>>()

    fun onShowAdsView(): LiveData<Boolean> = showAdsView
    fun onRenderAdsView(): LiveData<List<AdSize>> = renderAdsView

    fun renderAds(isTablet: Boolean, mobileSize: String, tabletSize: String) {
        val adsSizeList = mutableListOf<AdSize>()
        val customAdsSizeList = arrayListOf<Int>()
        val deviceSize = if (isTablet) tabletSize else mobileSize

        if (deviceSize.isNotEmpty()) {
            val adsSize = deviceSize.replace("[", "")
                .replace("]", "")
                .replace(" ", "")

            adsSize.split(",").forEach { adsSizeValue ->
                when (adsSizeValue) {
                    FLUID -> adsSizeList.add(AdSize.FLUID)
                    LARGE_BANNER -> adsSizeList.add(AdSize.LARGE_BANNER)
                    LEADERBOARD -> adsSizeList.add(AdSize.LEADERBOARD)
                    BANNER -> adsSizeList.add(AdSize.BANNER)
                    else -> adsSizeValue.toIntOrNull()?.let { customAdsSizeList.add(it) }
                }
            }

            if (customAdsSizeList.isNotEmpty()) {
                adsSizeList.addAll(getCustomAdsSizeList(customAdsSizeList))
            }

            if (adsSizeList.isNotEmpty()) {
                showAdsView.value = true
                renderAdsView.value = adsSizeList
            } else {
                showAdsView.value = false
            }
        } else {
            showAdsView.value = false
        }
    }

    private fun getCustomAdsSizeList(customAdsSizeList: List<Int>): ArrayList<AdSize> {
        val adsSizeList = arrayListOf<AdSize>()
        var width = 0

        customAdsSizeList.forEachIndexed { index, adsSize ->
            if ((index % 2) == 0) {
                width = adsSize
            } else {
                if (width != 0 && adsSize != 0) {
                    adsSizeList.add(AdSize(width, adsSize))
                }
            }
        }

        return adsSizeList
    }
}
