package com.sudo248.ltm.ui.activity.main.fragment.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("sudoo", "pickMedia: ${uri.path}")
            viewModel.sendImage(requireContext().contentResolver, uri)
        } else {
            Log.e("sudoo", "pickMedia: uri null")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        viewModel.image.observe(viewLifecycleOwner) {
            val imageUrl = "${Constant.URL_IMAGE}${it}"
            Glide
                .with(requireContext())
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.ic_error)
                .into(binding.imgAvatar)
        }
        viewModel.profile.observe(viewLifecycleOwner){
            binding.edtUsername.setText(it.name)
            binding.edtBio.setText(it.bio)
        }
        viewModel.getProfile()

        binding.imgChangeAvatar.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.txtSignOut.setOnClickListener {
            val intent = Intent(requireActivity(), AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.txtSave.setOnClickListener {
            val name = binding.edtUsername.text.toString()
            val bio = binding.edtBio.text.toString()
            viewModel.updateProfile(name, bio)
        }
        return binding.root
    }



}