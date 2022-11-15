package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.ContactEntity;
import com.sudo248.ltm.api.model.entities.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Integer> {

    List<ContactEntity> getAllByUserId(Integer userId);

    @Query(value = "SELECT * FROM contact c WHERE c.user_id = :userId and c.contact_type = FRIEND", nativeQuery = true)
    List<ContactEntity> getAllFriendByUserId(@Param("userId") Integer userId);

    @Query(value = "SELECT contact_type FROM contact WHERE (contact.user_id = :userId AND contact.friend_id = :friendId) OR (contact.user_id = :friendId AND contact.friend_id = :userId)", nativeQuery = true)
    ContactType getContactType(@Param("userId") Integer userId, @Param("friendId") Integer fiendId);
}
