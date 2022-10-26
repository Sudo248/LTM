package com.sudo248.ltm.common


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 00:39 - 23/10/2022
 */
object Constant {
    const val WS_HOST = "192.168.1.11"
    const val WS_PORT = 6026
    const val USER_ID = "userId"
    const val CONVERSATION_ID = "conversationId"
    const val NEW_SUBSCRIPTION = -1
    const val UNKNOWN = -2

    const val PATH_LOGIN = "/user/login"
    const val PATH_SIGN_UP = "/user/signup"
    const val PATH_CONVERSATION = "/conversations"
    const val PATH_CHATS = "/chats"
}

object PrefKey {
    const val KEY_USER_ID = "USER-ID"
}