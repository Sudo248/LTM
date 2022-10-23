package com.sudo248.ltm.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.sudo248.ltm.R
import com.sudo248.ltm.databinding.DialogLoadingBinding


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 14:55 - 23/10/2022
 */
object DialogUtils {

    fun loadingDialog(
        context: Context
    ): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_loading)

        return dialog
    }
}