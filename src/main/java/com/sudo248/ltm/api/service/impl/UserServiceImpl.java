package com.sudo248.ltm.api.service.impl;

import com.sudo248.ltm.api.repository.UserRepository;
import com.sudo248.ltm.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public List<Integer> getAllUserIdOfOneConversation(Integer conversationId) {
        return userRepository.getAllByConversationId(conversationId);
    }
}
