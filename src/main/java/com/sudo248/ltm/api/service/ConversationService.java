package com.sudo248.ltm.api.service;

import com.sudo248.ltm.api.model.entities.ConversationEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface ConversationService {
    List<ConversationEntity> getAllByUserId(Integer userId);

    ConversationEntity getConversationById(Integer conversationId);

    ConversationEntity getConversationByName(String name);

   // ConversationEntity update(ConversationEntity conversationEntity);

    ConversationEntity createGroup(String nameGroup, ArrayList<Integer> userId);
    void delete(Integer conversationId);

    void updateTimeConversation(Integer conversationId, LocalDateTime time);

}
