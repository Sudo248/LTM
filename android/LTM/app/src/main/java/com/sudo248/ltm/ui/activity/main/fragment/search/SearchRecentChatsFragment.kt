package com.sudo248.ltm.ui.activity.main.fragment.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.databinding.FragmentSearchRecentChatsBinding
import com.sudo248.ltm.ui.activity.main.MainActivity
import com.sudo248.ltm.ui.activity.main.fragment.recent_chat.RecentChatsAdapter
import com.sudo248.ltm.ui.activity.main.fragment.recent_chat.RecentChatsFragmentDirections
import com.sudo248.ltm.ui.activity.main.fragment.recent_chat.RecentChatsViewModel
import com.sudo248.ltm.utils.KeyboardUtils
import dagger.hilt.android.AndroidEntryPoint


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 10:23 - 06/11/2022
 */
@AndroidEntryPoint
class SearchRecentChatsFragment : Fragment() {

    private lateinit var binding: FragmentSearchRecentChatsBinding
    private val viewModel: RecentChatsViewModel by viewModels()
    private lateinit var conversationAdapter: RecentChatsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchRecentChatsBinding.inflate(inflater)
        setupView()
        setupSearchView()
        setOnClickListener()
        observe()
        return binding.root
    }

    private fun setupView() {
        setupAdapter()
        getMainActivity().hideBottomNavigation()
    }

    private fun setOnClickListener() {
        binding.apply {
            imgBack.setOnClickListener {
                onBackPress()
            }
//            svSearch.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn).setOnClickListener {
//
//            }
        }
    }

    private fun setupAdapter() {
        conversationAdapter = RecentChatsAdapter(isSearch = true) { conversation, _ ->
            navigateToChat(conversation)
        }
        binding.rcvSearchResult.adapter = conversationAdapter
    }

    private fun setupSearchView() {
        binding.svSearch.apply {
            requestFocus()
            setOnCloseListener {
                setQuery("", false)
                true
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.searchConversationByName(query ?: "")
                    KeyboardUtils.hide(requireActivity())
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.searchConversationByName(newText ?: "")
                    return true
                }
            })
        }
    }

    private fun observe() {
        viewModel.conversations.observe(viewLifecycleOwner) {
            conversationAdapter.submitList(it)
        }
        viewModel.getAllConversation()
    }

    private fun navigateToChat(conversation: Conversation) {
        val action = SearchRecentChatsFragmentDirections.actionSearchFragmentToChatFragment(conversation)
        findNavController().navigate(action)
    }

    private fun onBackPress() {
        findNavController().popBackStack()
    }

    private fun getMainActivity(): MainActivity {
        return activity as MainActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        getMainActivity().showBottomNavigation()
    }
}