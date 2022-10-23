package com.sudo248.ltm

import android.app.Application
import com.sudo248.ltm.utils.SharedPreferenceUtils
import dagger.hilt.android.HiltAndroidApp


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 10:33 - 22/10/2022
 */

@HiltAndroidApp
class LTMApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferenceUtils.createApplicationSharePreference(this)
    }
}