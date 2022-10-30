package com.sudo248.ltm.api.service;

import com.sudo248.ltm.api.model.entities.ConversationEntity;

public interface ConversationService {

    ConversationEntity getConversationById(Integer conversationId);

   // ConversationEntity update(ConversationEntity conversationEntity);

    void delete(Integer conversationId);
}
