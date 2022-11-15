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

    fun getAllProfile() {
        viewModelScope.launchHandler {
            val allProfile = profileRepo.getAllProfile()
            if (allProfile is Resource.Success) {
                listProfiles.clear()
                listProfiles.addAll(allProfile.requiredData())
                _listProfile.postValue(listProfiles)
            }
        }
    }

    fun getConversationByProfile(profile: Profile) {
        viewModelScope.launchHandler {
            val conversation = conversationRepo.getConversationByName(profile.name)
            if (conversation is Resource.Success) {
                _conversation.postValue(conversation.requiredData())
            }
        }
    }

    fun addFriend(profile: Profile, position: Int) {
        viewModelScope.launchHandler {
            val resAddFriend = profileRepo.addFriend(profile)
            if (resAddFriend is Resource.Success) {
//                _addFriend.postValue(position)
                val conversation = conversationRepo.getConversationById(resAddFriend.requiredData())
                if (conversation is Resource.Success) {
                    _addFriend.postValue(position)
                }
//                    conversationRepo.getAllConversation().collect()
            }
        }
    }

    fun searchProfileByName(name: String) {
        jobSearchProfile?.cancel()
        jobSearchProfile = viewModelScope.launchHandler {
            delay(500)
            if (name.isEmpty()) {
                _listProfile.postValue(listProfiles)
            } else {
                val searchList = listProfiles.filter { it.name.contains(name, ignoreCase = true) }
                _listProfile.postValue(searchList)
            }
        }
    }

    fun createGroup(nameGroup: String, listProfile: List<Profile>) {
        viewModelScope.launchHandler {
            if (listProfile.size <= 1) {
                _createGroupState.postValue(Resource.Error("Group must be more than 3 members"))
            }
            _createGroupState.postValue(Resource.Loading)
            val listUserId: MutableList<Int> = mutableListOf()
            listUserId.add(SharedPreferenceUtils.getInt(PrefKey.KEY_USER_ID))
            listUserId.addAll(listProfile.map { it.userId })
            _createGroupState.postValue(profileRepo.createNewGroup(nameGroup, listUserId))
        }
    }

}