package com.commit451.modalbottomsheetdialogfragment

import android.graphics.drawable.Drawable

/**
 * The inflated option in the modal
 */
data class Option(
    val id: Int,
    var title: CharSequence,
    var icon: Drawable?
)