package com.truedigital.core.utils;

import android.util.Log;

import com.truedigital.core.BuildConfig;

public class MILogging {

    private static final int MAX_LOG_TAG_LENGTH = 23;

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH) {
            return str.substring(0, MAX_LOG_TAG_LENGTH - 1);
        }

        return str;
    }

    /**
     * Don't use this when obfuscating class names!
     * Tag may be lost if length > 23 characters ****
     */
    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void LOGD(final String tag, String message) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG) {
            Log.d(makeLogTag(tag), message);
        }

    }

    public static void LOGD(final String tag, String message, Throwable cause) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG) {
            Log.d(makeLogTag(tag), message, cause);
        }
    }

    public static void LOGV(final String tag, String message) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG) {
            Log.v(makeLogTag(tag), message);
        }
    }

    public static void LOGV(final String tag, String message, Throwable cause) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG) {
            Log.v(makeLogTag(tag), message, cause);
        }
    }

    public static void LOGI(final String tag, String message) {
        Log.i(makeLogTag(tag), message);
    }

    public static void LOGI(final String tag, String message, Throwable cause) {
        Log.i(makeLogTag(tag), message, cause);
    }

    public static void LOGW(final String tag, String message) {
        Log.w(makeLogTag(tag), message);
    }

    public static void LOGW(final String tag, String message, Throwable cause) {
        Log.w(makeLogTag(tag), message, cause);
    }

    public static void LOGE(final String tag, String message) {
        Log.e(makeLogTag(tag), message);
    }

    public static void LOGE(final String tag, String message, Throwable cause) {
        Log.e(makeLogTag(tag), message, cause);
    }

    private MILogging() {
    }
}
