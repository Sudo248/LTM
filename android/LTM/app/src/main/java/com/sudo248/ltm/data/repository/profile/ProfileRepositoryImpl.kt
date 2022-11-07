package com.sudo248.ltm.data.repository.profile

import com.sudo248.ltm.api.model.Request
import com.sudo248.ltm.api.model.RequestMethod
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.api.model.profile.Profile
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.websocket.WebSocketService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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

    override suspend fun getAllProfile(): Flow<Resource<MutableList<Profile>>> = flow {
        emit(Resource.Success(getSampleProfile().toMutableList()))
        /*emit(Resource.Loading)
        val request = Request<String>()
        request.path = Constant.PATH_CONVERSATION
        request.method = RequestMethod.GET
        request.payload = ""
        socketService.send(request)

        val response = socketService.responseFlow.first { it.requestId == request.id }
        if (response.code == 200) {
            val profiles = response.payload as ArrayList<Profile>
            emit(Resource.Success(profiles))
        } else {
            emit(Resource.Error(response.message))
        }*/
    }

    override suspend fun searchProfileByName(name: String): Flow<Resource<MutableList<Profile>>> = flow<Resource<MutableList<Profile>>> {

    }.flowOn(Dispatchers.IO)

    override suspend fun createNewGroup(listProfileId: List<Int>): Flow<Resource<Conversation>> = flow {
        emit(Resource.Loading)
        val request = Request<ArrayList<Int>>();
        request.path = Constant.PATH_FRIEND
        request.method = RequestMethod.POST
        request.payload = ArrayList(listProfileId)
        socketService.send(request)

        val response = socketService.responseFlow.first { it.requestId == request.id }
        if (response.code == 200) {
            val conversation = response.payload as Conversation
            emit(Resource.Success(conversation))
        } else {
            emit(Resource.Error(response.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getConversationByProfile(profile: Profile): Flow<Resource<Conversation>> = flow<Resource<Conversation>> {
        emit(Resource.Loading)
        val request = Request<String>();
        request.path = Constant.PATH_FRIEND
        request.method = RequestMethod.POST

    }.flowOn(Dispatchers.IO)

    override suspend fun addFriend(profile: Profile): Flow<Resource<Int>> = flow {
        emit(Resource.Loading)
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
            val conversationId = response.payload as Int
            emit(Resource.Success(conversationId))
        } else {
            emit(Resource.Error(response.message))
        }
    }.flowOn(Dispatchers.IO)

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