package com.sudo248.ltm.ui.uimodel

import java.time.LocalDateTime


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 20:04 - 23/10/2022
 */
data class RecentChat(
        val idChat: Int,
        val avtUrl: String,
        val name: String,
        val description: String,
        val time: LocalDateTime
)