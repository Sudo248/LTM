package com.sudo248.ltm.data.repository.recent_chat

import com.sudo248.ltm.common.Resource
import com.sudo248.ltm.ui.uimodel.RecentChat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import org.sudo248.mqtt.model.MqttMessage


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 20:22 - 23/10/2022
 */
interface RecentChatsRepository {
    fun getAllRecentChat(): Flow<Resource<List<RecentChat>> >
    fun getMessageFlow(): SharedFlow<MqttMessage>
}