package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.ConversationEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, Integer> {

    @Query(value = "select * from conversation c join user_conversation uc on c.id = uc.conversation_id where uc.user_id = :userId", nativeQuery = true)
    List<ConversationEntity> getAllByUserId(@Param("userId") Integer userId);

//    @Query(value = "select * from conversation c where c.name like :name", nativeQuery = true)
//    List<ConversationEntity> findAllByName(@Param("name") String name);

    List<ConversationEntity> findAllByName( String name);

    ConversationEntity getById(Integer conversationId);

    //ConversationEntity update(ConversationEntity conversationEntity);

    void deleteById(@NotNull Integer conversationId);
}
