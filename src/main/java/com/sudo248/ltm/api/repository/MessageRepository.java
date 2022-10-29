package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {

    List<MessageEntity> getAllByConversationId(Integer conversationId);

}
