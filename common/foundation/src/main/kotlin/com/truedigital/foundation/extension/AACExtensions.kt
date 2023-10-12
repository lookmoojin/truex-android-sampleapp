package com.truedigital.foundation.extension

import android.os.Looper
import androidx.annotation.MainThread
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.remove as remove

class BaseViewModelFactory<T>(val creator: () -> T) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(noinline creator: (() -> T)): T {
    return ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}

inline fun <reified T : ViewModel> DialogFragment.getViewModel(noinline creator: (() -> T)): T {
    return ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}

inline fun <reified T : ViewModel> Fragment.getViewModel(noinline creator: (() -> T)): T {
    return ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}

/**
 * Convert LiveData to Single LiveData
 */
fun <T> LiveData<T>.toSingleEvent(): LiveData<T> {
    val result = LiveEvent<T>()
    result.addSource(this) {
        result.value = it
    }
    return result
}

/**
 * Convert MutableLiveData to Single MutableLiveData
 */
fun <T> MutableLiveData<T>.toSingleEvent(): MutableLiveData<T> {
    val result = LiveEvent<T>()
    result.addSource(this) {
        result.value = it
    }
    return result
}

/**
 * A lifecycle-aware observable that sends only new updates after subscription, used for events like
 * navigation and Snackbar messages.
 *
 *
 * This avoids a common problem with events: on configuration change (like rotation) an update
 * can be emitted if the observer is active. This LiveData only calls the observable if there's an
 * explicit call to setValue() or call().
 *
 *
 * Note that only one observer is going to be notified of changes.
 */
class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        // Observe the internal MutableLiveData
        super.observe(
            owner,
            Observer { t ->
                if (mPending.compareAndSet(true, false)) {
                    observer.onChanged(t)
                }
            }
        )
    }

    override fun setValue(t: T?) {
        mPending.set(true)

        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.setValue(t)
        } else {
            super.postValue(t)
        }
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    fun call() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            value = null
        } else {
            postValue(null)
        }
    }

    companion object {
        private val TAG = "SingleLiveEvent"
    }
}

/**
 * This class like Single in RxJava
 */
class LiveEvent<T> : MediatorLiveData<T>() {

    private val observers = ConcurrentHashMap<LifecycleOwner, MutableSet<ObserverWrapper<in T>>>()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val wrapper = ObserverWrapper(observer)
        val set = observers[owner]
        set?.apply {
            add(wrapper)
        } ?: run {
            val newSet = Collections.newSetFromMap(ConcurrentHashMap<ObserverWrapper<in T>, Boolean>())
            newSet.add(wrapper)
            observers[owner] = newSet
        }
        super.observe(owner, wrapper)
    }

    override fun removeObservers(owner: LifecycleOwner) {
        observers.remove(owner)
        super.removeObservers(owner)
    }

    override fun removeObserver(observer: Observer<in T>) {
        observers.forEach {
            if (it.value.remove<Observer<in T>>(observer)) {
                if (it.value.isEmpty()) {
                    observers.remove(it.key)
                }
                return@forEach
            }
        }
        super.removeObserver(observer)
    }

    @MainThread
    override fun setValue(t: T?) {
        observers.forEach { it.value.forEach { wrapper -> wrapper.newValue() } }
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    private class ObserverWrapper<T>(private val observer: Observer<T>) : Observer<T> {

        private val pending = AtomicBoolean(false)

        override fun onChanged(t: T) {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }

        fun newValue() {
            pending.set(true)
        }
    }
}
