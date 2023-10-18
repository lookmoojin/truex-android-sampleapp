package com.truedigital.features.tuned.data.setting.repository

interface SettingRepository {
    fun setAllowMobileDataStreaming(isAllowed: Boolean)
    fun allowMobileDataStreaming(): Boolean

    fun setShufflePlay(enable: Boolean)
    fun isShufflePlayEnabled(): Boolean

    fun setRepeatMode(mode: Int)
    fun getRepeatMode(): Int

    fun addAdCounter()
    fun resetAdCounter()
    fun getAdCounter(): Int
}
