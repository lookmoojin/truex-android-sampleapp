package com.truedigital.common.share.datalegacy.data.other.model.response.truepoint

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class PrivilegeTrueYou() : Parcelable {

    @SerializedName("cate_id")
    var cateId: String? = null

    @SerializedName("subcate_id")
    var subcateId: String? = null

    constructor(parcel: Parcel) : this() {
        cateId = parcel.readString()
        subcateId = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cateId)
        parcel.writeString(subcateId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PrivilegeTrueYou> {
        override fun createFromParcel(parcel: Parcel): PrivilegeTrueYou {
            return PrivilegeTrueYou(parcel)
        }

        override fun newArray(size: Int): Array<PrivilegeTrueYou?> {
            return arrayOfNulls(size)
        }
    }
}
