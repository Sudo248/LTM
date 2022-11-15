package com.sudo248.ltm.api.repository;

import com.sudo248.ltm.api.model.entities.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {

    @Query(value = "SELECT * FROM profile WHERE user_id = :userId", nativeQuery = true)
    ProfileEntity getProfileByUserId(@Param("userId") Integer userId);

//    ProfileEntity findByUserId(Integer id);

    @Query(value = "SELECT * FROM profile WHERE name LIKE %:username%", nativeQuery = true)
    List<ProfileEntity> findAllByName(@Param("username") String username);

}
