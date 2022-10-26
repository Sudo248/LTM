package com.sudo248.ltm.ui.activity.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sudo248.ltm.R
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.databinding.FragmentSignUpBinding
import com.sudo248.ltm.ui.activity.auth.AuthActivity
import com.sudo248.ltm.ui.activity.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater)
        setupUi()
        observer()
        return binding.root
    }

    private fun setupUi() {
        binding.apply {
            tilConfirmPassword.error = null
            edtEmail.doOnTextChanged { text, _, _, _ ->
                if (!text.isNullOrBlank()){
                    viewModel.email = text.toString()
                }
                tilConfirmPassword.error = null
            }

            edtPassword.doOnTextChanged { text, _, _, _ ->
                if (!text.isNullOrBlank()){
                    viewModel.password = text.toString()
                }
                tilConfirmPassword.error = null
            }

            edtConfirmPassword.doOnTextChanged { text, _, _, _ ->
                if (!text.isNullOrBlank()){
                    viewModel.confirmPassword = text.toString()
                }
                tilConfirmPassword.error = null
            }
        }
    }

    private fun observer() {

        viewModel.passwordsIsEqual.observe(viewLifecycleOwner) {
            if (!it) {
                binding.tilConfirmPassword.error = getString(R.string.passwords_mismatch)
            } else {
                binding.tilConfirmPassword.error = null
            }
        }

        viewModel.result.observe(viewLifecycleOwner) {
            if (it is Resource.Error){
                if(!binding.tilEmail.editText?.text.isNullOrBlank()
                    || !binding.tilPassword.editText?.text.isNullOrBlank()
                    || !binding.tilConfirmPassword.editText?.text.isNullOrBlank()
                ){
                    binding.tilConfirmPassword.error = it.message
                }
            } else if (it is Resource.Success){
                (activity as AuthActivity).navigate(0)
            }
        }
    }
}