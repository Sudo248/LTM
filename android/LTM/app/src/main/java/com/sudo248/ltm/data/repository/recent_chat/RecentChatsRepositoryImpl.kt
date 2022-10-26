package com.sudo248.ltm.data.repository.recent_chat

import com.sudo248.ltm.api.model.Request
import com.sudo248.ltm.api.model.RequestMethod
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.domain.model.RecentChat
import com.sudo248.ltm.websocket.WebSocketService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.sudo248.mqtt.model.MqttMessage
import javax.inject.Inject


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 20:23 - 23/10/2022
 */
class RecentChatsRepositoryImpl @Inject constructor(
    private val socketService: WebSocketService
) : RecentChatsRepository {

    override fun getAllRecentChat(): Flow<Resource<List<RecentChat>>> = flow {
        emit(Resource.Loading)
        val request = Request<String>()
        request.method = RequestMethod.GET
        request.path = Constant.PATH_CHATS
        request.params = mapOf(Constant.USER_ID to "${socketService.clientId}")

        socketService.responseFlow
            .filter { it.requestId == request.id }
            .collect {
//                val response = it as Response<>
            }

    }.flowOn(Dispatchers.IO)

    override fun getMessageFlow(): SharedFlow<MqttMessage> = socketService.messageFlow
}