package com.sudo248.ltm.ui.activity.main.fragment.friend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.api.model.profile.Profile
import com.sudo248.ltm.common.PrefKey
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.data.repository.conversation.ConversationRepository
import com.sudo248.ltm.data.repository.profile.ProfileRepository
import com.sudo248.ltm.ktx.launchHandler
import com.sudo248.ltm.utils.SharedPreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 10:03 - 06/11/2022
 */

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val conversationRepo: ConversationRepository
) : ViewModel() {

    private val listProfiles: MutableList<Profile> = mutableListOf()

    private val _listProfile = MutableLiveData<List<Profile>>()
    val listProfile: LiveData<List<Profile>> = _listProfile

    private val _conversation = MutableLiveData<Conversation>()
    val conversation: LiveData<Conversation> = _conversation

    private val _addFriend = MutableLiveData<Int>()
    val addFriend: LiveData<Int> = _addFriend

    private val _createGroupState = MutableLiveData<Resource<Conversation>>()
    val createGroupState: LiveData<Resource<Conversation>> = _createGroupState

    private var jobSearchProfile: Job? = null

    init {
        getAllConversation()
    }

    fun getAllConversation() {
        viewModelScope.launchHandler {
            profileRepo.getAllProfile().collect {
                if (it is Resource.Success) {
                    listProfiles.clear()
                    listProfiles.addAll(it.requiredData())
                    _listProfile.postValue(listProfiles)
                }
            }
        }
    }

    fun getConversationByProfile(profile: Profile) {
        viewModelScope.launchHandler {
            val userId = SharedPreferenceUtils.getInt(PrefKey.KEY_USER_ID)
            val nameConversation = "$userId+${profile.userId}"
            conversationRepo.searchConversationByName(nameConversation).collect{
                if (it is Resource.Success) {
                    _conversation.postValue(it.requiredData()[0])
                }
            }
        }
    }

    fun addFriend(profile: Profile, position: Int) {
        viewModelScope.launchHandler {
            profileRepo.addFriend(profile).collect {
                if (it is Resource.Success) {
                    _addFriend.postValue(position)
                }
            }
        }
    }

    fun searchProfileByName(name: String) {
        jobSearchProfile?.cancel()
        jobSearchProfile = viewModelScope.launch {
            delay(1000)
            if (name.isEmpty()) {
                _listProfile.postValue(listProfiles)
            } else {
                val searchList = listProfiles.filter { it.name.contains(name, ignoreCase = true) }
                _listProfile.postValue(searchList)
            }
        }
    }

    fun createGroup(listProfile: List<Profile>) {
        viewModelScope.launchHandler {
            _createGroupState.postValue(Resource.Loading)
            val listUserId = listProfile.map { it.userId }
            profileRepo.createNewGroup(listUserId).collect {
                _createGroupState.postValue(it)
            }
        }
    }

}