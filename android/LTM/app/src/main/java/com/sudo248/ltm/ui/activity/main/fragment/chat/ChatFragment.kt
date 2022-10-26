package com.sudo248.ltm.ui.activity.main.fragment.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sudo248.ltm.R
import com.sudo248.ltm.databinding.FragmentChatBinding
import com.sudo248.ltm.ui.activity.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()

    private val chatFragmentArgs: ChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater)
        getMainActivity().actionBottomNavigation()
        binding.txtTitleChat.text = chatFragmentArgs.conversation.name
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getMainActivity().actionBottomNavigation()
    }

    private fun getMainActivity(): MainActivity {
        return activity as MainActivity
    }
}