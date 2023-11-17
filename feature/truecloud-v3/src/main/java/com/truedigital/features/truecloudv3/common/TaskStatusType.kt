package com.truedigital.features.truecloudv3.common

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.truedigital.features.truecloudv3.R

enum class TaskStatusType(
    val keyStatus: String,
    @StringRes val textStatus: Int,
    @ColorRes val layoutColor: Int,
    @ColorRes val actionColor: Int,
    @ColorRes val indicatorColor: Int,
    @ColorRes val textNameColor: Int,
    @ColorRes val textDetailColor: Int,
    @ColorRes val statusColor: Int,
    val showActionPause: Int,
    val showActionRetry: Int
) {
    IN_QUEUE(
        keyStatus = "inqueue",
        textStatus = R.string.true_cloudv3_status_pending,
        layoutColor = R.color.light_background,
        actionColor = R.color.black,
        indicatorColor = R.color.true_cloudv3_color_dark,
        textNameColor = R.color.light_primary,
        textDetailColor = R.color.grey_700,
        statusColor = R.color.light_primary,
        showActionPause = View.VISIBLE,
        showActionRetry = View.GONE
    ),
    WAITING(
        keyStatus = "waiting",
        textStatus = R.string.true_cloudv3_status_pending,
        layoutColor = R.color.light_background,
        actionColor = R.color.black,
        indicatorColor = R.color.true_cloudv3_color_dark,
        textNameColor = R.color.light_primary,
        textDetailColor = R.color.grey_700,
        statusColor = R.color.light_primary,
        showActionPause = View.VISIBLE,
        showActionRetry = View.GONE
    ),
    IN_PROGRESS(
        keyStatus = "inprogress",
        textStatus = R.string.true_cloudv3_upload_status_uploading,
        layoutColor = R.color.light_background,
        actionColor = R.color.black,
        indicatorColor = R.color.true_cloudv3_color_dark,
        textNameColor = R.color.light_primary,
        textDetailColor = R.color.grey_700,
        statusColor = R.color.light_primary,
        showActionPause = View.VISIBLE,
        showActionRetry = View.GONE
    ),
    PAUSE(
        keyStatus = "pause",
        textStatus = R.string.true_cloudv3_status_paused,
        layoutColor = R.color.light_background,
        actionColor = R.color.black,
        indicatorColor = R.color.true_cloudv3_color_dark,
        textNameColor = R.color.light_primary,
        textDetailColor = R.color.grey_700,
        statusColor = R.color.light_primary,
        showActionPause = View.GONE,
        showActionRetry = View.VISIBLE
    ),
    FAILED(
        keyStatus = "failed",
        textStatus = R.string.true_cloudv3_status_failed,
        layoutColor = R.color.light_error,
        actionColor = R.color.white,
        indicatorColor = R.color.light_error,
        textNameColor = R.color.white,
        textDetailColor = R.color.white,
        statusColor = R.color.white,
        showActionPause = View.GONE,
        showActionRetry = View.VISIBLE
    ),
    COMPLETE(
        keyStatus = "complete",
        textStatus = R.string.true_cloudv3_status_complete,
        layoutColor = R.color.light_background,
        actionColor = R.color.black,
        indicatorColor = R.color.true_cloudv3_color_dark,
        textNameColor = R.color.light_primary,
        textDetailColor = R.color.grey_700,
        statusColor = R.color.light_primary,
        showActionPause = View.GONE,
        showActionRetry = View.GONE
    ),
    COMPLETE_API_FAILED(
        keyStatus = "complete_api_failed",
        textStatus = R.string.true_cloudv3_status_failed,
        layoutColor = R.color.light_error,
        actionColor = R.color.white,
        indicatorColor = R.color.light_error,
        textNameColor = R.color.white,
        textDetailColor = R.color.white,
        statusColor = R.color.white,
        showActionPause = View.GONE,
        showActionRetry = View.VISIBLE
    ),
    UNKNOWN(
        keyStatus = "unknown",
        textStatus = R.string.true_cloudv3_status_pending,
        layoutColor = R.color.light_background,
        actionColor = R.color.black,
        indicatorColor = R.color.true_cloudv3_color_dark,
        textNameColor = R.color.light_primary,
        textDetailColor = R.color.grey_700,
        statusColor = R.color.light_primary,
        showActionPause = View.GONE,
        showActionRetry = View.GONE
    );
}
