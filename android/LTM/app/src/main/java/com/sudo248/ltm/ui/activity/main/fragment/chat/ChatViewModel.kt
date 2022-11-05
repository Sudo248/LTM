package com.sudo248.ltm.ui.activity.main.fragment.chat

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.api.model.message.ContentMessageType
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.data.repository.message.MessageRepository
import com.sudo248.ltm.domain.model.Message
import com.sudo248.ltm.ktx.launchHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 23:52 - 25/10/2022
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _newMessage = MutableLiveData<Message>()
    val newMessage: LiveData<Message> = _newMessage

    lateinit var conversation: Conversation
    val conversationTopic = Constant.TEST_CONVERSATION_ID
    val userId: Int

    init {
        viewModelScope.launchHandler {
            messageRepository.subscribeTopic(conversationTopic)
        }
        userId = messageRepository.getUserId()
    }

    fun updateNewMessage() = viewModelScope.launchHandler {
        messageRepository.newMessageInTopic(conversationTopic).collect {
            Log.d("sudoo", "updateNewMessage: $it")
            _newMessage.postValue(it)
        }
    }

    fun sendMessage(message: String) = viewModelScope.launchHandler {
        val contentType =
            if (message.matches(Regex(Constant.URL_IMAGE_REGEX))) ContentMessageType.IMAGE else ContentMessageType.MESSAGE
        val message = Message(
            topic = conversationTopic,
            content = message,
            contentType = contentType,
            sendId = userId,
            avtUrl = "https://i.pinimg.com/564x/8b/f5/68/8bf5689456b68cd8af836c943c3754f2.jpg"
        )
        sendMessage(conversationTopic, message)
    }

    private suspend fun sendMessage(topic: Int, message: Message) {
        messageRepository.sendMessage(topic, message)
        _newMessage.postValue(message)
    }

    fun sendImage(resolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            val responseImage = messageRepository.sendImage(resolver, uri)
            if (responseImage is Resource.Success) {
                val message = Message(
                    topic = conversationTopic,
                    content = responseImage.data,
                    contentType = ContentMessageType.IMAGE,
                    sendId = userId,
                    avtUrl = "https://i.pinimg.com/564x/8b/f5/68/8bf5689456b68cd8af836c943c3754f2.jpg"
                )
                sendMessage(conversationTopic, message)
            } else {

            }
        }
    }

}