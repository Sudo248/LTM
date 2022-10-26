package com.sudo248.ltm.di

import com.sudo248.ltm.data.repository.auth.AuthRepository
import com.sudo248.ltm.data.repository.auth.AuthRepositoryImpl
import com.sudo248.ltm.data.repository.conversation.ConversationRepository
import com.sudo248.ltm.data.repository.conversation.ConversationRepositoryImpl
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

}