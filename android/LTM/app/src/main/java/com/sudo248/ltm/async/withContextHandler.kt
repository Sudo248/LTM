package com.sudo248.ltm.async

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


/**
 *  *
 * @author *Sudo248*
 * @since 09:26 - 28/08/2022
 */

suspend fun <T> withContextHandler(
    context: CoroutineContext,
    handleException: ((coroutineContext: CoroutineContext, throwable: Throwable) -> Unit)? =  null,
    block: suspend CoroutineScope.() -> T
): T {
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        handleException?.invoke(coroutineContext, throwable)
    }
    return withContext(context + coroutineExceptionHandler, block)
}