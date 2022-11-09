package com.sudo248.ltm.ui.activity.main.fragment.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sudo248.ltm.R
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.databinding.FragmentProfileBinding
import com.sudo248.ltm.ui.activity.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        viewModel.profile.observe(viewLifecycleOwner){
            binding.edtName.setText(it.name)
            binding.edtBio.setText(it.bio)
            val imageUrl = "${Constant.URL_IMAGE}${it.image}"
            Glide
                .with(requireContext())
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.ic_error)
                .into(binding.imgAvatar)
        }
        viewModel.getProfile()
        binding.txtSignOut.setOnClickListener {
            val intent = Intent(requireActivity(), AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        return binding.root
    }



}