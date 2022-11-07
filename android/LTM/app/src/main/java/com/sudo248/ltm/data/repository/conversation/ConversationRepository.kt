package com.sudo248.ltm.data.repository.conversation

import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.common.Resource
import kotlinx.coroutines.flow.Flow


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 00:16 - 25/10/2022
 */
interface ConversationRepository {
    suspend fun getAllConversation(refresh: Boolean = false): Flow<Resource<MutableList<Conversation>>>
    suspend fun getConversationById(id: Int): Flow<Resource<Conversation>>
    suspend fun getUpdateConversations(): Flow<Pair<Int, Conversation?>>
    suspend fun searchConversationByName(name: String): Flow<Resource<MutableList<Conversation>>>
}