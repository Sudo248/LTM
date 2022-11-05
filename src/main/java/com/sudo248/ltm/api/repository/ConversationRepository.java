package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.ConversationEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, Integer> {

   // List<ConversationEntity> getAllByUserId(Integer userId);

    ConversationEntity getById(Integer conversationId);

    //ConversationEntity update(ConversationEntity conversationEntity);

    void deleteById(@NotNull Integer conversationId);
}
