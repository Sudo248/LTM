package com.sudo248.ltm.ui.activity.main.fragment.recent_chat

import android.util.Log
import androidx.lifecycle.*
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.data.repository.conversation.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    private val _conversations = MutableLiveData<MutableList<Conversation>>()
    val conversations: LiveData<MutableList<Conversation>> = _conversations

    private val _updateConversation = MutableLiveData<Pair<Int, Conversation>>()
    val updateConversation: LiveData<Pair<Int, Conversation>> = _updateConversation

    private val _newConversation = MutableLiveData<Pair<Int, Conversation>>()
    val newConversation: LiveData<Pair<Int, Conversation>> = _newConversation

    init {
        viewModelScope.launch {
            conversationRepo.getUpdateConversations().collect {
                when(it.first) {
                    Constant.NEW_SUBSCRIPTION -> {
                        _newConversation.postValue(Pair(it.first, it.second!!))
                    }
                    Constant.UNKNOWN -> {

                    }
                    else -> {
                        _updateConversation.postValue(Pair(it.first, it.second!!))
                    }
                }
            }
        }
    }
    fun getAllConversation(isFresh: Boolean = false) = viewModelScope.launch {
        conversationRepo.getAllConversation(isFresh).collect {
            Log.d("sudoo", "getAllConversation: $it")
            if (it is Resource.Success) {
                _conversations.postValue(it.requiredData())
                Log.d("sudoo", "getAllConversation: success ${it.data.size}")
            }
        }
    }

}