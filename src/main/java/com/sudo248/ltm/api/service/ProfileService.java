package com.sudo248.ltm.api.service;

import com.sudo248.ltm.api.model.entities.ContactType;
import com.sudo248.ltm.api.model.entities.ProfileEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.util.List;

@Service
public interface ProfileService {

    List<ProfileEntity> getAllProfile();

    ProfileEntity getProfileByUserId(Integer userId);

    //List<ProfileEntity> getProfileOfConversationByConversationId(Integer conversationId);

    ProfileEntity update(ProfileEntity profileEntity);

    String getAvatarUser(Integer userId);

}
