package com.sudo248.ltm.api.service.impl;

import com.sudo248.ltm.api.model.entities.ProfileEntity;
import com.sudo248.ltm.api.repository.ProfileRepository;
import com.sudo248.ltm.api.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public List<ProfileEntity> getAllProfile() {
        return profileRepository.findAll();
    }

    @Override
    public ProfileEntity getProfileByUserId(Integer userId) {
        return profileRepository.findByUserId(userId);
    }

    @Override
    public List<ProfileEntity> findProfileByName(String name) {
        name = name.trim();
        return profileRepository.findAllByName(name);

    }

    @Override
    public ProfileEntity update(ProfileEntity profileEntity) {
        return profileRepository.save(profileEntity);
    }
}
