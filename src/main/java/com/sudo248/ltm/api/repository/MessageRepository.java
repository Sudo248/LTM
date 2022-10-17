package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {
}
