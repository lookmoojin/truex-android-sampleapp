package com.truedigital.component.base

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.truedigital.component.R
import com.truedigital.component.presentation.MainContainerViewModel
import com.truedigital.core.view.CoreFragment
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

abstract class BottomTabNavigationFragment(layoutId: Int) : CoreFragment(layoutId) {

    abstract fun sendTabAnalytic()

    open fun isHideBottomNavigation(): Boolean {
        return false
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val mainContainerViewModel: MainContainerViewModel by viewModels({ requireActivity() }) { viewModelFactory }

    private var backPressedCount = 0

    private val onDestinationChangedListener =
        NavController.OnDestinationChangedListener { controller, destination, _ ->
            if (destination.id != controller.graph.startDestinationId) {
                registerOnDestinationChangedListener()
            }
        }

    private val onDestinationChangedListener2 =
        NavController.OnDestinationChangedListener { controller, destination, _ ->
            if (destination.id == controller.graph.startDestinationId) {
                sendTabAnalytic()
                mainContainerViewModel.showBottomNavigation(true)
            } else if (isHideBottomNavigation()) {
                mainContainerViewModel.showBottomNavigation(false)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            findNavController().addOnDestinationChangedListener(onDestinationChangedListener)
        } catch (e: Exception) {
            // Do nothing
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBackButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            findNavController().removeOnDestinationChangedListener(onDestinationChangedListener)
            findNavController().removeOnDestinationChangedListener(onDestinationChangedListener2)
        } catch (e: Exception) {
            // Do nothing
        }
    }

    fun bottomTabBackPressed() {
        if (!mainContainerViewModel.isHomeActive()) {
            mainContainerViewModel.switchBottomTab(MainContainerViewModel.TAB_HOME_SLUG)
        } else {
            doubleBackPressToExit()
        }
    }

    private fun registerOnDestinationChangedListener() {
        try {
            findNavController().removeOnDestinationChangedListener(onDestinationChangedListener)
            findNavController().removeOnDestinationChangedListener(onDestinationChangedListener2)
            findNavController().addOnDestinationChangedListener(onDestinationChangedListener2)
        } catch (e: Exception) {
            // Do nothing
        }
    }

    private fun initBackButton() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    bottomTabBackPressed()
                }
            }
        )
    }

    private fun doubleBackPressToExit() {
        val backPressedDelay = 3000

        backPressedCount++
        if (backPressedCount > 1) {
            backPressedCount = 0
            activity?.finishAndRemoveTask()
        } else {
            Toast.makeText(context, getString(R.string.press_back_to_exit), Toast.LENGTH_SHORT)
                .show()
            Handler().postDelayed({ backPressedCount = 0 }, backPressedDelay.toLong())
        }
    }
}
