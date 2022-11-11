package com.sudo248.ltm.data.repository.conversation

import android.util.Log
import com.sudo248.ltm.api.model.Request
import com.sudo248.ltm.api.model.RequestMethod
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.api.model.conversation.ConversationType
import com.sudo248.ltm.api.model.message.Message
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.websocket.WebSocketService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.sudo248.mqtt.model.MqttMessageType
import java.time.LocalDateTime
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

    private val cacheConversations = mutableListOf<Conversation>()

    override suspend fun getAllConversation(refresh: Boolean): Resource<MutableList<Conversation>> = withContext(Dispatchers.IO){
            if (cacheConversations.isEmpty() || refresh) {
                cacheConversations.clear()
                val request = Request<Conversation>()
                request.path = Constant.PATH_USER_CONVERSATION
                request.method = RequestMethod.GET
                request.payload = Conversation()
                request.params = mapOf(Constant.USER_ID to "${socketService.clientId}")
                socketService.send(request)

                val response = socketService.responseFlow.first { it.requestId == request.id }
                if (response.code == 200) {
                    val conversationResponse = response.payload as ArrayList<Conversation>
                    cacheConversations.clear()
                    cacheConversations.addAll(conversationResponse)
                    cacheConversations.sortBy { it.createAt }
                    Resource.Success(conversationResponse)
                } else {
                    Resource.Error(response.message)
                }
            } else {
                Resource.Success(cacheConversations)
            }
        }

    override suspend fun getConversationById(id: Int): Resource<Conversation> = withContext(Dispatchers.IO) {

        val conversationLocal = cacheConversations.firstOrNull { it.id == id }

        if (conversationLocal != null) {
            Resource.Success(conversationLocal)
        } else {
            val request = Request<String>()
            request.path = Constant.PATH_CONVERSATION
            request.method = RequestMethod.GET
            request.params = mapOf(
                Constant.CONVERSATION_ID to "$id",
                Constant.USER_ID to "${socketService.clientId}"
            )
            request.payload = ""
            socketService.send(request)

            val response = socketService.responseFlow.first { it.requestId == request.id }
            if (response.code == 200) {
                val conversation = response.payload as Conversation
                Log.d("sudoo", "getConversationById: $conversation")
                cacheConversations.add(conversation)
                Resource.Success(conversation)
            } else {
                Resource.Error(response.message)
            }
        }
    }

    override suspend fun getUpdateConversations(): Flow<Pair<Int, Conversation?>> = flow {
        Log.d("sudoo", "getUpdateConversations:")
        socketService.messageFlow.collect { message ->
            val topic = message.topic.toInt()
            Log.d("sudoo", "getUpdateConversations: $topic")
            val index = cacheConversations.indexOfFirst { it.id == topic }
            if (index != -1) {
                val conversation = cacheConversations.removeAt(index)
                val payload = message.payload
                if (payload is Message) {
                    conversation.description = payload.content
                    conversation.createAt = payload.sendAt
                } else {
                    conversation.description = payload.toString()
                    conversation.createAt = LocalDateTime.now()
                }
                cacheConversations.add(0, conversation)
                emit(Pair(index, conversation))
            } else if (message.type == MqttMessageType.SUBSCRIBE) {
                val request = Request<Conversation>()
                request.path = Constant.PATH_CONVERSATION
                request.method = RequestMethod.GET
                request.params = mapOf(
                    Constant.CONVERSATION_ID to message.topic,
                    Constant.USER_ID to "${socketService.clientId}"
                )
                request.payload = Conversation()
                socketService.send(request)
                val response = socketService.responseFlow.first { it.requestId == request.id }
                if (response.code == 200 || response.code == 201) {
                    val conversation = response.payload as Conversation
                    Log.d("sudoo", "getUpdateConversations: $conversation")
                    cacheConversations.add(0, conversation)
                    emit(Pair(Constant.NEW_SUBSCRIPTION, conversation))
                } else {
                    emit(Pair(Constant.UNKNOWN, null))
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getConversationByName(
        name: String
    ): Resource<Conversation> = withContext(Dispatchers.IO) {
        val conversationLocal = cacheConversations.firstOrNull { it.name == name }
        Log.d("sudoo", "getConversationByName: $name")
        cacheConversations.forEach {
            Log.d("sudoo", "getConversationByName: ${it.name}")
        }
        Log.d("sudoo", "getConversationByName: $conversationLocal")
        if (conversationLocal == null) {
            Resource.Error("Not found")
        } else {
            Resource.Success(conversationLocal)
        }
    }

    override suspend fun searchConversationByName(
        nameInLocal: String,
        nameInServer: String
    ): Resource<MutableList<Conversation>> = withContext(Dispatchers.IO) {
        val conversationLocal = cacheConversations.filter { it.name.contains(nameInLocal) }

//        if (conversationLocal.isNotEmpty()) {
        Resource.Success(conversationLocal.toMutableList())
//        } else {
        /*val request = Request<String>()
        request.path = Constant.PATH_CONVERSATION
        request.method = RequestMethod.GET
        request.params = mapOf(Constant.CONVERSATION_NAME to nameInServer)
        request.payload = ""
        socketService.send(request)

        val response = socketService.responseFlow.first { it.requestId == request.id }
        if (response.code == 200) {
            val conversation = response.payload as ArrayList<Conversation>
            if (conversation.isNotEmpty()) {
                cacheConversations.addAll(conversation)
            }
            emit(Resource.Success(conversation))
        } else {
            emit(Resource.Error(response.message))
        }*/
//        }
    }

    private fun getSampleConversation(): List<Conversation> {
        return List(5) {
            Conversation(
                it,
                "Conversation $it",
                "",
                "Le Hong Duong",
                ConversationType.GROUP,
                LocalDateTime.now()
            )
        }
    }
}