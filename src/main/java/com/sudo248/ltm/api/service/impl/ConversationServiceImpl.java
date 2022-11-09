package com.sudo248.ltm.api.service.impl;

import com.sudo248.ltm.api.constants.Const;
import com.sudo248.ltm.api.model.conversation.ConversationType;
import com.sudo248.ltm.api.model.entities.ConversationEntity;
import com.sudo248.ltm.api.model.entities.UserConversationEntity;
import com.sudo248.ltm.api.repository.ConversationRepository;
import com.sudo248.ltm.api.repository.ProfileRepository;
import com.sudo248.ltm.api.repository.UserConversationRepository;
import com.sudo248.ltm.api.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserConversationRepository userConversationRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public List<ConversationEntity> getAllByUserId(Integer userId) {
        return conversationRepository.getAllByUserId(userId);
    }

    @Override
    public ConversationEntity getConversationById(Integer conversationId) {
        return conversationRepository.getById(conversationId);
    }

    @Override
    public ConversationEntity getConversationByName(String name) {
        return conversationRepository.getByName(name);
    }

    @Override
    public ConversationEntity createGroup(ArrayList<Integer> userIds) {
        ConversationEntity conversation = new ConversationEntity();
        if (userIds.size() == 2) {
            conversation.setName(userIds.get(0)+"-"+userIds.get(1));
            conversation.setType(ConversationType.P2P);
            conversation.setAvtUrl(Const.IMAGE_DEFAULT_P2P);
            conversation.setCreatedAt(LocalDateTime.now());
        } else {
            String name = profileRepository.getProfileByUserId(userIds.get(0)).getName();
            conversation.setName("Conversation of " + name);
            conversation.setType(ConversationType.GROUP);
            conversation.setAvtUrl(Const.IMAGE_DEFAULT_GROUP);
            conversation.setCreatedAt(LocalDateTime.now());
        }

        ConversationEntity storedConversationEntity = conversationRepository.save(conversation);

        for (Integer id : userIds) {
            UserConversationEntity userConversation = new UserConversationEntity(
                    id,
                    storedConversationEntity.getId(),
                    LocalDate.now()
            );
            userConversationRepository.save(userConversation);
        }

        return storedConversationEntity;
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

    @Override
    public void updateTimeConversation(Integer conversationId, LocalDateTime time) {
        conversationRepository.updateTimeConversation(conversationId, time);
    }
}

