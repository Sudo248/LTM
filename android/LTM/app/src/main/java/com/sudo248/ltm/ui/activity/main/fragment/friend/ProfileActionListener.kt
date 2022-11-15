package com.sudo248.ltm.ui.activity.main.fragment.friend

import com.sudo248.ltm.api.model.profile.Profile


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 22:47 - 06/11/2022
 */
interface ProfileActionListener {
    fun onAddNewGroup(profile: Profile, position: Int)
    fun onOpenMessage(profile: Profile)
    fun onAddFriend(profile: Profile, position: Int)
}