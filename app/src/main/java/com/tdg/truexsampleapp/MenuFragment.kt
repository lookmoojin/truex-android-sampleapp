package com.tdg.truexsampleapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tdg.onboarding.domain.model.WhatNewData
import com.tdg.onboarding.presentation.WhatNewDialogFragment
import com.tdg.truexsampleapp.databinding.FragmentMenuBinding
import com.tdg.truexsampleapp.injections.HomeComponent
import com.truedigital.features.truecloudv3.presentation.MainTrueCloudV3Activity
import javax.inject.Inject

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val menuViewModel: MenuViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeComponent.getInstance().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelObserve()
        binding.trueCloudMenu.setOnClickListener {
            val intent = Intent(requireContext(), MainTrueCloudV3Activity::class.java)
            startActivity(intent)
        }

        menuViewModel.getWhatNewConfig()
    }

    private fun viewModelObserve() = with(menuViewModel) {
        onDisplay.observe(viewLifecycleOwner) {
            openWhatNewDialog(it)
        }
    }

    private fun openWhatNewDialog(whatNewData: WhatNewData) {
        if (!isAdded) {
            return
        }
        try {
            val ft = parentFragmentManager.beginTransaction()
            val whatsNewDialog = WhatNewDialogFragment.newInstance(whatNewData)
            ft.add(whatsNewDialog, WhatNewDialogFragment.TAG)
            ft.commitAllowingStateLoss()
        } catch (e: IllegalStateException) {
//            val handlingExceptionMap = mapOf(
//                "Key" to "MainFragment:",
//                "Value" to "on OpenWhatNewDialog",
//            )
//            NewRelic.recordHandledException(Exception(e.localizedMessage), handlingExceptionMap)
        }
    }
}