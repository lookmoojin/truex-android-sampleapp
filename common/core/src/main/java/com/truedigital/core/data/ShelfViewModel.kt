package com.truedigital.core.data

abstract class ShelfViewModel {
    abstract var vType: String
    open var shelfId: String = ""
    open var shelfTitle: String = ""
    open var shelfIndex: Int = 0
    val moduleName: String?
        get() = vType.split("/").getOrNull(0)
}
