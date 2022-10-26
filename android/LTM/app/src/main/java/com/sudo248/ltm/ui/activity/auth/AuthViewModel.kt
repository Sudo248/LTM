package com.sudo248.ltm.ui.activity.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sudo248.ltm.api.model.auth.Account
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.data.repository.auth.AuthRepository
import com.sudo248.ltm.ktx.launchHandler
import com.sudo248.ltm.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 09:41 - 23/10/2022
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val TAG = "AuthViewModel"
    var email: String = ""

    var password: String  = ""

    var confirmPassword: String = ""
        set(value) {
            field = value
            comparePassword()
        }

    private var _passwordsIsEqual: MutableLiveData<Boolean> = MutableLiveData(true)
    val passwordsIsEqual: LiveData<Boolean> = _passwordsIsEqual

    private var _result: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val result: LiveData<Resource<Boolean>> = _result

    private fun comparePassword() {
        _passwordsIsEqual.postValue(confirmPassword == password)
    }

    fun login() {
        Log.e(TAG, "signIn: ${password}, $email")
        if (email.isBlank() || password.isBlank()) {
            Log.e(TAG, "Not empty email or password")
            _result.postValue(Resource.Error("Not empty email or password"))
        } else {
            viewModelScope.launchHandler(
                handleException = { _, throwable ->
                    _result.postValue(Resource.Error("${throwable.message}"))
                }
            ) {
                authRepo.login(
                    Account(
                        email,
                        Utils.hash(password)
                    )
                ).collect(_result::postValue)
            }
        }
    }

    fun signUp() {
        Log.d(TAG, "signUp: $password $email")
        viewModelScope.launchHandler(
            handleException = { _, throwable ->
                _result.postValue(Resource.Error("${throwable.message}"))
            }
        ) {
            authRepo.signup(
                Account(
                   email,
                   Utils.hash(password)
                )
            ).collect(_result::postValue)
        }
    }
}