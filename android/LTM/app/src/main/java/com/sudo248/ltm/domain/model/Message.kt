package com.sudo248.ltm.domain.model

import com.sudo248.ltm.api.model.message.ContentMessageType
import java.time.LocalDate


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 07:19 - 26/10/2022
 */
data class Message(
    val id: Int,
    val topic: Int,
    val content: String,
    val contentType: ContentMessageType,
    val sendId: Int,
    val avtUrl: String? = null,
    val sendAt: LocalDate,
)