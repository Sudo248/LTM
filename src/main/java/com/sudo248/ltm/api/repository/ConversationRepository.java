package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.ConversationEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.websocket.server.PathParam;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, Integer> {

    @Query(value = "select * from conversation c join user_conversation uc on c.id = uc.conversation_id where uc.user_id = :userId", nativeQuery = true)
    List<ConversationEntity> getAllByUserId(Integer userId);

    ConversationEntity getById(Integer conversationId);

    void deleteById(@NotNull Integer conversationId);

    @Query(value = "select * from conversation where name = :name", nativeQuery = true)
    ConversationEntity getByName(@Param("name") String name);

    @Modifying
    @Query(value = "UPDATE conversation SET created_at = :time WHERE id = :conversationId", nativeQuery = true)
    void updateTimeConversation(@Param("conversationId") Integer conversationId, @Param("time")LocalDateTime time);
}
