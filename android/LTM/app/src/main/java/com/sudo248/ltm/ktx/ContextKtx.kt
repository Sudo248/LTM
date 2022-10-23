package com.sudo248.ltm.ktx

import android.content.Context
import android.graphics.drawable.Drawable

/**
 * ## Created by
 * @author **Sudo248**
 * @since 00:00 - 15/08/2022
 */

val Context.applicationName: String
    get() = applicationInfo.loadLabel(packageManager).toString()

val Context.applicationIcon: Drawable
    get() = applicationInfo.loadIcon(packageManager)

val Context.applicationDescription: String
    get() = applicationInfo.loadDescription(packageManager).toString()
