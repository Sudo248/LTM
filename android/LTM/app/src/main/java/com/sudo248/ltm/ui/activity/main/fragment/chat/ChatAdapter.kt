package com.sudo248.ltm.ui.activity.main.fragment.chat

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.sudo248.ltm.R
import com.sudo248.ltm.api.model.message.ContentMessageType
import com.sudo248.ltm.common.Constant
import com.sudo248.ltm.databinding.ItemChatMeBinding
import com.sudo248.ltm.databinding.ItemChatOtherBinding
import com.sudo248.ltm.databinding.ItemChatServerBinding
import com.sudo248.ltm.domain.model.Message
import com.sudo248.ltm.ktx.gone
import com.sudo248.ltm.ktx.invisible
import com.sudo248.ltm.ktx.visible
import com.sudo248.ltm.utils.ImageTarget
import kotlinx.coroutines.coroutineScope


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
        const val VIEW_CHAT_SERVER = 3
    }

    private val messages: MutableList<Message> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(messages: List<Message>) {
        this.messages.clear()
        this.messages.addAll(messages)
        notifyDataSetChanged()
    }

    suspend fun addMessage(message: Message) = coroutineScope {
        messages.add(message)
        if (messages.size > 1) {
            notifyItemChanged(messages.size - 2)
        }
        notifyItemInserted(messages.size - 1)
    }

    override fun getItemViewType(position: Int): Int = when(messages[position].sendId) {
        Constant.SERVER_ID.toInt() -> VIEW_CHAT_SERVER
        clientId -> VIEW_CHAT_ME
        else -> VIEW_CHAT_OTHER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return when(viewType) {
            VIEW_CHAT_SERVER -> {
                ChatServerViewHolder(
                    ItemChatServerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            } VIEW_CHAT_ME -> {
                ChatMeViewHolder(
                    ItemChatMeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                ChatOtherViewHolder(
                    ItemChatOtherBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.onBind(
            messages[position],
            position,
            isSameNext = (position < messages.size - 1 && messages[position].sendId == messages[position + 1].sendId)
        )
        Log.d("sudoo", "onBindViewHolder: messages: ${messages.size} ")
        if (position < messages.size - 1) {
            Log.d("sudoo", "onBindViewHolder: position: $position")
            Log.d(
                "sudoo",
                "onBindViewHolder: ${position < messages.size - 1} && ${messages[position].sendId == messages[position + 1].sendId}"
            )
        }
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
            when (message.contentType) {
                ContentMessageType.MESSAGE -> {
                    txtContent.visible()
                    cardContent.gone()
                    txtContent.text = message.content
                }
                ContentMessageType.IMAGE -> {
                    txtContent.gone()
                    cardContent.visible()
                    val imageUrl =
                        if (message.content.matches(Regex(Constant.URL_IMAGE_REGEX)))
                            message.content
                        else
                            "${Constant.URL_IMAGE}${message.content}"
                    Glide
                        .with(itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.ic_error)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(ImageTarget(imgContent))
                }
                else -> {
                    Log.e("ChatMeViewHolder", "onBind: Error type message")
                }
            }
        }
    }

}

class ChatOtherViewHolder(private val binding: ItemChatOtherBinding) :
    ChatViewHolder(binding.root) {
    override fun onBind(message: Message, position: Int, isSameNext: Boolean) {
        binding.apply {
            if (isSameNext) {
                cardAvatar.invisible()
                root.setPadding(root.paddingLeft, root.paddingTop, root.paddingRight, 0)
            } else {
                cardAvatar.visible()
                Glide
                    .with(itemView.context)
                    .load("${Constant.URL_IMAGE}${message.avtUrl}")
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.ic_error)
                    .into(imgAvatar)
            }
            when (message.contentType) {
                ContentMessageType.MESSAGE -> {
                    txtContent.visible()
                    cardContent.gone()
                    txtContent.text = message.content
                }
                ContentMessageType.IMAGE -> {
                    txtContent.gone()
                    cardContent.visible()
                    val imageUrl =
                        if (message.content.matches(Regex(Constant.URL_IMAGE_REGEX)))
                            message.content
                        else
                            "${Constant.URL_IMAGE}${message.content}"
                    Glide
                        .with(itemView.context)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.ic_error)
                        .into(ImageTarget(imgContent))
                }
                else -> {
                    Log.e("ChatOtherViewHolder", "onBind: Error type message")
                }
            }
        }
    }
}

class ChatServerViewHolder(private val binding: ItemChatServerBinding) : ChatViewHolder(binding.root) {
    override fun onBind(message: Message, position: Int, isSameNext: Boolean) {
        binding.txtContent.text = message.content
    }
}
