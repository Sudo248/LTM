package com.sudo248.ltm.ui.activity.auth

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.sudo248.ltm.R
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.databinding.ActivityAuthBinding
import com.sudo248.ltm.ktx.gone
import com.sudo248.ltm.ktx.visible
import com.sudo248.ltm.ui.activity.main.MainActivity
import com.sudo248.ltm.ui.activity.auth.fragment.LoginFragment
import com.sudo248.ltm.ui.activity.auth.fragment.SignUpFragment
import com.sudo248.ltm.utils.DialogUtils
import com.sudo248.ltm.utils.KeyboardUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()
    private var isSignIn = true
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickListener()
        observer()
    }

    private fun setupClickListener() {
        loadingDialog = DialogUtils.loadingDialog(this)
        binding.apply {
            txtToSignUp.setOnClickListener {
                KeyboardUtils.hide(this@AuthActivity)
                navigate(1)
            }

            btnBack.setOnClickListener {
                KeyboardUtils.hide(this@AuthActivity)
                onBackPressed()
            }

            fabSignIn.setOnClickListener {
                KeyboardUtils.hide(this@AuthActivity)
                if (isSignIn) {
                    viewModel.login()
                } else {
                    viewModel.signUp()
                }
            }
        }
    }

    private fun observer() {
        viewModel.result.observe(this) {
            Log.d("sudoo", "observer: $it")
            when(it) {
                is Resource.Loading -> {
                    loadingDialog.show()
                    binding.root.isFocusable = false
                }
                is Resource.Error ->{
                    if(loadingDialog.isShowing){
                        loadingDialog.dismiss()
                        binding.root.isFocusable = true
                    }
                }
                else ->{
//                    navigate to Home
                    if(loadingDialog.isShowing)
                        loadingDialog.dismiss()
                    if (isSignIn){
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        //navigate back to SignIn
                        viewModel.passwordsIsEqual.value?.let{ equal ->
                            if (equal){
                                navigate(0)
                                viewLogin()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun viewLogin() {
        binding.apply {
            lnNewUser.visible()
            btnBack.gone()
            txtTitle.text = getString(R.string.sign_in)
            isSignIn = true
        }
    }

    private fun viewSignUp() {
        binding.apply {
            lnNewUser.gone()
            btnBack.visible()
            txtTitle.text = getString(R.string.sign_up)
            isSignIn = false
        }
    }


    fun navigate(position: Int = 0) {
        when(position) {
            0 -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fcv, LoginFragment())
                    .commit()
                viewLogin()
            }
            1 -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fcv, SignUpFragment())
                    .commit()
                viewSignUp()
            }
            else -> Unit
        }
    }

    override fun onBackPressed() {
        if(!isSignIn) {
            navigate(0)
        } else {
            super.onBackPressed()
        }
    }
}