package com.truedigital.common.share.analytics.measurement.usecase

import android.annotation.TargetApi
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.FragmentActivity
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key.KEY_SCREEN_RESOLUTION
import javax.inject.Inject

interface TrackDeviceResolutionUseCase {
    fun execute(activity: FragmentActivity)
}

class TrackDeviceResolutionUseCaseImpl @Inject constructor(
    private val analyticManager: AnalyticManager,
) : TrackDeviceResolutionUseCase {

    override fun execute(activity: FragmentActivity) {
        analyticManager.trackUserProperties(
            KEY_SCREEN_RESOLUTION,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getActuallyScreenResolution(activity, WindowInsets.Type.systemBars())
            } else {
                getActuallyScreenResolution(activity)
            }
        )
    }

    @VisibleForTesting
    fun getActuallyScreenResolution(activity: FragmentActivity): String {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val actuallyWidth = displayMetrics.widthPixels
        val actuallyHeight = displayMetrics.heightPixels
        return "$actuallyWidth x $actuallyHeight"
    }

    @VisibleForTesting
    @TargetApi(Build.VERSION_CODES.R)
    fun getActuallyScreenResolution(activity: FragmentActivity, type: Int): String {
        return activity.windowManager.currentWindowMetrics.let { windowMetrics ->
            windowMetrics.windowInsets.getInsetsIgnoringVisibility(type).let { insets ->
                windowMetrics.bounds.width() - insets.left - insets.right
                val actuallyWidth = windowMetrics.bounds.width() - insets.left - insets.right
                val actuallyHeight = windowMetrics.bounds.height()
                "$actuallyWidth x $actuallyHeight"
            }
        }
    }
}
