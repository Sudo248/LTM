package com.sudo248.ltm.api.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    List<Integer> getAllUserIdOfOneConversation(Integer conversationId);
}
