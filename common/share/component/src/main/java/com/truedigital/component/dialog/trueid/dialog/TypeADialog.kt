package com.truedigital.component.dialog.trueid.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truedigital.component.databinding.DialogTypeABinding
import com.truedigital.component.dialog.trueid.DialogData
import com.truedigital.component.dialog.trueid.TrueIdDialogFragment

class TypeADialog : TrueIdDialogFragment() {

    companion object {
        fun newInstance(
            data: DialogData
        ) = TypeADialog().apply {
            init(data)
        }
    }

    private lateinit var binding: DialogTypeABinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogTypeABinding.inflate(
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
                primary = btnPrimary
            )
        }
    }
}
