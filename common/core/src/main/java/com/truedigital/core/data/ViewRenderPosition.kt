package com.truedigital.core.data

data class ViewRenderPosition(
    val start: Int,
    val end: Int
) {
    fun isActivePosition(
        parentStartPosition: Int,
        parentEndPosition: Int
    ): Boolean {

        if (parentStartPosition >= end) return false
        if (parentEndPosition <= start) return false

        val deviceHeight = (parentEndPosition - parentStartPosition).toDouble()
        if (start < parentStartPosition) {
            // Over top
            return (end - parentStartPosition) / deviceHeight > 0
        }

        if (end > parentEndPosition) {
            // Over end
            return (parentEndPosition - start) / deviceHeight > 0
        }

        return true
    }
}
