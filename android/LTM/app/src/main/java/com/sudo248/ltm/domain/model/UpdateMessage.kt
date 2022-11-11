package com.sudo248.ltm.domain.model

import com.sudo248.ltm.api.model.conversation.Conversation


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 08:14 - 09/11/2022
 */
data class UpdateMessage(
    val oldIndex: Int,
    val newIndex: Int,
    val conversation: Conversation?
)