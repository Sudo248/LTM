package com.sudo248.ltm.ktx

import android.util.Log
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration


/**
 *  ## Created by
 * @author *Sudo248*
 * @since 23:21 - 21/08/2022
 */

fun <T> CoroutineScope.asyncHandler(
    context: CoroutineContext = EmptyCoroutineContext,
    handleException: ((coroutineContext: CoroutineContext, throwable: Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> T
): Deferred<T> {
    var result: Deferred<T>? = null
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        if (result?.isActive != false) result?.cancel()
        handleException?.invoke(coroutineContext, throwable)
        Log.e(TAG, "launchHandler: ", throwable)
    }
    result = async(context + coroutineExceptionHandler, block = block)
    return result
}

/**
 * When throw an exception this coroutine must be cancel
 *
 */

fun CoroutineScope.launchHandler(
    context: CoroutineContext = EmptyCoroutineContext,
    handleException: ((coroutineContext: CoroutineContext, throwable: Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
): Job {
    var result: Job? = null
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        if (result?.isActive != false) result?.cancel()
        handleException?.invoke(coroutineContext, throwable)
        Log.e(TAG, "launchHandler: ", throwable)
    }
    result = launch(context + coroutineExceptionHandler, block = block)
    return result
}

fun <T> CoroutineScope.debounceTime(
    data: T,
    time: Duration,
    action: (T) -> Unit
) {

}


