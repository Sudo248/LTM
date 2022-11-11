package com.sudo248.ltm.data.repository.profile

import android.content.res.Resources
import android.util.Log
import com.sudo248.ltm.api.model.Request
import com.sudo248.ltm.api.model.RequestMethod
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.api.model.profile.Profile
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.websocket.WebSocketService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 15:31 - 06/11/2022
 */
@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val socketService: WebSocketService
) : ProfileRepository {

    private val cacheNameUser: HashMap<Int, String> = HashMap()

    override suspend fun getProfile(): Resource<Profile> = withContext(Dispatchers.IO) {
        val request = Request<Profile>()
        request.path = Constant.PATH_PROFILE
        request.method = RequestMethod.GET
        request.params = mapOf(Constant.USER_ID to "${socketService.clientId}")
        request.payload = Profile()
        socketService.send(request)

        val response = socketService.responseFlow.first { it.requestId == request.id }
        if (response.code == 200) {
            val profiles = response.payload as Profile
            Resource.Success(profiles)
        } else {
            Resource.Error(response.message)
        }
    }

    override suspend fun getAllProfile(): Resource<MutableList<Profile>> = withContext(Dispatchers.IO) {
        //emit(Resource.Success(getSampleProfile().toMutableList()))
        val request = Request<Profile>()
        request.path = Constant.PATH_GET_PROFILE
        request.method = RequestMethod.GET
        request.params = mapOf(Constant.USER_ID to "${socketService.clientId}")
        request.payload = Profile()
        socketService.send(request)

        val response = socketService.responseFlow.first { it.requestId == request.id }
        if (response.code == 200) {
            val profiles = response.payload as ArrayList<Profile>
            profiles.forEach {
                cacheNameUser[it.userId] = it.name
            }
            Resource.Success(profiles)
        } else {
            Resource.Error(response.message)
        }
    }

    override suspend fun searchProfileByName(name: String): Resource<MutableList<Profile>> = withContext(Dispatchers.IO) {
        Resource.Loading
    }

    override suspend fun createNewGroup(nameGroup: String, listProfileId: List<Int>): Resource<Conversation> = withContext(Dispatchers.IO) {
        val request = Request<ArrayList<Int>>();
        request.path = Constant.PATH_CREATE_GROUP
        request.method = RequestMethod.POST
        request.params = mapOf(
            Constant.NAME_GROUP to nameGroup
        )
        request.payload = ArrayList(listProfileId)
        socketService.send(request)

        val response = socketService.responseFlow.first { it.requestId == request.id }
        if (response.code == 201) {
            val conversation = response.payload as Conversation
            Resource.Success(conversation)
        } else {
            Resource.Error(response.message)
        }
    }

    override suspend fun getConversationByProfile(profile: Profile): Resource<Conversation> = withContext(Dispatchers.IO) {
        val request = Request<String>();
        request.path = Constant.PATH_FRIEND
        request.method = RequestMethod.POST
        Resource.Loading
    }

    override suspend fun addFriend(profile: Profile): Resource<Int> = withContext(Dispatchers.IO) {
        val request = Request<String>();
        request.path = Constant.PATH_FRIEND
        request.method = RequestMethod.POST
        request.params = mapOf(
            Constant.USER_ID to "${socketService.clientId}",
            Constant.FRIEND_ID to "${profile.userId}"
        )
        request.payload = ""
        socketService.send(request)

        val response = socketService.responseFlow.first { it.requestId == request.id }
        if (response.code == 200) {
            val conversation = response.payload as Conversation
            Resource.Success(conversation.id)
        } else {
            Resource.Error(response.message)
        }
    }

    override suspend fun getProfileImage(): String = withContext(Dispatchers.IO) {
        var imageUser = Constant.IMAGE_USER_DEFAULT
        val profile = getProfile()
        if (profile is Resource.Success) {
            imageUser = profile.requiredData().image
        }
        Log.d("sudoo", "getProfileImage: $imageUser")
        imageUser
    }

    override suspend fun updateProfile(profile: Profile): Boolean = withContext(Dispatchers.IO) {
        val request = Request<Profile>();
        request.path = Constant.PATH_PROFILE
        request.method = RequestMethod.PUT
        request.payload = profile
        socketService.send(request)

        val response = socketService.responseFlow.first { it.requestId == request.id }
        response.code == 200
    }

    override suspend fun getNameUserById(userId: Int): String = withContext(Dispatchers.IO){
        val nameUser = cacheNameUser[userId]
        if (nameUser == null) {
            getAllProfile()
            cacheNameUser[userId] ?: "Unknown"
        } else {
            nameUser
        }
    }

    private fun getSampleProfile(): List<Profile> {
        return List(5) {
            Profile(
                it,
                "Bio $it",
                "Profile of $it",
                "",
                true,
                0,
                it % 2 == 0
            )
        }
    }
}