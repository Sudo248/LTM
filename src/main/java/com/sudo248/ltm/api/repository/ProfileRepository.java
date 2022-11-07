package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {

    ProfileEntity getProfileByUserId(Integer userId);

//    ProfileEntity findByUserId(Integer id);

    List<ProfileEntity> findAllByName(String username);

}
