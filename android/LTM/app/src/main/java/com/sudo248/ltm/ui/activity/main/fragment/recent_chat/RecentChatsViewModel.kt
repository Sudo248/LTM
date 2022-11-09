package com.sudo248.ltm.ui.activity.main.fragment.recent_chat

import android.util.Log
import androidx.lifecycle.*
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.data.repository.conversation.ConversationRepository
import com.sudo248.ltm.ktx.launchHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 19:55 - 23/10/2022
 */
@HiltViewModel
class RecentChatsViewModel @Inject constructor(
    private val conversationRepo: ConversationRepository
) : ViewModel() {

    private val listConversation: MutableList<Conversation> = mutableListOf()

    private val _conversations = MutableLiveData<List<Conversation>>()
    val conversations: LiveData<List<Conversation>> = _conversations

    private var jobSearchRecentChat: Job? = null
    var isNewConversation = false

    fun getAllConversation(isFresh: Boolean = true) = viewModelScope.launchHandler {
        conversationRepo.getAllConversation(isFresh).collect {
            Log.d("sudoo", "getAllConversation: $it")
            if (it is Resource.Success) {
                listConversation.clear()
                listConversation.addAll(it.requiredData())
                _conversations.postValue(listConversation)
                Log.d("sudoo", "getAllConversation: success ${it.data.size}")
            }
        }
    }

    fun searchConversationByName(name: String) {
        jobSearchRecentChat?.cancel()
        jobSearchRecentChat = viewModelScope.launchHandler {
            delay(500)
            if (name.isEmpty()) {
                _conversations.postValue(listConversation)
            } else {
                val searchList = listConversation.filter { it.name.contains(name, ignoreCase = true) }
                _conversations.postValue(searchList)
            }
        }
    }

    fun addNewConversation(conversation: Conversation) {
        isNewConversation = true
        listConversation.add(0, conversation)
        _conversations.postValue(listConversation)
    }
}