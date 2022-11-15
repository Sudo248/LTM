package com.sudo248.ltm.common


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 00:39 - 23/10/2022
 */
object Constant {
    const val SERVER_HOST = "192.168.1.11"
    const val SERVER_PORT = 8081
    const val SERVER_URL = "http://$SERVER_HOST:$SERVER_PORT"
    const val WS_HOST = SERVER_HOST
    const val WS_PORT = 6026
    const val USER_ID = "userId"
    const val FRIEND_ID = "friendId"
    const val CONVERSATION_ID = "conversationId"
    const val NAME_GROUP = "nameGroup"
    const val CONVERSATION_NAME = "conversationName"
    const val NEW_SUBSCRIPTION = -1
    const val UNKNOWN = -2

    const val URL_IMAGE = "$SERVER_URL/images/"

    const val PATH_LOGIN = "/user/login"
    const val PATH_SIGN_UP = "/user/signup"
    const val PATH_CONVERSATION = "/conversation"
    const val PATH_MESSAGE = "/message"
    const val PATH_USER_CONVERSATION = "/conversation/get"
    const val PATH_CHATS = "/chats"
    const val PATH_UPLOAD_IMAGE = "/upload/images"
    const val PATH_PROFILE = "/profile"
    const val PATH_FRIEND = "/friend"
    const val PATH_CREATE_GROUP = "/conversation/create_group"
    const val PATH_GET_PROFILE = "/profile/get"

    const val URL_REGEX = "[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)"
    const val URL_IMAGE_REGEX = "(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|gif|png)"
    const val TEST_CONVERSATION_ID = 24080309
    const val TEST_CLIENT_ID = 7480201
    const val SERVER_ID = 24080309L
    const val IMAGE_USER_DEFAULT = "default_user_image.png"
}

object PrefKey {
    const val KEY_USER_ID = "USER-ID"
    const val KEY_USER_IMAGE = "USER-IMAGE"
}