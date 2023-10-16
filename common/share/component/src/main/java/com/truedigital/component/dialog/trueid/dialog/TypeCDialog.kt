package com.truedigital.component.dialog.trueid.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truedigital.component.R
import com.truedigital.component.databinding.DialogTypeCBinding
import com.truedigital.component.dialog.trueid.DialogData
import com.truedigital.component.dialog.trueid.TrueIdDialogFragment
import com.truedigital.component.extension.isTablet

class TypeCDialog : TrueIdDialogFragment() {

    companion object {
        fun newInstance(
            data: DialogData
        ) = TypeCDialog().apply {
            init(data)
        }
    }

    private lateinit var binding: DialogTypeCBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, data?.theme?.default ?: R.style.TrueIdDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        context?.let { context ->
            if (context.isTablet) {
                return Dialog(context, theme)
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogTypeCBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        with(binding) {
            super.initView(
                icon = ivIcon,
                title = tvTitle,
                subTitle = tvSubTitle,
                primary = btnPrimary,
                secondary = btnSecondary,
                tertiary = btnTertiary
            )
        }
    }
}
