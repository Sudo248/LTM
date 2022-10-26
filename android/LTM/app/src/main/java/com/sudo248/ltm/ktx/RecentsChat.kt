package com.sudo248.ltm.ktx

import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.domain.model.RecentChat


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 22:22 - 25/10/2022
 */

fun RecentChat.fromConversation(conversation: Conversation): RecentChat = RecentChat(
        idChat = conversation.id,
        avtUrl = conversation.avtUrl,
        name = conversation.name,
        description, time
    )