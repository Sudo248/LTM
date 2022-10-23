package com.sudo248.ltm.utils

import android.app.Activity
import android.view.inputmethod.InputMethodManager


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 17:19 - 23/10/2022
 */
object KeyboardUtils {
    fun hide(activity: Activity) {
        activity.currentFocus?.let {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}