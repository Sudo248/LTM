package com.sudo248.ltm.ui.activity.main.fragment.recent_chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.databinding.FragmentRecentChatsBinding
import com.sudo248.ltm.ui.activity.main.MainViewModel
import com.sudo248.ltm.ui.activity.main.fragment.chat.ChatFragmentArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecentChatsFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: RecentChatsViewModel by viewModels()

    private lateinit var binding: FragmentRecentChatsBinding
    private lateinit var adapter: RecentChatsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentChatsBinding.inflate(inflater)
        setupRecentChatsAdapter()
        setOnClickListener()
        observer()
        return binding.root
    }

    private fun setupRecentChatsAdapter() {
        adapter = RecentChatsAdapter { conversation, _ ->
            navigateToChat(conversation)
        }
        binding.rcvRecentChats.adapter = adapter
    }

    private fun setOnClickListener() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getAllConversation(isFresh = true)
        }
        binding.imgSearch.setOnClickListener {
            findNavController().navigate(RecentChatsFragmentDirections.actionRecentChatsFragmentToSearchFragment())
        }
    }

    private fun observer() {
        viewModel.conversations.observe(viewLifecycleOwner) {
            Log.d("sudoo", "observer: $it")
            if (viewModel.isNewConversation) {
                adapter.newItem(it[0])
                viewModel.isNewConversation = false
            } else {
                adapter.submitList(it)
            }
            binding.swipeRefresh.isRefreshing = false
        }

        mainViewModel.newConversation.observe(viewLifecycleOwner) {
//            adapter.newItem(it.second)
            viewModel.addNewConversation(it.second)
        }

        mainViewModel.updateConversation.observe(viewLifecycleOwner) {
            adapter.updateItem(it.first, it.second)
        }

        viewModel.getAllConversation()
    }

    private fun navigateToChat(conversation: Conversation) {
        val action = RecentChatsFragmentDirections.actionRecentChatsFragmentToChatFragment(conversation)
        findNavController().navigate(action)
    }
}