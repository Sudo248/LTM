package com.sudo248.ltm.api.service.impl;

import com.sudo248.ltm.api.model.entities.ConversationEntity;
import com.sudo248.ltm.api.model.entities.ConversationType;
import com.sudo248.ltm.api.model.entities.UserConversationEntity;
import com.sudo248.ltm.api.repository.ConversationRepository;
import com.sudo248.ltm.api.repository.UserConversationRepository;
import com.sudo248.ltm.api.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserConversationRepository userConversationRepository;

    @Override
    public List<ConversationEntity> getAllByUserId(Integer userId) {
        return conversationRepository.getAllByUserId(userId);
    }

    @Override
    public ConversationEntity getConversationById(Integer conversationId) {
        return conversationRepository.getById(conversationId);
    }

    @Override
    public List<ConversationEntity> findAllByName(String name) {
        return conversationRepository.findAllByName(name);
    }

    @Override
    public ConversationEntity createGroup(ArrayList<String> userId) {
        ConversationEntity conversation = new ConversationEntity("urgroup", ConversationType.P2P, "", LocalDate.now());
        if (userId.size() > 2) {
            conversation.setType(ConversationType.GROUP);
        }

        conversationRepository.save(conversation);

        for (String id : userId) {
            UserConversationEntity userConversation = new UserConversationEntity(
                    Integer.parseInt(id),
                    conversation.getId(),
                    LocalDate.now()
            );
            userConversationRepository.save(userConversation);
        }

        return conversationRepository.save(conversation);
    }

//    @Override
//    public ConversationEntity update(ConversationEntity conversationEntity) {
//        ConversationEntity conversation = conversationRepository.findById(conversationEntity.getId()).get();
//        conversation.setName(conversationEntity.getName());
//        conversation.setType(conversationEntity.getType());
//        conversation.setAvtUrl(conversationEntity.getAvtUrl());
//        return conversationRepository.save(conversation);
//    }

    @Override
    public void delete(Integer conversationId) {
        conversationRepository.deleteById(conversationId);
    }
}

