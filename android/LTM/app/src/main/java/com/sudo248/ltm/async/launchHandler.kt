package com.sudo248.ltm.async

import com.sudo248.ltm.ktx.launchHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext


/**
 *  ## Createdby
 * @author *Sudo248*
 * @since 23:31 - 21/08/2022
 */

/**
 * create a new [CoroutineScope]
 * start it with async
 *
 * @param context [CoroutineContext] to create [CoroutineScope]
 * @param handleException handler when throw exception in [block]
 * @param block that run in [CoroutineScope]
 * @return [Deferred]
 */

fun launchHandler(
    context: CoroutineContext,
    handleException: ((coroutineContext: CoroutineContext, throwable: Throwable) -> Unit)? =  null,
    block: suspend CoroutineScope.() -> Unit
): Job {
    val newScope = CoroutineScope(context + SupervisorJob())
    return newScope.launchHandler(
        handleException = handleException,
        block = block
    )
}