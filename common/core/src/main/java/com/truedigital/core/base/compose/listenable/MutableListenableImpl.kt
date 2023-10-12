package com.truedigital.core.base.compose.listenable

import java.util.UUID

class MutableListenableImpl<T>(initialValue: T) : MutableListenable<T> {

    override var value: T = initialValue
        set(value) {
            field = value
            notify(value)
        }

    private fun notify(value: T) {
        val copy = callBacks.toMap()
        copy.forEach {
            it.value.invoke(value)
        }
    }

    private var callBacks: MutableMap<UUID, ((T) -> Unit)> = mutableMapOf()

    override fun listen(immediate: Boolean, callBack: (T) -> Unit): Disposable {
        val key = UUID.randomUUID()
        callBacks[key] = callBack

        if (immediate) {
            callBack.invoke(value)
        }

        return object : Disposable {
            override fun dispose() {
                callBacks.remove(key)
            }
        }
    }
}

fun <T> mutableListenableOf(value: T): MutableListenable<T> {
    return MutableListenableImpl(value)
}
