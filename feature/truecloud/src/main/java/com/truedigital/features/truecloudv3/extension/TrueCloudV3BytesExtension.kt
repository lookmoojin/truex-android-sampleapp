package com.truedigital.features.truecloudv3.extension

import android.content.Context
import com.truedigital.features.truecloudv3.R

private const val KILO_BYTE_AS_BYTE = 1024.0
fun Long.formatBinarySize(context: Context): String {
    val megaByteAsByte = KILO_BYTE_AS_BYTE * KILO_BYTE_AS_BYTE
    val gigaByteAsByte = megaByteAsByte * KILO_BYTE_AS_BYTE
    val teraByteAsByte = gigaByteAsByte * KILO_BYTE_AS_BYTE
    val petaByteAsByte = teraByteAsByte * KILO_BYTE_AS_BYTE
    return when {
        this < KILO_BYTE_AS_BYTE -> "${this.toDouble()} B"
        this >= KILO_BYTE_AS_BYTE && this < megaByteAsByte -> {
            "${String.format(
                context.getString(R.string.true_cloudv3_binary_size_format),
                (this / KILO_BYTE_AS_BYTE)
            )} KB"
        }
        this >= megaByteAsByte && this < gigaByteAsByte ->
            "${String.format(
                context.getString(R.string.true_cloudv3_binary_size_format),
                (this / megaByteAsByte)
            )} MB"
        this >= gigaByteAsByte && this < teraByteAsByte ->
            "${String.format(
                context.getString(R.string.true_cloudv3_binary_size_format),
                (this / gigaByteAsByte)
            )} GB"
        this >= teraByteAsByte && this < petaByteAsByte ->
            "${String.format(
                context.getString(R.string.true_cloudv3_binary_size_format),
                (this / teraByteAsByte)
            )} TB"
        else -> "Bigger than 1024 TB"
    }
}
