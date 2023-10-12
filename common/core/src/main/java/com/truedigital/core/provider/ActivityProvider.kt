package com.truedigital.core.provider

import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentLinkedQueue

interface ActivityCreatedListener {
    fun onActivityCreated(activity: Activity)
}

interface ActivityResumedListener {
    fun onActivityResumed(activity: Activity)
}

interface ActivityPausedListener {
    fun onActivityPaused(activity: Activity)
}

interface ActivityDestroyedListener {
    fun onActivityDestroyed(activity: Activity)
}

interface ActivityStoppedListener {
    fun onActivityStopped(activity: Activity)
}

interface ActivityStartedListener {
    fun onActivityStarted(activity: Activity)
}

enum class ActivityState {
    CREATE,
    START,
    RESUME,
    PAUSE,
    STOP,
    DESTROY
}

class ActivityAndState {
    val name: String
    val activity: WeakReference<Activity>
    val state: ActivityState

    constructor(activity: Activity, state: ActivityState) {
        this.name = (activity as Any).toString() // the adress
        this.activity = WeakReference(activity)
        this.state = state
    }
}

object ActivityProvider {
    private val activityCreatedListeners = ConcurrentLinkedQueue<ActivityCreatedListener>()
    private val activityResumedListeners = ConcurrentLinkedQueue<ActivityResumedListener>()
    private val activityPausedListeners = ConcurrentLinkedQueue<ActivityPausedListener>()
    private val activityStoppedListeners = ConcurrentLinkedQueue<ActivityStoppedListener>()
    private val activityStartedListeners = ConcurrentLinkedQueue<ActivityStartedListener>()
    private val activityDestroyedListeners = ConcurrentLinkedQueue<ActivityDestroyedListener>()

    @JvmStatic
    fun addCreatedListener(listener: ActivityCreatedListener) {
        activityCreatedListeners.add(listener)
    }

    @JvmStatic
    fun removeCreatedListener(listener: ActivityCreatedListener) {
        activityCreatedListeners.remove(listener)
    }

    @JvmStatic
    fun addResumedListener(listener: ActivityResumedListener) {
        activityResumedListeners.add(listener)
    }

    @JvmStatic
    fun removeResumedListener(listener: ActivityResumedListener) {
        activityResumedListeners.remove(listener)
    }

    @JvmStatic
    fun addPausedListener(listener: ActivityPausedListener) {
        activityPausedListeners.add(listener)
    }

    @JvmStatic
    fun removePausedListener(listener: ActivityPausedListener) {
        activityPausedListeners.remove(listener)
    }

    @JvmStatic
    fun addDestroyedListener(listener: ActivityDestroyedListener) {
        activityDestroyedListeners.add(listener)
    }

    @JvmStatic
    fun removeDestroyedListener(listener: ActivityDestroyedListener) {
        activityDestroyedListeners.remove(listener)
    }

    @JvmStatic
    fun addStoppedListener(listener: ActivityStoppedListener) {
        activityStoppedListeners.add(listener)
    }

    @JvmStatic
    fun removeStoppedListener(listener: ActivityStoppedListener) {
        activityStoppedListeners.remove(listener)
    }

    @JvmStatic
    fun addStartedListener(listener: ActivityStartedListener) {
        activityStartedListeners.add(listener)
    }

    @JvmStatic
    fun removeStartedListener(listener: ActivityStartedListener) {
        activityStartedListeners.remove(listener)
    }

    internal fun pingResumedListeners(activity: Activity) {
        _activitiesState.trySend(ActivityAndState(activity, ActivityState.RESUME)).isSuccess
        offerIfDiffer(activity)
        activityResumedListeners.forEach {
            it.onActivityResumed(activity)
        }
    }

    internal fun pingPausedListeners(activity: Activity) {
        _activitiesState.trySend(ActivityAndState(activity, ActivityState.PAUSE)).isSuccess
        activityPausedListeners.forEach {
            it.onActivityPaused(activity)
        }
    }

    internal fun pingCreatedListeners(activity: Activity) {
        offerIfDiffer(activity)
        _activitiesState.trySend(ActivityAndState(activity, ActivityState.CREATE)).isSuccess
        activityCreatedListeners.forEach {
            it.onActivityCreated(activity)
        }
    }

    internal fun pingDestroyedListeners(activity: Activity) {
        _activitiesState.trySend(ActivityAndState(activity, ActivityState.DESTROY)).isSuccess
        activityDestroyedListeners.forEach {
            it.onActivityDestroyed(activity)
        }
    }

    internal fun pingStartedListeners(activity: Activity) {
        _activitiesState.trySend(ActivityAndState(activity, ActivityState.START)).isSuccess
        activityStartedListeners.forEach {
            it.onActivityStarted(activity)
        }
    }

    internal fun pingStoppedListeners(activity: Activity) {
        _activitiesState.trySend(ActivityAndState(activity, ActivityState.STOP)).isSuccess
        activityStoppedListeners.forEach {
            it.onActivityStopped(activity)
        }
    }

    private fun offerIfDiffer(newActivity: Activity) {
        val current = currentActivity
        if (current == null || current != newActivity) {
            _currentActivity.trySend(WeakReference(newActivity)).isSuccess
        }
    }

    private val _currentActivity = ConflatedBroadcastChannel<WeakReference<Activity>>()

    @JvmStatic
    val currentActivity: Activity?
        get() {
            return _currentActivity.valueOrNull?.get()
        }

    private val _activitiesState = ConflatedBroadcastChannel<ActivityAndState>()
    private val listenActivitiesState: Flow<ActivityAndState> = _activitiesState.asFlow()

    fun listenCreated() = callbackFlow<Activity> {
        val listener =
            object : ActivityCreatedListener { // implementation of some callback interface
                override fun onActivityCreated(activity: Activity) {
                    trySend(activity).isSuccess
                }
            }
        addCreatedListener(listener)
        // Suspend until either onCompleted or external cancellation are invoked
        awaitClose { removeCreatedListener(listener) }
    }

    fun listenStarted() = callbackFlow<Activity> {
        val listener =
            object : ActivityStartedListener { // implementation of some callback interface
                override fun onActivityStarted(activity: Activity) {
                    trySend(activity).isSuccess
                }
            }
        addStartedListener(listener)
        // Suspend until either onCompleted or external cancellation are invoked
        awaitClose { removeStartedListener(listener) }
    }

    fun listenResumed() = callbackFlow<Activity> {
        val listener =
            object : ActivityResumedListener { // implementation of some callback interface
                override fun onActivityResumed(activity: Activity) {
                    trySend(activity).isSuccess
                }
            }
        addResumedListener(listener)
        // Suspend until either onCompleted or external cancellation are invoked
        awaitClose { removeResumedListener(listener) }
    }

    fun listenDestroyed() = callbackFlow<Activity> {
        val listener =
            object : ActivityDestroyedListener { // implementation of some callback interface
                override fun onActivityDestroyed(activity: Activity) {
                    trySend(activity).isSuccess
                }
            }
        addDestroyedListener(listener)
        // Suspend until either onCompleted or external cancellation are invoked
        awaitClose { removeDestroyedListener(listener) }
    }

    fun listenStopped() = callbackFlow<Activity> {
        val listener =
            object : ActivityStoppedListener { // implementation of some callback interface
                override fun onActivityStopped(activity: Activity) {
                    trySend(activity).isSuccess
                }
            }
        addStoppedListener(listener)
        // Suspend until either onCompleted or external cancellation are invoked
        awaitClose { removeStoppedListener(listener) }
    }

    fun listenPaused() = callbackFlow<Activity> {
        val listener =
            object : ActivityPausedListener { // implementation of some callback interface
                override fun onActivityPaused(activity: Activity) {
                    trySend(activity).isSuccess
                }
            }
        addPausedListener(listener)
        // Suspend until either onCompleted or external cancellation are invoked
        awaitClose { removePausedListener(listener) }
    }

    fun listenActivityChanged() = listenActivitiesState
        .filter { it.state == ActivityState.RESUME }
        .distinctUntilChangedBy { it.name }
}

class LastActivityProvider : EmptyProvider() {
    override fun onCreate(): Boolean {
        BaseApplicationProvider.listen { application ->
            application.registerActivityLifecycleCallbacks(object :
                    Application.ActivityLifecycleCallbacks {
                    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                        ActivityProvider.pingCreatedListeners(activity)
                    }

                    override fun onActivityStarted(activity: Activity) {
                        ActivityProvider.pingStartedListeners(activity)
                    }

                    override fun onActivityResumed(activity: Activity) {
                        ActivityProvider.pingResumedListeners(activity)
                    }

                    override fun onActivityPaused(activity: Activity) {
                        ActivityProvider.pingPausedListeners(activity)
                    }

                    override fun onActivityStopped(activity: Activity) {
                        ActivityProvider.pingStoppedListeners(activity)
                    }

                    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                        // DO NOTHING
                    }

                    override fun onActivityDestroyed(activity: Activity) {
                        ActivityProvider.pingDestroyedListeners(activity)
                    }
                })
        }
        return true
    }
}
