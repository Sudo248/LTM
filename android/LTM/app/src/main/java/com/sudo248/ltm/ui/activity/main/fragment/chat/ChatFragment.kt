package com.sudo248.ltm.ui.activity.main.fragment.chat

import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sudo248.ltm.R
import com.sudo248.ltm.api.model.conversation.ConversationType
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.databinding.FragmentChatBinding
import com.sudo248.ltm.ktx.gone
import com.sudo248.ltm.ktx.visible
import com.sudo248.ltm.ui.activity.main.MainActivity
import com.sudo248.ltm.ui.activity.main.fragment.recent_chat.RecentChatsFragmentDirections
import com.sudo248.ltm.utils.DialogUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URL

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private val chatFragmentArgs: ChatFragmentArgs by navArgs()
    private lateinit var chatAdapter: ChatAdapter

    private val autoScroll = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            scroll()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            scroll()
        }

        override fun onChanged() {
            super.onChanged()
            scroll()
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("sudoo", "pickMedia: ${uri.path}")
            viewModel.sendImage(requireContext().contentResolver, uri)
        } else {
            Log.e("sudoo", "pickMedia: uri null")
        }
    }

    private fun scroll() {
        lifecycleScope.launch {
            delay(100)
            binding.rcvChat.scrollToPosition(chatAdapter.messages.size - 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater)
        setupUi()
        setupAdapter()
        setOnClickListener()
        observer()
        return binding.root
    }

    private fun setupUi() {
        getMainActivity().hideBottomNavigation()
        viewModel.conversation = chatFragmentArgs.conversation
        binding.apply {
            if (viewModel.conversation.type == ConversationType.GROUP) {
                imgInfo.visible()
                imgInfo.setOnClickListener {
                    navigateToInfoFragment()
                }
            } else {
                imgInfo.gone()
            }
            txtTitleChat.text = viewModel.conversation.name
            val imageUrl = "${Constant.URL_IMAGE}${viewModel.conversation.avtUrl}"
            Glide
                .with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.ic_error)
                .into(imgAvatar)

            edtInputMessage.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    imgAddImage.gone()
                } else {
                    imgAddImage.visible()
                }
            }
            edtInputMessage.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    imgAddImage.visible()
                } else {
                    imgAddImage.gone()
                }
            }
            imgAddImage.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }

    private fun setupAdapter() {
        chatAdapter = ChatAdapter(viewModel.userId)
        chatAdapter.registerAdapterDataObserver(autoScroll)
        binding.rcvChat.adapter = chatAdapter
    }

    private fun setOnClickListener() {
        binding.apply {
            imgBack.setOnClickListener {
                onBackPress()
            }

            imgSend.setOnClickListener {
                viewModel.sendMessage(edtInputMessage.text.toString())
                edtInputMessage.text.clear()
            }

            imgVideo.setOnClickListener {
                DialogUtils.showDialog(
                    requireContext(),
                    title = getString(R.string.developing),
                    description = getString(R.string.feature_develop)
                )
            }
        }
    }

    private fun observer() {
        viewModel.newMessage.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                chatAdapter.addMessage(it)
            }
        }
        viewModel.messages.observe(viewLifecycleOwner) {
            chatAdapter.submitList(it)
        }
        viewModel.subscribeTopic()
        viewModel.getAllMessage()
        viewModel.updateNewMessage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getMainActivity().showBottomNavigation()
        chatAdapter.unregisterAdapterDataObserver(autoScroll)
    }

    private fun getMainActivity(): MainActivity {
        return activity as MainActivity
    }

    private fun onBackPress() {
        findNavController().popBackStack()
    }

    private fun navigateToInfoFragment() {
        val action = ChatFragmentDirections.actionChatFragmentToInfoConversationFragment(viewModel.conversation)
        findNavController().navigate(action)
    }
}