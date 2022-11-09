package com.sudo248.ltm.api.service;

import com.sudo248.ltm.api.model.entities.ContactEntity;
import com.sudo248.ltm.api.model.entities.ContactType;

import java.util.List;

public interface ContactService {

    List<ContactEntity> getAllContact(Integer userId);
    List<ContactEntity> getAllFriend(Integer userId);

    ContactType getContactType(Integer userId, Integer friendId);

    ContactEntity saveContact(ContactEntity contactEntity);
}
