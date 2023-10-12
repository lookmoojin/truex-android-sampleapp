package com.truedigital.foundation.presentations

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras

class AssistedViewModelFactory<VM : ViewModel>(
    val creator: () -> VM
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = creator() as VM
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.assistedViewModels(
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    noinline creator: () -> VM
) = viewModels<VM>(
    { ownerProducer() },
    extrasProducer,
    { AssistedViewModelFactory(creator) }
)

@MainThread
inline fun <reified VM : ViewModel> Fragment.activityAssistedViewModels(
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline creator: () -> VM
) = viewModels<VM>(
    { requireActivity() },
    extrasProducer,
    { AssistedViewModelFactory(creator) }
)

@MainThread
inline fun <reified VM : ViewModel> Fragment.parentAssistedViewModels(
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline creator: () -> VM
) = viewModels<VM>(
    { requireParentFragment() },
    extrasProducer,
    { AssistedViewModelFactory(creator) }
)

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.assistedViewModels(
    noinline creator: () -> VM
) = viewModels<VM> {
    AssistedViewModelFactory(creator)
}
