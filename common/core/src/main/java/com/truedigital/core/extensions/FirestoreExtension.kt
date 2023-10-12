package com.truedigital.core.extensions

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

fun <T> Task<T>.addOnSuccessListenerWithNewExecutor(
    success: (T) -> Unit
) = addOnSuccessListener(createNewExecutor()) {
    success.invoke(it)
}

fun <T> Task<T>.addOnCompleteListenerWithNewExecutor(
    success: (Task<T>) -> Unit
) = addOnCompleteListener(createNewExecutor()) {
    success.invoke(it)
}

fun <T> Task<T>.addOnFailureListenerWithNewExecutor(
    exception: (Exception) -> Unit
) = addOnFailureListener(createNewExecutor()) {
    exception.invoke(it)
}

fun <T> Task<T>.addOnCanceledListenerWithNewExecutor(
    cancel: (T) -> Unit
) = addOnCanceledListener(createNewExecutor()) {
    cancel.invoke(Unit as T)
}

fun DocumentReference.addSnapshotListenerWithNewExecutor(
    eventListener: EventListener<DocumentSnapshot>
) = addSnapshotListener(createNewExecutor(), eventListener)

const val CORE_POOL_SIZE = 2
const val MAX_POOL_SIZE = 2
const val KEEP_ALIVE_TIME_IN_SEC = 60L

private fun createNewExecutor(): ThreadPoolExecutor {
    /**Create a new ThreadPoolExecutor with 2 threads for each processor on the
     * device and a 60 second keep-alive time.
     * @see https://developers.google.com/android/guides/tasks#threading
     */

    val numCores = Runtime.getRuntime().availableProcessors()
    return ThreadPoolExecutor(
        numCores * CORE_POOL_SIZE, numCores * MAX_POOL_SIZE,
        KEEP_ALIVE_TIME_IN_SEC, TimeUnit.SECONDS, LinkedBlockingQueue()
    )
}
