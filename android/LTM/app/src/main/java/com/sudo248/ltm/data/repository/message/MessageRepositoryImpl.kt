package com.sudo248.ltm.data.repository.message

import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.domain.model.Message
import com.sudo248.ltm.websocket.WebSocketService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 08:18 - 26/10/2022
 */
@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val socketService: WebSocketService
) : MessageRepository {
    override suspend fun getAllMessage(isConversation: Int): Flow<Resource<List<Message>>> = flow{
        emit(Resource.Loading)
        
    }

    override suspend fun newMessage(): Flow<Message> {
        TODO("Not yet implemented")
    }
}