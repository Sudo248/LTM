package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {

    List<MessageEntity> getAllByConversationId(Integer conversationId);

    @Query(value = "SELECT content, sent_at FROM MessageEntity m where m.conversation_id = :conversationId order by m.sent_at desc, limit (1)", nativeQuery = true)
    String getNewMessage(Integer conversationId);

}
