package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.UserConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserConversationRepository extends JpaRepository<UserConversationEntity, Integer> {

    List<UserConversationEntity> getAllByUserId(Integer userId);

    UserConversationEntity getByConversationId(Integer conversationId);

    @Query(value = "SELECT uc.user_id FROM user_conversation uc WHERE conversation_id = :conversationId", nativeQuery = true)
    List<Integer> getUserIdFromConversationId(@Param("conversationId") Integer conversationId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_conversation WHERE conversation_id = :conversationId AND user_id = :userId", nativeQuery = true)
    void deleteByUserIdAndConversationId(@Param("userId") Integer userId, @Param("conversationId") Integer conversationId);
}
