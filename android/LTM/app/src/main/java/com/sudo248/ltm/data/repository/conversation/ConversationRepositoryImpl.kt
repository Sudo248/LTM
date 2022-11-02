package com.sudo248.ltm.data.repository.conversation

import com.sudo248.ltm.api.model.Request
import com.sudo248.ltm.api.model.RequestMethod
import com.sudo248.ltm.api.model.Response
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.api.model.conversation.ConversationType
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.domain.model.Message
import com.sudo248.ltm.websocket.WebSocketService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.sudo248.mqtt.model.MqttMessageType
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 00:25 - 25/10/2022
 */
@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val socketService: WebSocketService
) : ConversationRepository {

    private val conversations = mutableListOf<Conversation>()

    override suspend fun getAllConversation(refresh: Boolean): Flow<Resource<MutableList<Conversation>>> = flow {
        emit(Resource.Loading)
        if (conversations.isEmpty() || refresh) {
            conversations.clear()
            conversations.addAll(getSampleConversation())
            emit(Resource.Success(conversations))

            val request = Request<String>()
            request.path = Constant.PATH_CONVERSATION
            request.method = RequestMethod.GET
            request.payload = ""
            socketService.send(request)

            val response = socketService.responseFlow.first { it.requestId == request.id }
            if (response.code == 200) {
                val conversationResponse = response.payload as ArrayList<Conversation>
                conversations.clear()
                conversations.addAll(conversationResponse)
                emit(Resource.Success(conversationResponse))
            } else {
                emit(Resource.Error(response.message))
            }
        } else {
            emit(Resource.Success(conversations))
        }
        
    }.flowOn(Dispatchers.IO)

    override suspend fun getConversationById(id: Int): Flow<Resource<Conversation>> = flow {
        emit(Resource.Loading)
        val request = Request<String>()
        request.path = Constant.PATH_CONVERSATION
        request.method = RequestMethod.GET
        request.params = mapOf(Constant.CONVERSATION_ID to "$id")
        request.payload = ""
        socketService.send(request)

        val response = socketService.responseFlow.first { it.requestId == request.id }
        if (response.code == 200) {
            emit(Resource.Success(response.payload as Conversation))
        } else {
            emit(Resource.Error(response.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getUpdateConversations(): Flow<Pair<Int, Conversation?>> = flow {
        delay(5000)
        val conversation = conversations.removeAt(2)
        conversation.description = "Update message now"
        conversations.add(0, conversation)
        emit(Pair(2, conversation))

        /*socketService.messageFlow.collect { message ->
            val topic = message.topic.toInt()
            val index = conversations.indexOfFirst { it.id == topic }
            if (index != -1) {
                val conversation = conversations.removeAt(index)
                conversation.description = message.payload.toString()
                conversations.add(0, conversation)
                emit(Pair(index, conversation))
            } else if (message.type == MqttMessageType.SUBSCRIBE) {
                val request = Request<String>()
                request.path = Constant.PATH_CONVERSATION
                request.method = RequestMethod.GET
                request.params = mapOf(Constant.CONVERSATION_ID to message.topic)
                request.payload = ""
                socketService.send(request)
                val response = socketService.responseFlow.first { it.requestId == request.id }
                if (response.code == 200) {
                    val conversation = response.payload as Conversation
                    conversations.add(0, conversation)
                    emit(Pair(Constant.NEW_SUBSCRIPTION, conversation))
                } else {
                    emit(Pair(Constant.UNKNOWN, null))
                }
            }
        }*/
    }.flowOn(Dispatchers.IO)

    private fun getSampleConversation(): List<Conversation> {
        return List(5) {
            Conversation(
                it,
                "Conversation $it",
                "",
                "Le Hongg Duong",
                ConversationType.GROUP,
                LocalDate.now()
            )
        }
    }
}