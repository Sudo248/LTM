package com.sudo248.ltm.api.service.impl;

import com.sudo248.ltm.api.model.entities.UserEntity;
import com.sudo248.ltm.api.repository.UserRepository;
import com.sudo248.ltm.api.security.payload.LoginRequest;
import com.sudo248.ltm.api.security.payload.Status;
import com.sudo248.ltm.api.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Status signUp(UserEntity user) {
        Status message = new Status();
        message.setSuccess(false);

        UserEntity userByName = userRepository.findByUsername(user.getUsername());
        UserEntity userByEmail = userRepository.findByEmail(user.getEmail());

        if (userByName != null) {
            message.setMessage("Username da ton tai.");
        } else if (userByEmail != null) {
            message.setMessage("Email da ton tai.");
        } else {
            LocalDate now = LocalDate.now();
            user.setCreatedAt(now);
        }
        userRepository.save(user);
        return message;
    }

    @Override
    public Boolean checkAccount(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        UserEntity user = userRepository.findByUsername(username);
        if (user.getPassword() == loginRequest.getPassword()) {
            return true;
        }
        return false;
    }

    @Override
    public String checkLogin(String username) {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            return "Tài khoản không hợp lệ, vui lòng kiểm tra lại.";
        }
        else {
            return "Mật khẩu chưa chính xác, vui lòng kiểm tra lại.";
        }
    }


}
