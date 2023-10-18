package com.truedigital.features.tuned.presentation.components

import android.os.Bundle
import com.truedigital.features.tuned.presentation.common.Presenter

class PresenterComponent(val presenter: Presenter) : LifecycleComponent {
    override fun onStart(arguments: Bundle) {
        presenter.onStart(arguments)
    }

    override fun onStop() {
        presenter.onStop()
    }

    override fun onResume() {
        presenter.onResume()
    }

    override fun onPause() {
        presenter.onPause()
    }
}
