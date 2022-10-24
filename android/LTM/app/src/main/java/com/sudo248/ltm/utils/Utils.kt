package com.sudo248.ltm.utils

import java.security.MessageDigest


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 14:51 - 23/10/2022
 */
object Utils {
    fun hash(password: String): String{
        val digest = MessageDigest.getInstance("SHA-1")
        val  sb = StringBuilder()
        val result = digest.digest(password.toByteArray(Charsets.UTF_8))
        for (i in result){
            sb.append(String.format("%02X",i))
        }
        return sb.toString()
    }
}