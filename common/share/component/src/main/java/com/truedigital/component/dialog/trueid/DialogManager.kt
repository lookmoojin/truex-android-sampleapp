package com.truedigital.component.dialog.trueid

import android.content.Context
import androidx.fragment.app.FragmentManager

object DialogManager {

    fun getPopUpDialog(
        context: Context,
        theme: DialogTheme = DialogTheme.LIGHT,
        icon: DialogIconType,
        title: String,
        subTitle: String,
        init: TrueIdDialog.Builder.() -> TrueIdDialog.Builder
    ): TrueIdDialog {
        val type = DialogType.POPUP
        return getDialogBuilder(context, theme, type, icon, title, subTitle)
            .apply {
                init(this)
            }
            .create()
    }

    fun showPopUpDialog(
        context: Context,
        fragmentManager: FragmentManager,
        theme: DialogTheme = DialogTheme.LIGHT,
        icon: DialogIconType,
        title: String,
        subTitle: String,
        init: TrueIdDialog.Builder.() -> TrueIdDialog.Builder
    ) {
        getPopUpDialog(context, theme, icon, title, subTitle, init)
            .show(fragmentManager)
    }

    fun getBottomSheetDialog(
        context: Context,
        theme: DialogTheme = DialogTheme.LIGHT,
        icon: DialogIconType,
        title: String,
        subTitle: String,
        init: TrueIdDialog.Builder.() -> TrueIdDialog.Builder
    ): TrueIdDialog {
        val type = DialogType.BOTTOM_SHEET
        return getDialogBuilder(context, theme, type, icon, title, subTitle)
            .apply {
                init(this)
            }
            .create()
    }

    fun showBottomSheetDialog(
        context: Context,
        fragmentManager: FragmentManager,
        theme: DialogTheme = DialogTheme.LIGHT,
        icon: DialogIconType,
        title: String,
        subTitle: String,
        init: TrueIdDialog.Builder.() -> TrueIdDialog.Builder
    ) {
        getBottomSheetDialog(context, theme, icon, title, subTitle, init)
            .show(fragmentManager)
    }

    fun getFullScreenDialog(
        context: Context,
        theme: DialogTheme = DialogTheme.LIGHT,
        icon: DialogIconType,
        title: String,
        subTitle: String,
        init: TrueIdDialog.Builder.() -> TrueIdDialog.Builder
    ): TrueIdDialog {
        val type = DialogType.FULL_SCREEN
        return getDialogBuilder(context, theme, type, icon, title, subTitle)
            .apply {
                init(this)
            }
            .create()
    }

    fun showFullScreenDialog(
        context: Context,
        fragmentManager: FragmentManager,
        theme: DialogTheme = DialogTheme.LIGHT,
        icon: DialogIconType,
        title: String,
        subTitle: String,
        init: TrueIdDialog.Builder.() -> TrueIdDialog.Builder
    ) {
        getFullScreenDialog(context, theme, icon, title, subTitle, init)
            .show(fragmentManager)
    }

    private fun getDialogBuilder(
        context: Context,
        theme: DialogTheme = DialogTheme.LIGHT,
        type: DialogType,
        icon: DialogIconType,
        title: String,
        subTitle: String,
    ): TrueIdDialog.Builder {
        return TrueIdDialog.Builder(context, type)
            .setTheme(theme)
            .setIcon(icon)
            .setTitle(title)
            .setSubtitle(subTitle)
    }
}
