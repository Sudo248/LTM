package com.sudo248.ltm.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 22:18 - 05/11/2022
 */
class ImageTarget(private val image: ImageView) : CustomTarget<Drawable>() {

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        image.layoutParams.width = resource.intrinsicWidth
        image.setImageDrawable(resource)
    }

    override fun onLoadCleared(placeholder: Drawable?) {

    }
}