package com.sudo248.ltm.ui.activity.main.fragment.recent_chat

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sudo248.ltm.R
import com.sudo248.ltm.api.model.conversation.Conversation
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.databinding.ItemRecentChatBinding
import com.sudo248.ltm.ktx.gone
import com.sudo248.ltm.utils.DateUtils


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 19:56 - 23/10/2022
 */
class RecentChatsAdapter(
    private val isSearch: Boolean = false,
    private val onClickItem: (Conversation, Int) -> Unit
) : RecyclerView.Adapter<RecentChatsAdapter.RecentChatsViewHolder>() {

    private var listChats: MutableList<Conversation> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Conversation>) {
        listChats.clear()
        listChats.addAll(list)
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, conversation: Conversation) {
        notifyItemRemoved(position)
        listChats.removeAt(position)
        listChats.add(0, conversation)
        notifyItemInserted(0)
    }

    fun newItem(conversation: Conversation) {
        listChats.add(0, conversation)
        notifyItemInserted(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatsViewHolder {
        return RecentChatsViewHolder(
            ItemRecentChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecentChatsViewHolder, position: Int) {
        holder.onBind(listChats[position], position)
    }

    override fun getItemCount(): Int = listChats.size

    inner class RecentChatsViewHolder(private val binding: ItemRecentChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(conversation: Conversation, position: Int) {
            binding.apply {
                root.setOnClickListener {
                    onClickItem(conversation, position)
                }
                txtTitle.text = conversation.name
                val imageUrl = "${Constant.URL_IMAGE}${conversation.avtUrl}"
                Glide
                    .with(itemView.context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.ic_error)
                    .into(imgAvatar)
                if (!isSearch) {
                    txtDescription.text = conversation.description
                    txtTime.text = DateUtils.convertToString(conversation.createAt)
                } else {
                    txtDescription.gone()
                    txtTime.gone()
                }
            }
        }
    }
}