package com.sudo248.ltm.ui.activity.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.data.repository.conversation.ConversationRepository
import com.sudo248.ltm.ktx.launchHandler
import com.sudo248.ltm.websocket.WebSocketService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 17:35 - 23/10/2022
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val conversationRepo: ConversationRepository,
    private val socketService: WebSocketService
) : ViewModel() {

    private val _newConversation = MutableLiveData<Pair<Int, Conversation>>()
    val newConversation: LiveData<Pair<Int, Conversation>> = _newConversation

    private val _updateConversation = MutableLiveData<Pair<Int, Conversation>>()
    val updateConversation: LiveData<Pair<Int, Conversation>> = _updateConversation

    fun updateConversation() {
        viewModelScope.launchHandler {
            conversationRepo.getUpdateConversations().collect {
                Log.d("sudoo", "updateConversation: MainViewModel")
                when (it.first) {
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

    fun checkConnect() {
        if (!socketService.isOpen) {
            socketService.connect()
        }
    }

}