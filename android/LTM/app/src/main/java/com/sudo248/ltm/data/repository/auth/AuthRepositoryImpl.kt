package com.sudo248.ltm.data.repository.auth

import android.util.Log
import com.sudo248.ltm.api.model.Request
import com.sudo248.ltm.api.model.RequestMethod
import com.sudo248.ltm.api.model.Response
import com.sudo248.ltm.api.model.auth.Account
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.common.PrefKey
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.ktx.launchHandler
import com.sudo248.ltm.utils.SharedPreferenceUtils
import com.sudo248.ltm.websocket.WebSocketService
import kotlinx.coroutines.Dispatchers
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
    private val socketService: WebSocketService
) : AuthRepository {
    override suspend fun login(account: Account): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        val request = Request<Account>()
        request.path = Constant.PATH_LOGIN
        request.method = RequestMethod.POST
        request.payload = account
        socketService.send(request)
        socketService.responseFlow
            .filter { it.requestId == request.id }
            .collect {
                val response = it as Response<Account>
                if (response.code == 200) {
                    Log.d("sudoo", "login: ${request.payload.id}")
                    saveId(response.payload.id)
                    socketService.clientId = response.payload.id
                    emit(Resource.Success(true))
                } else {
                    emit(Resource.Error(response.message))
                }
            }
    }.flowOn(Dispatchers.IO)

    override suspend fun signup(account: Account): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        val request = Request<Account>()
        request.path = Constant.PATH_SIGN_UP
        request.method = RequestMethod.POST
        request.payload = account
        socketService.send(request)
        socketService.responseFlow
            .filter { it.requestId == request.id }
            .collect {
            val response = it as Response<Account>
            if (response.code == 201) {
                saveId(response.payload.id)
                socketService.clientId = response.payload.id
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error(response.message))
            }
        }

    }.flowOn(Dispatchers.IO)

    override suspend fun saveId(userId: Long) {
        SharedPreferenceUtils.putLongAsync(PrefKey.KEY_USER_ID, userId)
    }
}