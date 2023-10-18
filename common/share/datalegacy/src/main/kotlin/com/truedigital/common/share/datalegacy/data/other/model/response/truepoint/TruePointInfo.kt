package com.truedigital.common.share.datalegacy.data.other.model.response.truepoint

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by sasara on 10/31/2017 AD.
 */
class TruePointInfo() : Parcelable {

    var trueid: TruePointTrueId? = null

    constructor(parcel: Parcel) : this() {
        trueid = parcel.readParcelable(TruePointTrueId::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(trueid, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TruePointInfo> {
        override fun createFromParcel(parcel: Parcel): TruePointInfo {
            return TruePointInfo(parcel)
        }

        override fun newArray(size: Int): Array<TruePointInfo?> {
            return arrayOfNulls(size)
        }
    }
}
