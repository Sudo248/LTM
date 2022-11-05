package com.sudo248.ltm.ui.activity.main.fragment.recent_chat

import android.util.Log
import androidx.lifecycle.*
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.data.repository.conversation.ConversationRepository
import com.sudo248.ltm.ktx.launchHandler
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


    fun getAllConversation(isFresh: Boolean = false) = viewModelScope.launchHandler {
        conversationRepo.getAllConversation(isFresh).collect {
            Log.d("sudoo", "getAllConversation: $it")
            if (it is Resource.Success) {
                _conversations.postValue(it.requiredData())
                Log.d("sudoo", "getAllConversation: success ${it.data.size}")
            }
        }
    }

}