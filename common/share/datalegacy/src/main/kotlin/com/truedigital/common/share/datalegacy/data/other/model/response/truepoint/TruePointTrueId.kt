package com.truedigital.common.share.datalegacy.data.other.model.response.truepoint

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by sasara on 10/31/2017 AD.
 */
class TruePointTrueId() : Parcelable {

    var button_en: String? = null

    var button_th: String? = null

    var deep_link: String? = null

    var url_webview_en: String? = null

    var url_webview_th: String? = null

    var shelf_slug: List<String>? = arrayListOf()

    var privilege_trueyou: PrivilegeTrueYou? = null

    var post_webview_trueyou: String? = null

    constructor(parcel: Parcel) : this() {
        button_en = parcel.readString()
        button_th = parcel.readString()
        deep_link = parcel.readString()
        url_webview_en = parcel.readString()
        url_webview_th = parcel.readString()
        shelf_slug = parcel.createStringArrayList()
        privilege_trueyou = parcel.readParcelable(PrivilegeTrueYou::class.java.classLoader)
        post_webview_trueyou = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(button_en)
        parcel.writeString(button_th)
        parcel.writeString(deep_link)
        parcel.writeString(url_webview_en)
        parcel.writeString(url_webview_th)
        parcel.writeStringList(shelf_slug)
        parcel.writeParcelable(privilege_trueyou, flags)
        parcel.writeString(post_webview_trueyou)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TruePointTrueId> {
        override fun createFromParcel(parcel: Parcel): TruePointTrueId {
            return TruePointTrueId(parcel)
        }

        override fun newArray(size: Int): Array<TruePointTrueId?> {
            return arrayOfNulls(size)
        }
    }
}
