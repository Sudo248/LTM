package com.sudo248.ltm.api.service;

import com.sudo248.ltm.api.model.entities.ConversationEntity;

import java.util.ArrayList;
import java.util.List;

public interface ConversationService {
    List<ConversationEntity> getAllByUserId(Integer userId);

    ConversationEntity getConversationById(Integer conversationId);

    List<ConversationEntity> findAllByName(String name);

   // ConversationEntity update(ConversationEntity conversationEntity);

    ConversationEntity createGroup(ArrayList<String> userId);
    void delete(Integer conversationId);

}
