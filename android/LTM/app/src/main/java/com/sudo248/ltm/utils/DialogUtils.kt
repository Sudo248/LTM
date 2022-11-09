package com.sudo248.ltm.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.sudo248.ltm.R
import com.sudo248.ltm.databinding.DialogBinding
import com.sudo248.ltm.databinding.DialogConfirmBinding
import com.sudo248.ltm.databinding.DialogLoadingBinding


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 14:55 - 23/10/2022
 */
object DialogUtils {

    fun showDialog(
        context: Context,
        title: String,
        description: String,
        @ColorRes textColorTitle: Int? = null,
        confirm: String = context.getString(R.string.ok),
        @DrawableRes imageResource: Int? = null,
        onClickConfirm: (() -> Unit)? = null,
    ) {
        val dialog = Dialog(context)
        val binding = DialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        binding.apply {
            imageResource?.let {
                imgDialog.visible()
                imgDialog.setImageResource(imageResource)
            }

            txtTitle.text = title
            textColorTitle?.let { color ->
                txtTitle.setTextColor(context.getColor(color))
            }

            txtDescription.text = description

            txtConfirm.text = confirm
            txtConfirm.setOnClickListener {
                dialog.dismiss()
                onClickConfirm?.invoke()
            }
        }

        dialog.showWithTransparentBackground()
    }

    fun loadingDialog(
        context: Context
    ): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_loading)

        return dialog
    }

    fun showConfirmDialog(
        context: Context,
        title: String,
        description: String,
        @ColorRes textColorTitle: Int? = null,
        positive: String = context.getString(R.string.ok),
        negative: String = context.getString(R.string.cancel),
        @DrawableRes imageResource: Int? = null,
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null,
    ) {
        val dialog = Dialog(context)
        val binding = DialogConfirmBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        binding.apply {
            imageResource?.let {
                imgDialog.visible()
                imgDialog.setImageResource(imageResource)
            }

            txtTitle.text = title
            textColorTitle?.let { color ->
                txtTitle.setTextColor(context.getColor(color))
            }

            txtDescription.text = description

            txtPositive.text = positive
            txtPositive.setOnClickListener {
                dialog.dismiss()
                onPositive?.invoke()
            }

            txtNegative.text = negative
            txtNegative.setOnClickListener {
                dialog.dismiss()
                onNegative?.invoke()
            }
        }

        dialog.showWithTransparentBackground()
    }
}