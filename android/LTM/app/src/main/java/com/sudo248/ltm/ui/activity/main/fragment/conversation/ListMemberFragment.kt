package com.sudo248.ltm.ui.activity.main.fragment.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sudo248.ltm.R
import com.sudo248.ltm.api.model.profile.Profile
import com.sudo248.ltm.databinding.FragmentInfoConversationBinding
import com.sudo248.ltm.databinding.FragmentListMemberBinding
import com.sudo248.ltm.ui.activity.main.MainActivity
import com.sudo248.ltm.ui.activity.main.fragment.friend.ProfileActionListener
import com.sudo248.ltm.ui.activity.main.fragment.friend.ProfileAdapter
import dagger.hilt.android.AndroidEntryPoint


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 00:34 - 16/11/2022
 */
@AndroidEntryPoint
class ListMemberFragment : Fragment(){
    private lateinit var binding: FragmentListMemberBinding
    private val listMemberArgs: ListMemberFragmentArgs by navArgs()
    private val viewModel: InfoConversationViewModel by viewModels()
    private lateinit var profileAdapter: ProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListMemberBinding.inflate(inflater)
        profileAdapter = ProfileAdapter(
            object : ProfileActionListener {
                override fun onAddNewGroup(profile: Profile, position: Int) {

                }

                override fun onOpenMessage(profile: Profile) {
                }

                override fun onAddFriend(profile: Profile, position: Int) {
                }

            },
            isShowIconAction = false
        )
        binding.rcvMemBers.adapter = profileAdapter
        viewModel.members.observe(viewLifecycleOwner) {
            binding.txtNumberMembers.text = String.format(getString(R.string.peoples), it.size)
            profileAdapter.submitList(it)
        }
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.getMemberConversation(listMemberArgs.conversation.id)
        getMainActivity().hideBottomNavigation()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getMainActivity().showBottomNavigation()
    }

    private fun getMainActivity(): MainActivity {
        return activity as MainActivity
    }

}