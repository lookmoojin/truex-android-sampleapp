package com.truedigital.component.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.truedigital.component.R
import java.lang.Exception

/**
 * This class will be move to Privilege Module
 */

object CardPrivilegeUtils {

    private const val prefixImageFileName = "ic_card_"
    private const val prefixTextFileName = "txt_card_"
    private const val nameTruePoint = "truepoint"
    private const val defTypeDrawable = "drawable"
    private const val defTypeString = "string"

    fun getCardImage(
        context: Context,
        isCampaignTypePoint: Boolean,
        listCard: List<String>?
    ): Drawable? {
        return getResImageCard(
            context,
            getResName(prefixImageFileName, isCampaignTypePoint, listCard)
        )
    }

    fun getCardName(
        context: Context,
        isCampaignTypePoint: Boolean,
        listCard: List<String>?
    ): String {
        return getResNameCard(
            context,
            getResName(prefixTextFileName, isCampaignTypePoint, listCard)
        )
    }

    private fun getResImageCard(context: Context, drawableName: String): Drawable? {
        return try {
            ContextCompat.getDrawable(
                context,
                context.resources.getIdentifier(drawableName, defTypeDrawable, context.packageName)
            )
        } catch (e: Exception) {
            ContextCompat.getDrawable(context, R.drawable.ic_card_trueyou)
        }
    }

    private fun getResName(
        prefix: String,
        isCampaignTypePoint: Boolean,
        listCard: List<String>?
    ): String {
        return prefix.plus(
            if (isCampaignTypePoint) {
                nameTruePoint
            } else {
                listCard?.sorted()?.joinToString("_")?.toLowerCase() ?: ""
            }
        )
    }

    private fun getResNameCard(context: Context, drawableName: String): String {
        return try {
            context.getString(
                context.resources.getIdentifier(
                    drawableName,
                    defTypeString,
                    context.packageName
                )
            )
        } catch (e: Exception) {
            context.getString(R.string.txt_card_other)
        }
    }
}
