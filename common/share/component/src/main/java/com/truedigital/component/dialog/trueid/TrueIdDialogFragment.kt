package com.truedigital.component.dialog.trueid

import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.truedigital.component.R

open class TrueIdDialogFragment : BottomSheetDialogFragment(), TrueIdDialog {

    companion object {
        private const val BUNDLE_KEY_DATA = "data"
    }

    private var onPrimaryClick: DialogInterface.OnClickListener? = null
    private var onSecondaryClick: DialogInterface.OnClickListener? = null
    private var onTertiaryClick: DialogInterface.OnClickListener? = null
    private var onBackButtonClick: DialogInterface.OnDismissListener? = null
    private var onCloseButtonClick: DialogInterface.OnDismissListener? = null
    private var onDialogDismiss: DialogInterface.OnDismissListener? = null
    private var onCancelDialog: DialogInterface.OnCancelListener? = null

    protected val viewModel: TrueIdDialogViewModel by viewModels()

    protected val data: DialogData?
        get() = requireArguments().getParcelable(BUNDLE_KEY_DATA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, data?.theme?.default ?: R.style.TrueIdDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context
        return if (data?.type == DialogType.BOTTOM_SHEET || context == null) {
            super.onCreateDialog(savedInstanceState).apply {
                setOnShowListener {
                    findViewById<View>(
                        com.google.android.material.R.id.design_bottom_sheet
                    )?.let { view ->
                        BottomSheetBehavior.from(view).run {
                            peekHeight = view.height
                            state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }
                }
            }
        } else {
            Dialog(context, theme)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.let {
            if (onPrimaryClick != null) {
                it.onPrimaryClick = onPrimaryClick
            }
            if (onSecondaryClick != null) {
                it.onSecondaryClick = onSecondaryClick
            }
            if (onTertiaryClick != null) {
                it.onTertiaryClick = onTertiaryClick
            }
            if (onCloseButtonClick != null) {
                it.onCloseButtonClick = onCloseButtonClick
            }
            if (onBackButtonClick != null) {
                it.onBackButtonClick = onBackButtonClick
            }
            if (onDialogDismiss != null) {
                it.onDismissListener = onDialogDismiss
            }
            if (onCancelDialog != null) {
                it.onCancelListener = onCancelDialog
            }
        }
    }

    override fun show(manager: FragmentManager) {
        super.show(manager, this::class.simpleName)
        manager.executePendingTransactions()
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        viewModel.onDismissListener?.onDismiss(this)
    }

    override fun onCancel(dialog: android.content.DialogInterface) {
        dialog.dismiss()
        viewModel.onCancelListener?.onCancel(this)
    }

    protected fun init(data: DialogData) {
        arguments = bundleOf(
            BUNDLE_KEY_DATA to data
        )
    }

    fun setOnClickListener(
        onPrimaryClick: DialogInterface.OnClickListener?
    ) {
        setOnClickListener(onPrimaryClick, null, null)
    }

    fun setOnClickListener(
        onPrimaryClick: DialogInterface.OnClickListener?,
        onSecondaryClick: DialogInterface.OnClickListener?
    ) {
        setOnClickListener(onPrimaryClick, onSecondaryClick, null)
    }

    fun setOnClickListener(
        onPrimaryClick: DialogInterface.OnClickListener?,
        onSecondaryClick: DialogInterface.OnClickListener?,
        onTertiaryClick: DialogInterface.OnClickListener?
    ) {
        this.onPrimaryClick = onPrimaryClick
        this.onSecondaryClick = onSecondaryClick
        this.onTertiaryClick = onTertiaryClick
    }

    fun setOnClickBackListener(onBackButtonClick: DialogInterface.OnDismissListener?) {
        this.onBackButtonClick = onBackButtonClick
    }

    fun setOnClickCloseListener(onBackButtonClick: DialogInterface.OnDismissListener?) {
        this.onCloseButtonClick = onBackButtonClick
    }

    fun setOnDialogDismissListener(listener: DialogInterface.OnDismissListener?) {
        this.onDialogDismiss = listener
    }

    fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
        this.onCancelDialog = listener
    }

    fun initView(
        icon: AppCompatImageView,
        title: AppCompatTextView,
        subTitle: AppCompatTextView,
        primary: AppCompatButton
    ) {
        initView(icon, title, subTitle, primary, null, null)
    }

    fun initView(
        icon: AppCompatImageView,
        title: AppCompatTextView,
        subTitle: AppCompatTextView,
        primary: AppCompatButton,
        secondary: AppCompatButton?
    ) {
        initView(icon, title, subTitle, primary, secondary, null)
    }

    fun initView(
        icon: AppCompatImageView,
        title: AppCompatTextView,
        subTitle: AppCompatTextView,
        primary: AppCompatButton,
        secondary: AppCompatButton?,
        tertiary: AppCompatButton?
    ) {
        initIconView(icon)
        initTextView(title, subTitle)
        initButtonView(
            primary, secondary, tertiary
        )
        initCancelable()
    }

    fun initBackAndCloseButton(
        closeButton: AppCompatImageButton?,
        backButton: AppCompatImageButton?
    ) {
        closeButton?.let { button ->
            initCloseButtonListener(button)
        }
        backButton?.let { button ->
            initBackButtonListener(button)
        }
    }

    private fun initIconView(view: AppCompatImageView) {
        data?.let { data ->
            with(view) {
                val iconType = data.iconType
                iconType.icon?.let { icon ->
                    when (icon) {
                        is AdaptiveIcon -> {
                            if (!iconType.ignoreTint) {
                                context.obtainStyledAttributes(
                                    theme,
                                    intArrayOf(R.attr.iconColor)
                                ).apply {
                                    val tintColor = getColor(0, 0)
                                    setColorFilter(
                                        tintColor,
                                        PorterDuff.Mode.SRC_IN
                                    )
                                }.recycle()
                            }
                            setImageDrawable(
                                ContextCompat.getDrawable(
                                    context, icon.res
                                )
                            )
                        }
                        is ThemedIcon -> {
                            setImageDrawable(
                                ContextCompat.getDrawable(
                                    context, icon.getIconByTheme(data.theme)
                                )
                            )
                        }

                        else -> {
                            /* Do something */
                        }
                    }
                }
            }
        }
    }

    private fun initTextView(
        title: AppCompatTextView,
        subTitle: AppCompatTextView,
    ) {
        data?.let { data ->
            title.text = data.title
            subTitle.text = data.subtitle
            subTitle.textAlignment = data.contentAlignment.value
        }
    }

    private fun initButtonView(
        primary: AppCompatButton,
        secondary: AppCompatButton? = null,
        tertiary: AppCompatButton? = null
    ) {
        data?.let { data ->
            primary.text = data.primaryButton.text
            secondary?.text = data.secondaryButton.text
            tertiary?.text = data.tertiaryButton.text
        }
        initButtonListener(
            primary, secondary, tertiary
        )
    }

    private fun initButtonListener(
        primary: AppCompatButton,
        secondary: AppCompatButton? = null,
        tertiary: AppCompatButton? = null
    ) {
        primary.setOnClickListenerAndDismiss {
            viewModel.onPrimaryClick?.onClick(this)
        }
        secondary?.setOnClickListenerAndDismiss {
            viewModel.onSecondaryClick?.onClick(this)
        }
        tertiary?.setOnClickListenerAndDismiss {
            viewModel.onTertiaryClick?.onClick(this)
        }
    }

    private fun initBackButtonListener(button: AppCompatImageButton) {
        button.setOnClickListenerAndDismiss {
            viewModel.onBackButtonClick?.onDismiss(this)
        }
    }

    private fun initCloseButtonListener(button: AppCompatImageButton) {
        button.setOnClickListenerAndDismiss {
            viewModel.onCloseButtonClick?.onDismiss(this)
        }
    }

    private fun initCancelable() {
        data?.isCancelable?.let { value ->
            dialog?.setCancelable(value)
        }
        data?.isCanceledOnTouchOutSide?.let { value ->
            dialog?.setCanceledOnTouchOutside(value)
        }
    }

    private fun View.setOnClickListenerAndDismiss(listener: View.OnClickListener) {
        setOnClickListener {
            dialog?.dismiss()
            listener.onClick(this)
        }
    }
}
