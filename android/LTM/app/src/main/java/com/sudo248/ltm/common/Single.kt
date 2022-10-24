package com.sudo248.ltm.common


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 15:23 - 22/10/2022
 */
data class Single<out T>(private val data: T) {
    private var isHandled: Boolean = false

    fun data(): T? {
        val dataOrNull = if (isHandled) null
        else data
        isHandled = true
        return dataOrNull
    }

    fun requiredData(): T = data

    fun reset() {
        isHandled = false
    }
}