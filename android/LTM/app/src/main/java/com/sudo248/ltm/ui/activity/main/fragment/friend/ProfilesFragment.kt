package com.sudo248.ltm.ui.activity.main.fragment.friend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sudo248.ltm.R
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.api.model.profile.Profile
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.databinding.FragmentProfilesBinding
import com.sudo248.ltm.ktx.gone
import com.sudo248.ltm.ktx.visible
import com.sudo248.ltm.ui.activity.main.fragment.recent_chat.RecentChatsFragmentDirections
import com.sudo248.ltm.utils.DialogUtils
import com.sudo248.ltm.utils.KeyboardUtils
import dagger.hilt.android.AndroidEntryPoint


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 09:40 - 06/11/2022
 */
@AndroidEntryPoint
class ProfilesFragment : Fragment() {

    private lateinit var binding: FragmentProfilesBinding
    private val viewModel: ProfilesViewModel by viewModels()
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var newGroupAdapter: NewGroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilesBinding.inflate(inflater)
        setupView()
        setupSearch()
        setupAdapter()
        setOnclickListener()
        observer()
        return binding.root
    }

    private fun setupView() {

    }

    private fun setupAdapter() {
        newGroupAdapter = NewGroupAdapter {
            profileAdapter.notifyItemChanged(it)
        }
        binding.rcvAddGroup.adapter = newGroupAdapter
        profileAdapter = ProfileAdapter(object : ProfileActionListener {
            override fun onAddNewGroup(profile: Profile, position: Int) {
                newGroupAdapter.addNewItem(profile, position)
            }

            override fun onOpenMessage(profile: Profile) {
                Log.d("sudoo", "onOpenMessage: ${profile.userId}")
                viewModel.getConversationByProfile(profile)
            }

            override fun onAddFriend(profile: Profile, position: Int) {
                viewModel.addFriend(profile, position)
            }
        })
        binding.rcvProfile.adapter = profileAdapter
    }

    private fun setupSearch() {
        binding.svSearch.apply {
            requestFocus()
            setOnCloseListener {
                setQuery("", false)
                true
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.searchProfileByName(query ?: "")
                    KeyboardUtils.hide(requireActivity())
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.searchProfileByName(newText ?: "")
                    return true
                }
            })
        }
    }

    private fun setOnclickListener() {
        binding.txtAddGroup.setOnClickListener {
            binding.txtAddGroup.gone()
            binding.groupAddNewGroup.visible()
            profileAdapter.isAddGroup = true
        }
        binding.imgActionAddGroup.setOnClickListener {
            viewModel.createGroup(newGroupAdapter.listProfile)
        }
    }

    private fun observer() {
        viewModel.listProfile.observe(viewLifecycleOwner) {
            profileAdapter.submitList(it)
        }

        viewModel.conversation.observe(viewLifecycleOwner) {
            navigateToChat(conversation = it)
        }

        viewModel.addFriend.observe(viewLifecycleOwner) {
            profileAdapter.addFriendSuccess(it)
        }

        viewModel.createGroupState.observe(viewLifecycleOwner) {
            binding.imgActionAddGroup.apply {
                when (it) {
                    is Resource.Loading -> {
                        setImageResource(R.drawable.placeholder)
                    }
                    is Resource.Success -> {
                        navigateToChat(it.requiredData())
                    }
                    else -> {
                        DialogUtils.showDialog(
                            requireContext(),
                            title = getString(R.string.error),
                            description = (it as Resource.Error).message,
                            textColorTitle = R.color.red,
                            onClickConfirm = {
                                findNavController().popBackStack()
                            }
                        )
                    }
                }
            }
        }
        viewModel.getAllProfile()
    }

    private fun navigateToChat(conversation: Conversation) {
        val action = ProfilesFragmentDirections.actionFriendFragmentToChatFragment(conversation)
        findNavController().navigate(action)
    }
}