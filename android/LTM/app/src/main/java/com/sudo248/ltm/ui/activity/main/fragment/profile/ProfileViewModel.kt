package com.sudo248.ltm.ui.activity.main.fragment.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sudo248.ltm.api.model.profile.Profile
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.data.repository.profile.ProfileRepository
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
    private val profileRepo: ProfileRepository
) : ViewModel() {

    val profile = MutableLiveData<Profile>()

    fun getProfile() {
        viewModelScope.launch {
            profileRepo.getProfile().collect {
                if (it is Resource.Success) {
                    profile.postValue(it.requiredData())
                }
            }
        }
    }

}