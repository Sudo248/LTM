package com.sudo248.ltm.data.repository.message

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.sudo248.ltm.api.model.Request
import com.sudo248.ltm.api.model.RequestMethod
import com.sudo248.ltm.api.model.image.Image
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.common.PrefKey
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.domain.model.Message
import com.sudo248.ltm.utils.SharedPreferenceUtils
import com.sudo248.ltm.websocket.WebSocketService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.sudo248.mqtt.model.MqttMessageType
import java.io.IOException
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
    private val socketService: WebSocketService,
) : MessageRepository {

    override fun getUserId(): Int {
        return SharedPreferenceUtils.getInt(PrefKey.KEY_USER_ID)
    }

    override suspend fun getAllMessage(idConversation: Int): Flow<Resource<List<Message>>> = flow {
        emit(Resource.Loading)
        val request = Request<String>()
        request.path = Constant.PATH_MESSAGE
        request.method = RequestMethod.GET
        request.params = mapOf(Constant.CONVERSATION_ID to "$idConversation")
        request.payload = ""
        socketService.send(request)
        val response = socketService.responseFlow.first { it.requestId == request.id }
        if (response.code == 200) {
            val messages =
                (response.payload as ArrayList<com.sudo248.ltm.api.model.message.Message>).map { apiMessage ->
                    Message(
                        id = apiMessage.id,
                        topic = idConversation,
                        content = apiMessage.content,
                        contentType = apiMessage.contentType,
                        sendId = apiMessage.sendId,
                        avtUrl = apiMessage.avtUrl,
                        sendAt = apiMessage.sendAt
                    )
                }
            emit(Resource.Success(messages))
        } else {
            emit(Resource.Error(response.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun newMessageInTopic(topic: Int): Flow<Message> = flow {
        socketService.messageFlow
            .filter {
                Log.d("sudoo", "newMessageInTopic: ${it.topic} ${it.topic == "$topic"} $topic ")
                it.topic == "$topic"
            }.collect { mqttMessage ->
                if (mqttMessage.type == MqttMessageType.PUBLISH) {
                    Log.d("sudoo", "newMessageInTopic: ${mqttMessage.payload}")
                    val apiMessage = mqttMessage.payload as com.sudo248.ltm.api.model.message.Message
                    emit(
                        Message(
                            id = apiMessage.id ?: -1,
                            topic = topic,
                            content = apiMessage.content,
                            contentType = apiMessage.contentType,
                            sendId = apiMessage.sendId,
                            avtUrl = apiMessage.avtUrl,
                            sendAt = apiMessage.sendAt
                        )
                    )
                }
            }
    }.flowOn(Dispatchers.IO)

    override suspend fun sendMessage(topic: Int, message: Message): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val apiMessage = com.sudo248.ltm.api.model.message.Message(
                    message.content,
                    message.contentType,
                    message.sendId,
                    message.avtUrl,
                    topic
                )
                socketService.publish("$topic", apiMessage)
            } catch (e: Exception) {
                false
            }
            true
        }

    override suspend fun subscribeTopic(topic: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            socketService.subscribe("$topic")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @SuppressLint("Recycle")
    override suspend fun sendImage(resolver: ContentResolver, uri: Uri): Resource<String> =
        withContext(Dispatchers.IO) {
            try {
                var nameImage = "Unknow.png"
                resolver.query(uri, null, null, null)?.let { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    cursor.moveToFirst()
                    nameImage = cursor.getString(nameIndex)
                    cursor.close()
                }

                resolver.openInputStream(uri)?.let { inputStream ->
                    val imageBytes = inputStream.readBytes()
                    inputStream.close()
                    Log.d("sudoo", "sendImage: image size: ${imageBytes.size}")
                    val image = Image(
                        nameImage,
                        imageBytes.size,
                        imageBytes
                    )
                    val request = Request<Image>()
                    request.path = Constant.PATH_UPLOAD_IMAGE
                    request.method = RequestMethod.POST
                    request.payload = image
                    socketService.send(request)
                    val response = socketService.responseFlow.first { it.requestId == request.id }
                    if (response.code == 200) {
                        val url = response.payload as String
                        Resource.Success(url)
                    } else {
                        Resource.Error(response.message)
                    }
                } ?: Resource.Error("Null when open input stream $uri")
            } catch (e: IOException) {
                e.printStackTrace()
                Resource.Error(e.message.toString())
            }
        }
}