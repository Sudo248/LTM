package com.sudo248.ltm.ui.activity.main.fragment.chat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.databinding.ItemChatMeBinding
import com.sudo248.ltm.databinding.ItemChatOtherBinding
import com.sudo248.ltm.domain.model.Message
import com.sudo248.ltm.ktx.invisible


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 23:52 - 25/10/2022
 */
class ChatAdapter(
    private val clientId: Int
) : RecyclerView.Adapter<ChatViewHolder>() {

    companion object {
        const val VIEW_CHAT_ME = 1
        const val VIEW_CHAT_OTHER = 2
    }

    private val messages: MutableList<Message> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(messages: List<Message>) {
        this.messages.clear()
        this.messages.addAll(messages)
        notifyDataSetChanged()
    }

    fun addMessage(message: Message) {
        this.messages.add(message)
        notifyItemInserted(this.messages.size - 1)
    }

    override fun getItemViewType(position: Int): Int =
        if (messages[position].sendId == clientId) VIEW_CHAT_ME else VIEW_CHAT_OTHER

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return if (viewType == VIEW_CHAT_ME) {
            ChatMeViewHolder(
                ItemChatMeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ChatOtherViewHolder(
                ItemChatOtherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.onBind(
            messages[position],
            position,
            isSameNext = (position < messages.size - 2 && messages[position].sendId == messages[position + 1].sendId)
        )
    }

    override fun getItemCount(): Int = messages.size


}

abstract class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(message: Message, position: Int, isSameNext: Boolean = false)
}

class ChatMeViewHolder(private val binding: ItemChatMeBinding) :
    ChatViewHolder(binding.root) {
    override fun onBind(message: Message, position: Int, isSameNext: Boolean) {
        binding.apply {
            if (isSameNext) {
                root.setPadding(root.paddingLeft, root.paddingTop, root.paddingRight, 0)
            }
            txtContent.text = message.content
        }
    }

}

class ChatOtherViewHolder(private val binding: ItemChatOtherBinding) :
    ChatViewHolder(binding.root) {
    override fun onBind(message: Message, position: Int, isSameNext: Boolean) {
        binding.apply {
            if (isSameNext) {
                imgAvatar.invisible()
                root.setPadding(root.paddingLeft, root.paddingTop, root.paddingRight, 0)
            } else {
                Glide.with(itemView.context).load(message.avtUrl).into(imgAvatar)
            }
            txtContent.text = message.content
        }
    }
}
