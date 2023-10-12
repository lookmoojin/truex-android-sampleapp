package com.truedigital.common.share.datalegacy.data

import javax.inject.Inject

interface TvsNowCacheSourceRepository {
    fun isOpenFromTrueVision(): Boolean
    fun setOpenFromTrueVision()
    fun clearOpenFromTrueVision()
}

class TvsNowCacheSourceRepositoryImpl @Inject constructor() : TvsNowCacheSourceRepository {

    private var openFromTrueVision = false

    override fun isOpenFromTrueVision(): Boolean = openFromTrueVision

    override fun setOpenFromTrueVision() {
        openFromTrueVision = true
    }

    override fun clearOpenFromTrueVision() {
        openFromTrueVision = false
    }
}
