package com.sudo248.ltm.di

import com.sudo248.ltm.data.repository.auth.AuthRepository
import com.sudo248.ltm.data.repository.auth.AuthRepositoryImpl
import com.sudo248.ltm.data.repository.conversation.ConversationRepository
import com.sudo248.ltm.data.repository.conversation.ConversationRepositoryImpl
import com.sudo248.ltm.data.repository.message.MessageRepository
import com.sudo248.ltm.data.repository.message.MessageRepositoryImpl
import com.sudo248.ltm.data.repository.profile.ProfileRepository
import com.sudo248.ltm.data.repository.profile.ProfileRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 12:01 - 23/10/2022
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindAuthRepository(authRepository: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindConversationRepository(conversationRepository: ConversationRepositoryImpl): ConversationRepository

    @Binds
    abstract fun bindMessageRepository(messageRepository: MessageRepositoryImpl): MessageRepository

    @Binds
    abstract fun bindProfileRepository(profileRepository: ProfileRepositoryImpl): ProfileRepository
}