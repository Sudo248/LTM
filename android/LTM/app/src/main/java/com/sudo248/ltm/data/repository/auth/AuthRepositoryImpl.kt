package com.sudo248.ltm.data.repository.auth

import android.util.Log
import com.sudo248.ltm.api.model.Request
import com.sudo248.ltm.api.model.RequestMethod
import com.sudo248.ltm.api.model.Response
import com.sudo248.ltm.api.model.auth.Account
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.common.PrefKey
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.data.repository.profile.ProfileRepository
import com.sudo248.ltm.utils.SharedPreferenceUtils
import com.sudo248.ltm.websocket.WebSocketService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 09:42 - 23/10/2022
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val socketService: WebSocketService,
    private val profileRepo: ProfileRepository
) : AuthRepository {
    override suspend fun login(account: Account): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        val request = Request<Account>()
        request.path = Constant.PATH_LOGIN
        request.method = RequestMethod.POST
        request.payload = account
        socketService.send(request)

        Log.d("sudoo", "login: start send request")
        val response = socketService.responseFlow.first { it.requestId == request.id }
        if (response.code == 200) {
            val accountResponse = response.payload as Account
            Log.d("sudoo", "login: userId -> ${accountResponse.id}")
            saveUserId(accountResponse.id.toInt())
            socketService.clientId = accountResponse.id
            socketService.connectMqtt()
            saveUserImage(profileRepo.getProfileImage())
            emit(Resource.Success(true))
        } else {
            emit(Resource.Error(response.message))
        }
        Log.d("sudoo", "login: finish send request")
    }.flowOn(Dispatchers.IO)

    override suspend fun signup(account: Account): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        val request = Request<Account>()
        request.path = Constant.PATH_SIGN_UP
        request.method = RequestMethod.POST
        request.payload = account
        socketService.send(request)

        val response = socketService.responseFlow.first { it.requestId == request.id }
        if (response.code == 201) {
            val accountResponse = response.payload as Account
            saveUserId(accountResponse.id.toInt())
            socketService.clientId = accountResponse.id
//            socketService.connectMqtt()
            emit(Resource.Success(true))
        } else {
            emit(Resource.Error(response.message))
        }

    }.flowOn(Dispatchers.IO)

    override fun saveUserId(userId: Int) {
        SharedPreferenceUtils.putInt(PrefKey.KEY_USER_ID, userId)
        Log.d("sudoo", "saveUserId:$userId ${SharedPreferenceUtils.getInt(PrefKey.KEY_USER_ID)}")
    }

    override fun saveUserImage(userImage: String) {
        SharedPreferenceUtils.putString(PrefKey.KEY_USER_IMAGE, userImage)
        Log.d("sudoo", "saveUserImage:$userImage ${SharedPreferenceUtils.getString(PrefKey.KEY_USER_IMAGE)}")
    }
}