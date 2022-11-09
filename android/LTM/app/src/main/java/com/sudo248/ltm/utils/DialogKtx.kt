package com.sudo248.ltm.utils

import android.app.Dialog
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 01:13 - 05/11/2022
 */
fun Dialog.setTransparentBackground() {
    if (this is BottomSheetDialog) {
        val bottomSheet = findViewById<View?>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.setBackgroundResource(android.R.color.transparent)
    } else {
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}

fun Dialog.setLayoutParamsRatio(ratioWidth: Float? = null, ratioHeight: Float? = null) {
    val widthDialog =
        if (ratioWidth != null) Resources.getSystem().displayMetrics.widthPixels * ratioWidth
        else ViewGroup.LayoutParams.WRAP_CONTENT
    val heightDialog =
        if (ratioHeight != null) Resources.getSystem().displayMetrics.heightPixels * ratioHeight
        else ViewGroup.LayoutParams.WRAP_CONTENT
    window?.setLayout(widthDialog.toInt(), heightDialog.toInt())
}

fun Dialog.showWithTransparentBackground() {
    setTransparentBackground()
    show()
    setLayoutParamsRatio(0.9f)
}