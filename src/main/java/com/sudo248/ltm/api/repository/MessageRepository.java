package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {

    @Query(value = "SELECT * FROM message WHERE conversation_id = :conversationId", nativeQuery = true)
    List<MessageEntity> getAllByConversationId(@Param("conversationId") Integer conversationId);

    @Query(value = "SELECT content FROM message m where m.conversation_id = :conversationId order by m.sent_at desc limit 1", nativeQuery = true)
    String getNewMessage(@Param("conversationId") Integer conversationId);

}
