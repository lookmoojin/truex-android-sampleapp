package com.truedigital.common.share.datalegacy.utils

import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ArticleDetailStateUtil(private val dispatcher: CoroutineDispatcherProvider) {
    private val _onDialogDismiss = MutableSharedFlow<Unit>()
    val onDialogDismiss = _onDialogDismiss.asSharedFlow()

    fun dialogDismiss() {
        CoroutineScope(dispatcher.default()).launchSafe {
            _onDialogDismiss.emit(Unit)
        }
    }
}
