package com.sudo248.ltm.ui.activity.main.fragment.friend

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sudo248.ltm.R
import com.sudo248.ltm.api.model.profile.Profile
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.databinding.ItemFriendBinding
import com.sudo248.ltm.ktx.visible
import com.sudo248.ltm.utils.gone


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 11:07 - 06/11/2022
 */
class ProfileAdapter(
    private val profileActionListener: ProfileActionListener,
    private val isShowIconAction: Boolean = true
) : RecyclerView.Adapter<ProfileAdapter.FriendViewHolder>() {

    private val listFriend: MutableList<Profile> = mutableListOf()
    var isAddGroup = false
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Profile>) {
        listFriend.clear()
        listFriend.addAll(list)
        notifyDataSetChanged()
    }

    fun addFriendSuccess(position: Int) {
        listFriend[position].isFriended = true
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            ItemFriendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.onBind(listFriend[position], position)
    }

    override fun getItemCount(): Int = listFriend.size

    inner class FriendViewHolder(private val binding: ItemFriendBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(profile: Profile, position: Int) {
            binding.apply {
                txtNameFriend.text = profile.name
                txtDescription.text = profile.bio

                if (isShowIconAction) {
                    val action = when{
                        isAddGroup -> {
                            Log.d("sudoo", "onBind: isAddGroup")
                            imgAction.isEnabled = true
                            imgAction.setOnClickListener {
                                profileActionListener.onAddNewGroup(profile, position)
                                imgAction.setImageResource(R.drawable.ic_done)
                                imgAction.isEnabled = false
                            }
                            R.drawable.ic_group_add
                        }
                        profile.isFriended -> {
                            Log.d("sudoo", "onBind: isFriended")
                            imgAction.setOnClickListener {
                                Log.d("sudoo", "onBind: isFriended")
                                profileActionListener.onOpenMessage(profile)
                            }
                            R.drawable.ic_chat
                        }
                        else -> {
                            Log.d("sudoo", "onBind: add friend")
                            imgAction.setOnClickListener {
                                Log.d("sudoo", "onBind: add friend")
                                profileActionListener.onAddFriend(profile, position)
                            }
                            R.drawable.ic_add_friend
                        }
                    }
                    imgAction.setImageResource(action)
                } else {
                    imgAction.gone()
                }

                if (profile.active) {
                    imgStatus.visible()
                } else {
                    imgStatus.gone()
                }

                val imageUrl = "${Constant.URL_IMAGE}${profile.image}"
                Glide
                    .with(itemView.context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.ic_error)
                    .into(imgAvatarFriend)
            }
        }
    }
}