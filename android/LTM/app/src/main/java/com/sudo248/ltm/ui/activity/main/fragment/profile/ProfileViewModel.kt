package com.sudo248.ltm.ui.activity.main.fragment.profile

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sudo248.ltm.api.model.message.ContentMessageType
import com.sudo248.ltm.api.model.profile.Profile
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.data.repository.message.MessageRepository
import com.sudo248.ltm.data.repository.profile.ProfileRepository
import com.sudo248.ltm.domain.model.Message
import com.sudo248.ltm.ktx.launchHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 10:04 - 06/11/2022
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    val profile = MutableLiveData<Profile>()
    val image = MutableLiveData<String>()

    fun getProfile() {
        viewModelScope.launchHandler {
            val resProfile = profileRepo.getProfile()
            if (resProfile is Resource.Success) {
                val data = resProfile.requiredData()
                profile.postValue(data)
                image.postValue(data.image)
            }
        }
    }

    fun sendImage(resolver: ContentResolver, uri: Uri) {
        viewModelScope.launchHandler {
            val responseImage = messageRepository.sendImage(resolver, uri)
            if (responseImage is Resource.Success) {
                image.postValue(responseImage.requiredData())
            } else {

            }
        }
    }

    fun updateProfile(name: String, bio: String) {
        viewModelScope.launchHandler {
            val updateProfile = profile.value!!
            updateProfile.image = image.value
            updateProfile.name = name
            updateProfile.bio = bio
            profileRepo.updateProfile(profile = updateProfile)
        }
    }
}