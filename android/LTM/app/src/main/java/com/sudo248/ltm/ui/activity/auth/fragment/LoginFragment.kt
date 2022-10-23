package com.sudo248.ltm.ui.activity.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.databinding.ActivityAuthBinding
import com.sudo248.ltm.databinding.FragmentLoginBinding
import com.sudo248.ltm.ui.activity.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        setupUi()
        observer()
        return binding.root
    }

    private fun setupUi() {
        binding.apply {
            edtEmail.doOnTextChanged { text, _, _, _ ->
                if (!text.isNullOrBlank()){
                    viewModel.setEmail(text.toString())
                }
                tilPassword.error = null
            }

            edtPassword.doOnTextChanged { text, _, _, _ ->
                if (!text.isNullOrBlank()){
                    viewModel.setPassword(text.toString())
                }
                tilPassword.error = null
            }
        }
    }

    private fun observer() {
        viewModel.result.observe(viewLifecycleOwner) {
            if (it is Resource.Error){
                //if(!binding.tilEmail.editText?.text.isNullOrBlank() || !binding.tilPassword.editText?.text.isNullOrBlank()){
                    binding.tilPassword.error = it.message
                //}
            }
        }
    }
}