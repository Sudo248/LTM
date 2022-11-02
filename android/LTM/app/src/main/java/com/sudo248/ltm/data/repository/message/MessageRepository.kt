package com.sudo248.ltm.data.repository.message

import android.content.ContentResolver
import android.net.Uri
import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.domain.model.Message
import kotlinx.coroutines.flow.Flow


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 08:18 - 26/10/2022
 */
interface MessageRepository {
    fun getUserId(): Int
    suspend fun getAllMessage(idConversation: Int): Flow<Resource<List<Message>>>
    suspend fun newMessageInTopic(topic: Int): Flow<Message>
    suspend fun sendMessage(topic: Int, message: Message): Boolean
    suspend fun subscribeTopic(topic: Int): Boolean
    suspend fun sendImage(resolver: ContentResolver, uri: Uri): Resource<String>
}