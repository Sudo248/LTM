package com.sudo248.ltm.ktx

/**
 * ## Created by
 * @author **Sudo248**
 * @since 00:00 - 15/08/2022
 */

val <T : Any> T.TAG: String
    get() = this::class.java.simpleName