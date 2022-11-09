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

    suspend fun getProfile(): Flow<Resource<Profile>>

    suspend fun getAllProfile(): Flow<Resource<MutableList<Profile>>>

    suspend fun searchProfileByName(name: String): Flow<Resource<MutableList<Profile>>>

    suspend fun createNewGroup(listProfileId: List<Int>): Flow<Resource<Conversation>>

    suspend fun getConversationByProfile(profile: Profile): Flow<Resource<Conversation>>

    suspend fun addFriend(profile: Profile): Flow<Resource<Int>>

    suspend fun getProfileImage(): String
}