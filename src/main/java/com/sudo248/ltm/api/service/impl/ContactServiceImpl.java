package com.sudo248.ltm.api.service.impl;

import com.sudo248.ltm.api.model.entities.ContactEntity;
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
}
