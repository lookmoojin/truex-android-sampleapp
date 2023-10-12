package com.truedigital.foundation.initializer

import android.content.Context
import androidx.startup.Initializer
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

const val CORE_POOL_SIZE = 2
const val MAX_POOL_SIZE = 2
const val KEEP_ALIVE_TIME_IN_SEC = 1L

abstract class InitializerBackgroundThread : Initializer<Unit> {

    abstract fun createBackground(context: Context)

    abstract fun getDependencies(): List<Class<out Initializer<*>>>

    override fun create(context: Context) {
        createNewExecutor().execute {
            createBackground(context)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return getDependencies()
    }

    private fun createNewExecutor(): ThreadPoolExecutor {
        /**Create a new ThreadPoolExecutor with 2 threads for each processor on the
         * device and a 1 second keep-alive time.
         */

        val numCores = Runtime.getRuntime().availableProcessors()
        return ThreadPoolExecutor(
            numCores * CORE_POOL_SIZE, numCores * MAX_POOL_SIZE,
            KEEP_ALIVE_TIME_IN_SEC, TimeUnit.SECONDS, LinkedBlockingQueue()
        )
    }
}
