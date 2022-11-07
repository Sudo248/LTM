package com.sudo248.ltm.api.service;

import com.sudo248.ltm.api.model.entities.ConversationEntity;

import java.util.List;

public interface ConversationService {
    List<ConversationEntity> getAllByUserId(Integer userId);

    ConversationEntity getConversationById(Integer conversationId);

   // ConversationEntity update(ConversationEntity conversationEntity);

    void delete(Integer conversationId);

}
