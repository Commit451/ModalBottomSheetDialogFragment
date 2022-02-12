package com.commit451.modalbottomsheetdialogfragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat

/**
 * Request for an option you can select within the modal
 */
data class OptionRequest(
    val id: Int,
    val title: String,
    @DrawableRes val icon: Int?
) : Parcelable {

    internal fun toOption(context: Context): Option {
        var drawable: Drawable? = null
        icon?.let {
            drawable = ResourcesCompat.getDrawable(context.resources, icon, context.theme)
        }

        return Option(id, title, drawable)
    }

    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString() ?: "",
        source.readValue(Int::class.java.classLoader) as Int?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(title)
        writeValue(icon)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OptionRequest> =
            object : Parcelable.Creator<OptionRequest> {
                override fun createFromParcel(source: Parcel): OptionRequest = OptionRequest(source)
                override fun newArray(size: Int): Array<OptionRequest?> = arrayOfNulls(size)
            }
    }
}