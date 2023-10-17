package com.truedigital.features.tuned.presentation.popups.view

import android.content.Context
import android.os.Bundle
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.databinding.DialogUpgradeBinding
import com.truedigital.foundation.extension.onClick

/**
 * This dialog requires the CMS configurable translation module which is not included in the POC.
 * Using placeholder string for now.
 */

class UpgradePremiumDialog(dialogContext: Context) : InfoDialog(dialogContext) {

    private val binding: DialogUpgradeBinding by lazy {
        DialogUpgradeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonClose.onClick { dismiss() }

        binding.textViewTitle.text = "upgrade_title" // translations.get(context, "upgrade_title")

//        translations.get(context, "upgrade_description")?.let {
        binding.textViewDescription.text = "upgrade_description" // it.parseHtml()
//        }

        binding.buttonUpgrade.text = "upgrade_button" // translations.get(context, "upgrade_button")

        binding.buttonUpgrade.onClick {
            dismiss()
        }
    }
}
