package com.sudo248.ltm.async


/**
 *  ## Created by
 * @author *Sudo248*
 * @since 23:01 - 21/08/2022
 */

import com.sudo248.ltm.ktx.asyncHandler
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * create a new [CoroutineScope]
 * start it with async
 *
 * @param context [CoroutineContext] to create [CoroutineScope]
 * @param handleException handler when throw exception in [block]
 * @param block that run in [CoroutineScope]
 * @return [Deferred]
 */

fun <T> asyncHandler(
    context: CoroutineContext,
    handleException: ((coroutineContext: CoroutineContext, throwable: Throwable) -> Unit)? =  null,
    block: suspend CoroutineScope.() -> T
): Deferred<T> {
    val newScope = CoroutineScope(context + SupervisorJob())
    return newScope.asyncHandler(
        handleException = handleException,
        block = block
    )
}

