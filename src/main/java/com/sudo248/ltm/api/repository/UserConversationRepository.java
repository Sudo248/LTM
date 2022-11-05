package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.UserConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository
public interface UserConversationRepository extends JpaRepository<UserConversationEntity, Integer> {

    List<UserConversationEntity> getAllByUserId(Integer userId);

    UserConversationEntity getByUserIdOrConversationId(Integer userId, Integer conversationId);


}
