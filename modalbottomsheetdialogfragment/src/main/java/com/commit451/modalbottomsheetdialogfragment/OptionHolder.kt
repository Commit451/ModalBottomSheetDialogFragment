package com.commit451.modalbottomsheetdialogfragment

import android.os.Parcel
import android.os.Parcelable

/**
 * Holds either the resource options or the custom option
 */
internal class OptionHolder(val resource: Int?, val optionRequest: OptionRequest?) : Parcelable {

    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readParcelable(OptionRequest::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(resource)
        writeParcelable(optionRequest, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OptionHolder> = object : Parcelable.Creator<OptionHolder> {
            override fun createFromParcel(source: Parcel): OptionHolder = OptionHolder(source)
            override fun newArray(size: Int): Array<OptionHolder?> = arrayOfNulls(size)
        }
    }
}