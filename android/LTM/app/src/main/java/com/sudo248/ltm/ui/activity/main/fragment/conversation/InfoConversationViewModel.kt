package com.sudo248.ltm.ui.activity.main.fragment.conversation

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.api.model.profile.Profile
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.data.repository.conversation.ConversationRepository
import com.sudo248.ltm.data.repository.message.MessageRepository
import com.sudo248.ltm.ktx.launchHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 21:57 - 15/11/2022
 */
@HiltViewModel
class InfoConversationViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {
    lateinit var conversation: Conversation
    val members = MutableLiveData<List<Profile>>()
    val removeConversation = MutableLiveData<Boolean>()
    val image = MutableLiveData<String>()
    val nameConversation = MutableLiveData<String>()

    fun sendImage(resolver: ContentResolver, uri: Uri) {
        viewModelScope.launchHandler {
            val responseImage = messageRepository.sendImage(resolver, uri)
            if (responseImage is Resource.Success) {
                //image.postValue(responseImage.requiredData())
                conversation.avtUrl = responseImage.requiredData()
                updateConversation()
            } else {

            }
        }
    }

    fun updateNameConversation(name: String) {
        conversation.name = name
        updateConversation()
    }

    private fun updateConversation() {
        viewModelScope.launchHandler {
            val response = conversationRepository.updateConversation(conversation)
            if (response is Resource.Success) {
                image.postValue(conversation.avtUrl)
                nameConversation.postValue(conversation.name)
            }
        }
    }

    fun removeConversation() {
        viewModelScope.launchHandler {
            val response = conversationRepository.removeFromConversation(conversation.id)
            if (response is Resource.Success) {
                removeConversation.postValue(true)
            }
        }
    }

    fun getMemberConversation(conversationId: Int) {
        viewModelScope.launchHandler {
            val response = conversationRepository.getProfileOfConversation(conversationId)
            if (response is Resource.Success) {
                members.postValue(response.requiredData())
            }
        }
    }
}