package com.truedigital.component

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

class ExampleTest {
    @ExperimentalCoroutinesApi
    val testDispatcher = TestCoroutineDispatcher()
}
