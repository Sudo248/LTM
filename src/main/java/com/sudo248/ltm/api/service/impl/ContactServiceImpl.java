package com.sudo248.ltm.api.service.impl;

import com.sudo248.ltm.api.model.entities.ContactEntity;
import com.sudo248.ltm.api.model.entities.ContactType;
import com.sudo248.ltm.api.repository.ContactRepository;
import com.sudo248.ltm.api.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {
    @Autowired
    private ContactRepository contactRepository;
    @Override
    public List<ContactEntity> getAllContact(Integer userId) {
        return contactRepository.getAllByUserId(userId);
    }

    @Override
    public List<ContactEntity> getAllFriend(Integer userId) {
        return contactRepository.getAllFriendByUserId(userId);
    }

    @Override
    public ContactType getContactType(Integer userId, Integer friendId) {
        ContactType contactType = contactRepository.getContactType(userId, friendId);
        if (contactType == null) {
            return ContactType.STRANGER;
        } else {
            return contactType;
        }
    }

    @Override
    public ContactEntity saveContact(ContactEntity contactEntity) {
        return contactRepository.save(contactEntity);
    }
}
