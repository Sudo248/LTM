package com.sudo248.ltm.data.repository.profile

import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.api.model.profile.Profile
import com.sudo248.ltm.common.Resource
import kotlinx.coroutines.flow.Flow


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 15:21 - 06/11/2022
 */
interface ProfileRepository {

    suspend fun getProfile(): Resource<Profile>

    suspend fun getAllProfile(): Resource<MutableList<Profile>>

    suspend fun searchProfileByName(name: String): Resource<MutableList<Profile>>

    suspend fun createNewGroup(nameGroup: String, listProfileId: List<Int>): Resource<Conversation>

    suspend fun getConversationByProfile(profile: Profile): Resource<Conversation>

    suspend fun addFriend(profile: Profile): Resource<Int>

    suspend fun getProfileImage(): String

    suspend fun updateProfile(profile: Profile): Boolean

    suspend fun getNameUserById(userId: Int): String
}