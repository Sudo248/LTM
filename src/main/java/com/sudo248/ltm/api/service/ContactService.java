package com.sudo248.ltm.api.service;

import com.sudo248.ltm.api.model.entities.ContactEntity;

import java.util.List;

public interface ContactService {

    List<ContactEntity> getAllContact(Integer userId);
    List<ContactEntity> getAllFriend(Integer userId);


}
