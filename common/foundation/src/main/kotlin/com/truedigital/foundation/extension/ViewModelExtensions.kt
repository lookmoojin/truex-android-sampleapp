package com.truedigital.foundation.extension

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

@MainThread
inline fun <reified VM : ViewModel> Fragment.parentViewModels(
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
) = viewModels<VM>(
    { requireParentFragment() },
    extrasProducer,
    factoryProducer
)

// TODO: Need to refactor it due to there still have a crash issue
/*
@MainThread
inline fun <reified VM : ViewModel> View.viewModels(
    noinline ownerProducer: () -> ViewModelStoreOwner = { findViewTreeViewModelStoreOwner()!! },
    noinline factoryProducer: (() -> ViewModelProvider.Factory)
) = ViewModelLazy(VM::class, { ownerProducer().viewModelStore }, factoryProducer)
*/
