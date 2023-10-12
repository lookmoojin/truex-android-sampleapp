package com.truedigital.features.truecloudv3.util

object CalculateStorage {
    const val TOTAL_PERCENTAGE = 100
    fun getStoragePercentage(used: Long, totalUsed: Long): Int {
        val percentage = ((used.toDouble() / totalUsed.toDouble()) * TOTAL_PERCENTAGE)
        return percentage.toInt()
    }
}
