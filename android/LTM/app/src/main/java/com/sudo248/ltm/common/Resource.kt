package com.sudo248.ltm.common


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 15:18 - 22/10/2022
 */
sealed class Resource<out T> {
    data class Success<out S> (val data: S) : Resource<S>()
    data class Error(val message: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>()

    fun getDataOrNull(): T? {
        return if (this is Success) this.data
        else null
    }

    fun requiredData(): T {
        return if (this is Success) this.data
        else throw Exception("${this::class.java.name} is not a Success")
    }
}
