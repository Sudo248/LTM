package com.sudo248.ltm.api.service;

import com.sudo248.ltm.api.model.entities.MessageEntity;
import com.sudo248.ltm.api.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface MessageService {

    List<MessageEntity> getAllMessage(Integer conversationId);

    String getNewMessage(Integer conversationId);
}
