package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.ConversationEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, Integer> {

    @Query(value = "select * from conversation c join user_conversation uc on c.id = uc.conversation_id where uc.user_id = :userId", nativeQuery = true)
    List<ConversationEntity> getAllByUserId(Integer userId);

    ConversationEntity getById(Integer conversationId);

    //ConversationEntity update(ConversationEntity conversationEntity);

    void deleteById(@NotNull Integer conversationId);
}
