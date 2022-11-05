package com.sudo248.ltm.api.service;


import com.sudo248.ltm.api.model.entities.UserConversationEntity;

import java.util.List;

public interface UserConversationService {

    List<UserConversationEntity> getAllByUserId(Integer userId);

}
