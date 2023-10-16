package com.truedigital.component.dialog.trueid

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.truedigital.component.dialog.trueid.dialog.TypeADialog
import com.truedigital.component.dialog.trueid.dialog.TypeBDialog
import com.truedigital.component.dialog.trueid.dialog.TypeCDialog
import com.truedigital.component.dialog.trueid.dialog.TypeDDialog

interface TrueIdDialog {
    fun show(manager: FragmentManager)

    class Builder(
        private val context: Context,
        type: DialogType
    ) {

        private val data = DialogData(type)

        private var onPrimaryClickListener: DialogInterface.OnClickListener? = null
        private var onSecondaryClickListener: DialogInterface.OnClickListener? = null
        private var onTertiaryClickListener: DialogInterface.OnClickListener? = null
        private var onBackButtonClickListener: DialogInterface.OnDismissListener? = null
        private var onCloseButtonClickListener: DialogInterface.OnDismissListener? = null
        private var onDialogDismissListener: DialogInterface.OnDismissListener? = null
        private var onCancelListener: DialogInterface.OnCancelListener? = null

        fun setTitle(title: String): Builder {
            data.title = title
            return this
        }

        fun setTitle(@StringRes title: Int): Builder {
            return setTitle(context.getString(title))
        }

        fun setSubtitle(subTitle: String): Builder {
            data.subtitle = subTitle
            return this
        }

        fun setSubtitle(@StringRes subTitle: Int): Builder {
            return setSubtitle(context.getString(subTitle))
        }

        fun setIcon(iconType: DialogIconType): Builder {
            data.iconType = iconType
            return this
        }

        fun setTheme(theme: DialogTheme): Builder {
            data.theme = theme
            return this
        }

        fun setContentAlignment(align: DialogContentAlignment): Builder {
            data.contentAlignment = align
            return this
        }

        fun setPrimaryButton(
            text: String,
            listener: ((DialogFragment) -> Unit)? = null
        ): Builder {
            data.primaryButton = DialogButtonData(text)
            onPrimaryClickListener = initOnClick(listener)
            return this
        }

        fun setPrimaryButton(
            @StringRes text: Int,
            listener: ((DialogFragment) -> Unit)? = null
        ): Builder {
            return setPrimaryButton(context.getString(text), listener)
        }

        fun setSecondaryButton(
            text: String,
            listener: ((DialogFragment) -> Unit)? = null
        ): Builder {
            data.secondaryButton = DialogButtonData(text)
            onSecondaryClickListener = initOnClick(listener)
            return this
        }

        fun setSecondaryButton(
            @StringRes text: Int,
            listener: ((DialogFragment) -> Unit)? = null
        ): Builder {
            return setSecondaryButton(context.getString(text), listener)
        }

        fun setTertiaryButton(
            text: String,
            listener: ((DialogFragment) -> Unit)? = null
        ): Builder {
            data.tertiaryButton = DialogButtonData(text)
            onTertiaryClickListener = initOnClick(listener)
            return this
        }

        fun setBackButtonListener(listener: (DialogFragment) -> Unit?): Builder {
            onBackButtonClickListener = initOnDismiss(listener)
            return this
        }

        fun setCloseButtonListener(listener: (DialogFragment) -> Unit?): Builder {
            onCloseButtonClickListener = initOnDismiss(listener)
            return this
        }

        fun setOnDismissListener(listener: (DialogFragment) -> Unit?): Builder {
            onDialogDismissListener = initOnDismiss(listener)
            return this
        }

        fun setOnCancelListener(listener: (DialogFragment) -> Unit?): Builder {
            onCancelListener = initOnCancel(listener)
            return this
        }

        fun setTertiaryButton(
            @StringRes text: Int,
            listener: ((DialogFragment) -> Unit)? = null
        ): Builder {
            return setTertiaryButton(context.getString(text), listener)
        }

        fun setTopNavigationType(navType: DialogTopNavigationType): Builder {
            data.topNavigation = navType
            return this
        }

        fun create(): TrueIdDialog {
            return when (data.type) {
                DialogType.POPUP -> TypeADialog.newInstance(data)
                DialogType.BOTTOM_SHEET -> {
                    if (data.tertiaryButton.text.isEmpty()) {
                        TypeBDialog.newInstance(data)
                    } else {
                        TypeCDialog.newInstance(data)
                    }
                }
                DialogType.FULL_SCREEN -> TypeDDialog.newInstance(data)
            }.apply {
                setOnClickListener(
                    onPrimaryClickListener,
                    onSecondaryClickListener,
                    onTertiaryClickListener
                )
                setOnClickCloseListener(onCloseButtonClickListener)
                setOnClickBackListener(onBackButtonClickListener)
                setOnDialogDismissListener(onDialogDismissListener)
                setOnCancelListener(onCancelListener)
            }
        }

        private fun initOnClick(listener: ((DialogFragment) -> Unit)?): DialogInterface.OnClickListener {
            return object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogFragment) {
                    listener?.invoke(dialog)
                }
            }
        }

        private fun initOnDismiss(listener: (DialogFragment) -> Unit?): DialogInterface.OnDismissListener {
            return object : DialogInterface.OnDismissListener {
                override fun onDismiss(dialog: DialogFragment) {
                    listener.invoke(dialog)
                }
            }
        }

        private fun initOnCancel(listener: (DialogFragment) -> Unit?): DialogInterface.OnCancelListener {
            return object : DialogInterface.OnCancelListener {
                override fun onCancel(dialog: DialogFragment) {
                    listener.invoke(dialog)
                }
            }
        }
    }
}
