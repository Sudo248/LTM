package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.UserConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConversationRepository extends JpaRepository<UserConversationEntity, Integer> {
}
