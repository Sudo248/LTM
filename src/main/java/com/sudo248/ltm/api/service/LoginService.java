package com.sudo248.ltm.api.service;

import com.sudo248.ltm.api.model.entities.UserEntity;
import com.sudo248.ltm.api.security.payload.LoginRequest;
import com.sudo248.ltm.api.security.payload.Status;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {

    Status signUp(UserEntity user);

    Boolean checkAccount(LoginRequest loginRequest);

    String checkLogin(String username);
}
