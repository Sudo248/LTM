package com.sudo248.ltm.api.service.impl;

import com.sudo248.ltm.api.model.entities.UserConversationEntity;
import com.sudo248.ltm.api.repository.UserConversationRepository;
import com.sudo248.ltm.api.service.UserConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserConversationServiceImpl implements UserConversationService {

    @Autowired
    private UserConversationRepository userConversationRepository;
    @Override
    public List<UserConversationEntity> getAllByUserId(Integer userId) {
        return userConversationRepository.getAllByUserId(userId);
    }
}
