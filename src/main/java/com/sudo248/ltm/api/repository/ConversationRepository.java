package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.ConversationEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, Integer> {

    @Query(value = "select * from conversation c join user_conversation uc on c.id = uc.conversation_id where uc.user_id = :userId", nativeQuery = true)
    List<ConversationEntity> getAllByUserId(Integer userId);

    ConversationEntity getConversationById(Integer conversationId);

    ConversationEntity getByName(String name);
    //ConversationEntity update(ConversationEntity conversationEntity);
    @Query(value = "update conversation set create_at = :time where id = :conversationId", nativeQuery = true)
    void updateTimeConversation(@Param("conversationId") Integer conversationId, @Param("time") LocalDateTime time);

    void deleteById(@NotNull Integer conversationId);
}
