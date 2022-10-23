package com.sudo248.ltm.ui.activity.main.fragment.recent_chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sudo248.ltm.databinding.ItemRecentChatBinding
import com.sudo248.ltm.ui.uimodel.RecentChat
import com.sudo248.ltm.utils.DateUtils


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 19:56 - 23/10/2022
 */
class RecentChatsAdapter(
    private val onClickItem: (RecentChat, Int) -> Unit
) : RecyclerView.Adapter<RecentChatsAdapter.RecentChatsViewHolder>() {

    private var listChats: MutableList<RecentChat> = mutableListOf()

    fun submitList(list: MutableList<RecentChat>) {
        listChats = list
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, chat: RecentChat) {
        notifyItemRemoved(position)
        listChats.removeAt(position)
        listChats.add(0, chat)
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

    inner class RecentChatsViewHolder(private val binding: ItemRecentChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(chat: RecentChat, position: Int) {
            binding.apply {
                root.setOnClickListener {
                    onClickItem(chat, position)
                }
                txtTitle.text = chat.name
                txtDescription.text = chat.description
                Glide.with(itemView).load(chat.avtUrl).into(imgAvatar)
                txtTime.text = DateUtils.convertToString(chat.time)
            }
        }
    }
}