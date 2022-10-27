package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Integer> {

    List<ContactEntity> getAllByUserId(Integer userId);

    List<ContactEntity> getAllFriendByUserId(Integer userId);
}
