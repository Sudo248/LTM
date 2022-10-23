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
    private var _email: MutableLiveData<String> = MutableLiveData()
    val email: LiveData<String> = _email

    private var _password: MutableLiveData<String> = MutableLiveData()
    val password: LiveData<String> = _password

    private var _confirmPassword: MutableLiveData<String> = MutableLiveData()
    val confirmPassword: LiveData<String> = _confirmPassword

    private var _passwordsIsEqual: MutableLiveData<Boolean> = MutableLiveData(true)
    val passwordsIsEqual: LiveData<Boolean> = _passwordsIsEqual

    private var _result: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val result: LiveData<Resource<Boolean>> = _result

    fun setEmail(email: String) {
//        Log.d(TAG, "setEmail: $email")
        _email.postValue(email)
    }

    fun setPassword(password: String) {
//        Log.d(TAG, "setPassword: $password")
        _password.postValue(password)
    }

    fun setConfirmPassword(confirmPassword: String) {
//        Log.e(TAG, "comparePassword: ${this.confirmPassword.value}, ${password.value}")
        _confirmPassword.postValue(confirmPassword)
    }

    fun comparePassword() {
        _passwordsIsEqual.postValue(confirmPassword.value == password.value)
    }

    fun login() {
        Log.e(TAG, "signIn:${password.value}, ${email.value}")
        if (email.value.isNullOrBlank() || password.value.isNullOrBlank()) {
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
                        email.value,
                        Utils.hash(password.value.toString())
                    )
                ).collect(_result::postValue)
            }
        }
    }

    fun signUp() {
        Log.d(TAG, "signUp: ${password.value} {${email.value}")
        viewModelScope.launchHandler(
            handleException = { _, throwable ->
                _result.postValue(Resource.Error("${throwable.message}"))
            }
        ) {
            authRepo.signup(
                Account(
                   email.value,
                   Utils.hash(password.value.toString())
                )
            ).collect(_result::postValue)
        }
    }
}