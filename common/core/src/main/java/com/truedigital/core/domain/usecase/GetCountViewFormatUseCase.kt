package com.truedigital.core.domain.usecase

import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

interface GetCountViewFormatUseCase {
    fun execute(countView: Double): String
}

class GetCountViewFormatUseCaseImpl @Inject constructor() : GetCountViewFormatUseCase {
    override fun execute(countView: Double): String {
        val suffix = charArrayOf('K', 'M')
        val decimalFormat = DecimalFormat("#.#").apply {
            this.roundingMode = RoundingMode.DOWN
        }
        val integerFormat = DecimalFormat("#").apply {
            this.roundingMode = RoundingMode.DOWN
        }

        return try {
            when {
                countView < 1000 -> {
                    return integerFormat.format(countView)
                }
                countView < 100000 -> {
                    val afterDivide = countView / 1000
                    return decimalFormat.format(afterDivide) + suffix[0]
                }
                countView < 1000000 -> {
                    val afterDivide = countView / 1000
                    return integerFormat.format(afterDivide) + suffix[0]
                }
                countView <= 10000000 -> {
                    val afterDivide = countView / 1000000
                    return decimalFormat.format(afterDivide) + suffix[1]
                }
                else -> {
                    val afterDivide = countView / 1000000
                    return integerFormat.format(afterDivide) + suffix[1]
                }
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            "0"
        }
    }
}
