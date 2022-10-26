package com.sudo248.ltm.data.repository.message

import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.domain.model.Message
import kotlinx.coroutines.flow.Flow


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 08:18 - 26/10/2022
 */
interface MessageRepository {
    suspend fun getAllMessage(isConversation: Int): Flow<Resource<List<Message>>>
    suspend fun newMessage(): Flow<Message>
}