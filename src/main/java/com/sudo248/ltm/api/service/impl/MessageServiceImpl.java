package com.sudo248.ltm.api.service.impl;

import com.sudo248.ltm.api.model.entities.MessageEntity;
import com.sudo248.ltm.api.repository.MessageRepository;
import com.sudo248.ltm.api.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Override
    public List<MessageEntity> getAllMessage(Integer conversationId) {
        return messageRepository.getAllByConversationId(conversationId);
    }

    @Override
    public String getNewMessage(Integer conversationId) {
        return getNewMessage(conversationId);
    }

    @Override
    public MessageEntity createMessage(MessageEntity messageEntity) {
        return messageRepository.save(messageEntity);
    }
}
