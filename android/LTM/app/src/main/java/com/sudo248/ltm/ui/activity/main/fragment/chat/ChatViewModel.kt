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
import com.sudo248.ltm.common.PrefKey
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.data.repository.message.MessageRepository
import com.sudo248.ltm.domain.model.Message
import com.sudo248.ltm.ktx.launchHandler
import com.sudo248.ltm.utils.SharedPreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
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

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _newMessage = MutableLiveData<Message>()
    val newMessage: LiveData<Message> = _newMessage

    lateinit var conversation: Conversation

    private val conversationTopic: Int
        get() = conversation.id

    val userId: Int = messageRepository.getUserId()

    var imageUser: String = Constant.IMAGE_USER_DEFAULT

    init {
        viewModelScope.launch {
            imageUser = SharedPreferenceUtils.getString(PrefKey.KEY_USER_IMAGE, Constant.IMAGE_USER_DEFAULT)
        }
    }

    fun getAllMessage() {
        viewModelScope.launchHandler {
            messageRepository.getAllMessage(conversationTopic).collect {
                if (it is Resource.Success) {
                    _messages.postValue(it.requiredData())
                }
            }
        }
    }

    fun subscribeTopic() {
        viewModelScope.launchHandler {
            messageRepository.subscribeTopic(conversationTopic)
        }
    }

    fun updateNewMessage() = viewModelScope.launch {
        Log.d("sudoo", "updateNewMessage: $conversationTopic")
        messageRepository.newMessageInTopic(conversationTopic).collectLatest {
            Log.d("sudoo", "updateNewMessage: $it")
            _newMessage.postValue(it)
        }
    }

    fun sendMessage(messageContent: String) = viewModelScope.launchHandler {
        if (messageContent.isNotEmpty()) {
            val contentType =
                if (messageContent.matches(Regex(Constant.URL_IMAGE_REGEX))) ContentMessageType.IMAGE else ContentMessageType.MESSAGE
            val message = Message(
                topic = conversationTopic,
                content = messageContent,
                contentType = contentType,
                sendId = userId,
                avtUrl = imageUser
            )
            sendMessage(conversationTopic, message)
        }
    }

    private suspend fun sendMessage(topic: Int, message: Message) {
        messageRepository.sendMessage(topic, message)
//        _newMessage.postValue(message)
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
                    avtUrl = imageUser
                )
                sendMessage(conversationTopic, message)
            } else {

            }
        }
    }

}