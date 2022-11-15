package com.sudo248.ltm.utils

import java.time.LocalDateTime

/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 20:19 - 23/10/2022
 */
object DateUtils {
    fun convertToString(time: LocalDateTime): String {
        return "${time.hour}:${time.minute}"
    }
}