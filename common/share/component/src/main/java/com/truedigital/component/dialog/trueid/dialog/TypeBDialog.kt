package com.truedigital.component.dialog.trueid.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truedigital.component.databinding.DialogTypeBBinding
import com.truedigital.component.dialog.trueid.DialogData
import com.truedigital.component.dialog.trueid.TrueIdDialogFragment
import com.truedigital.component.extension.isTablet

class TypeBDialog : TrueIdDialogFragment() {

    companion object {
        fun newInstance(
            data: DialogData
        ) = TypeBDialog().apply {
            init(data)
        }
    }

    private lateinit var binding: DialogTypeBBinding

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
        binding = DialogTypeBBinding.inflate(
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
                secondary = btnSecondary
            )
        }
    }
}
