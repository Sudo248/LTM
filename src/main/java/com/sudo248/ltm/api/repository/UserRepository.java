package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.ConversationEntity;
import com.sudo248.ltm.api.model.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findByEmail(String email);

    @Query(value = "select u.id from user u join user_conversation uc on u.id = uc.user_id where uc.conversation_id = :conversationId", nativeQuery = true)
    List<Integer> getAllByConversationId(Integer conversationId);
}
