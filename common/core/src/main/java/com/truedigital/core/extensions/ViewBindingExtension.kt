package com.truedigital.core.extensions

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.newrelic.agent.android.NewRelic
import com.truedigital.core.BuildConfig
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Ref. https://medium.com/@Zhuinden/simple-one-liner-viewbinding-in-fragments-and-activities-with-kotlin-961430c6c07c
 *
 * Updated v. 2.38.0
 * Ref. https://itnext.io/an-update-to-the-fragmentviewbindingdelegate-the-bug-weve-inherited-from-autoclearedvalue-7fc0a89fcae1
 *
 * Ref. https://gist.github.com/Zhuinden/ea3189198938bd16c03db628e084a4fa#gistcomment-3934657
 *
 **/

class ViewBindingExtension<T : ViewBinding>(
    val fragment: Fragment,
    val viewBindingFactory: (Fragment) -> T,
    val cleanUp: ((T?) -> Unit)?
) : ReadOnlyProperty<Fragment, T> {

    // A backing property to hold our value
    private var binding: T? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            val viewLifecycleOwnerLiveDataObserver =
                Observer<LifecycleOwner?> {
                    val viewLifecycleOwner = it ?: return@Observer

                    viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            cleanUp?.invoke(binding)
                            binding = null
                        }
                    })
                }

            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observeForever(
                    viewLifecycleOwnerLiveDataObserver
                )
            }

            override fun onDestroy(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.removeObserver(
                    viewLifecycleOwnerLiveDataObserver
                )
            }
        })
    }

    override fun getValue(
        thisRef: Fragment,
        property: KProperty<*>
    ): T {
        val binding = binding
        if (binding != null) {
            return binding
        }

        if (BuildConfig.DEBUG) {
            warningMsgSafe(fragment)
        } else {
            try {
                warningMsgSafe(fragment)
            } catch (error: Exception) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ViewBindingExtension.getValue()",
                    "Value" to "Problem with ViewBindingExtension.getValue() caused by $error"
                )
                NewRelic.recordHandledException(Exception(error), handlingExceptionMap)
            }
        }

        return viewBindingFactory(thisRef).also { this.binding = it }
    }
}

private fun warningMsgSafe(fragment: Fragment) {
    val lifecycle = fragment.viewLifecycleOwner.lifecycle
    if (lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED).not()) {
        throw IllegalStateException("Should not attempt to get bindings when Fragment views are destroyed.")
    }
}

inline fun <T : ViewBinding> Fragment.viewBinding(
    crossinline viewBindingFactory: (View) -> T,
    noinline cleanUp: ((T?) -> Unit)? = null
): ViewBindingExtension<T> =
    ViewBindingExtension(this, { f -> viewBindingFactory(f.requireView()) }, cleanUp)

inline fun <T : ViewBinding> Fragment.viewInflateBinding(
    crossinline bindingInflater: (LayoutInflater) -> T,
    noinline cleanUp: ((T?) -> Unit)? = null,
): ViewBindingExtension<T> =
    ViewBindingExtension(this, { f -> bindingInflater(f.layoutInflater) }, cleanUp)

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) =
    lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater.invoke(layoutInflater)
    }
