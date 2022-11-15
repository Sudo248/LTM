package com.sudo248.ltm.ui.activity.main.fragment.conversation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sudo248.ltm.R
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.databinding.FragmentInfoConversationBinding
import com.sudo248.ltm.ui.activity.main.MainActivity
import com.sudo248.ltm.utils.DialogUtils
import dagger.hilt.android.AndroidEntryPoint


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 21:49 - 15/11/2022
 */
@AndroidEntryPoint
class InfoConversationFragment : Fragment() {

    private lateinit var binding: FragmentInfoConversationBinding
    private val infoConversationFragmentArgs: InfoConversationFragmentArgs by navArgs()
    private val viewModel: InfoConversationViewModel by viewModels()

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
        binding = FragmentInfoConversationBinding.inflate(inflater)
        viewModel.conversation = infoConversationFragmentArgs.conversation
        viewModel.image.postValue(viewModel.conversation.avtUrl)
        viewModel.nameConversation.postValue(viewModel.conversation.name)
        setupInfo()
        setOnClickListener()
        getMainActivity().hideBottomNavigation()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getMainActivity().showBottomNavigation()
    }

    private fun setOnClickListener() {
        binding.txtNameGroup.setOnClickListener {
            DialogUtils.showInputDialog(
                requireContext(),
                getString(R.string.conversation),
                hint = getString(R.string.user_name),
                initValue = viewModel.conversation.name,
                onSubmit = { nameGroup ->
                    viewModel.updateNameConversation(nameGroup)
                }
            )
        }

        binding.imgChangeAvatar.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.txtRemove.setOnClickListener {
            viewModel.removeConversation()
        }

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.txtMembers.setOnClickListener {
            navigateToListMember()
        }
    }

    private fun setupInfo() {
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

        viewModel.nameConversation.observe(viewLifecycleOwner){
            binding.txtNameGroup.text = it
        }

        viewModel.removeConversation.observe(viewLifecycleOwner) {
            if (it) {
                navigateOffRecentChat()
            }
        }
    }

    private fun navigateOffRecentChat() {
        findNavController().apply {
            popBackStack()
            popBackStack()
        }
    }

    private fun navigateToListMember() {
        val action = InfoConversationFragmentDirections.actionInfoConversationFragmentToListMemberFragment(viewModel.conversation)
        findNavController().navigate(action)
    }

    private fun getMainActivity(): MainActivity {
        return activity as MainActivity
    }

}