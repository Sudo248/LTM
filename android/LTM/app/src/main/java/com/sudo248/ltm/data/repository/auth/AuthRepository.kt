package com.sudo248.ltm.data.repository.auth

import com.sudo248.ltm.api.model.auth.Account
import com.sudo248.ltm.common.Resource
import kotlinx.coroutines.flow.Flow


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 09:42 - 23/10/2022
 */
interface AuthRepository {

    suspend fun login(account: Account): Flow<Resource<Boolean>>

    suspend fun signup(account: Account): Flow<Resource<Boolean>>

    suspend fun saveId(userId: Long)
}