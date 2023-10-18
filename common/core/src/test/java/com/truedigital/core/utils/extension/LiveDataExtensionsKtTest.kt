package com.truedigital.core.utils.extension

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.lifecycle.MutableLiveData
import com.jraska.livedata.test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LiveDataExtensionsKtTest {

    @BeforeEach
    fun setup() {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) = runnable.run()

            override fun postToMainThread(runnable: Runnable) = runnable.run()

            override fun isMainThread() = true
        })
    }

    @AfterEach
    fun tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    @Test
    fun `Given liveData value is null When update Then liveData is not working`() {
        val liveData = MutableLiveData<String>()
        liveData.update {
            "test"
        }
        liveData.test()
            .assertNoValue()
    }

    @Test
    fun `Given liveData value is not null When update Then liveData is working`() {
        val liveData = MutableLiveData("init")
        liveData.update {
            "test"
        }
        liveData.test()
            .assertValue("test")
    }
}
