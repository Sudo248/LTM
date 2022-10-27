package com.sudo248.ltm.api.service.impl;

import com.sudo248.ltm.api.model.entities.ConversationEntity;
import com.sudo248.ltm.api.repository.ConversationRepository;
import com.sudo248.ltm.api.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Override
    public ConversationEntity getConversationById(Integer conversationId) {
        return conversationRepository.getById(conversationId);
    }

    @Override
    public ConversationEntity update(ConversationEntity conversationEntity) {
        ConversationEntity conversation = conversationRepository.findById(conversationEntity.getId()).get();
        conversation.setName(conversationEntity.getName());
        conversation.setType(conversationEntity.getType());
        conversation.setAvtUrl(conversationEntity.getAvtUrl());
        return conversationRepository.save(conversation);
    }

    @Override
    public void delete(Integer conversationId) {
        conversationRepository.deleteById(conversationId);
    }
}
