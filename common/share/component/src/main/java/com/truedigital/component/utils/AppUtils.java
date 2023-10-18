package com.truedigital.component.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.truedigital.common.share.datalegacy.utils.MIStringUtils;
import com.truedigital.component.R;
import com.truedigital.core.extensions.DateStringExtensionKt;

import java.net.ConnectException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

import static com.truedigital.core.constant.DateFormatConstant.HH_mm;

public class AppUtils {

    public static void notifyError(Context context, String message) {
        String localizedMessage = message;
        if (MIStringUtils.isNullOrEmpty(localizedMessage)) {
            localizedMessage = context.getString(R.string.error_activity_error_occurred);
        }
        Toast toast = Toast.makeText(context, localizedMessage, Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) {
            v.setGravity(Gravity.CENTER);
        }
        toast.show();
    }

    public static void notifyError(Context context, int resId) {
        notifyError(context, context.getString(resId));
    }

    public static void notifyError(Context context, Throwable throwable) {
        if (throwable instanceof ConnectException) {
            notifyError(context, R.string.error_internet_connection);
        } else {
            notifyError(context, throwable.getLocalizedMessage());
        }
    }

}