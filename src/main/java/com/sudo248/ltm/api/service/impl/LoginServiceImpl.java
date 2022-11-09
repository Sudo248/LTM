package com.sudo248.ltm.api.service.impl;

import com.sudo248.ltm.api.constants.Const;
import com.sudo248.ltm.api.model.entities.ProfileEntity;
import com.sudo248.ltm.api.model.entities.UserEntity;
import com.sudo248.ltm.api.repository.ProfileRepository;
import com.sudo248.ltm.api.repository.UserRepository;
import com.sudo248.ltm.api.security.payload.LoginRequest;
import com.sudo248.ltm.api.security.payload.Status;
import com.sudo248.ltm.api.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Override
    public Status signUp(UserEntity user) {
        Status message = new Status();
        message.setSuccess(false);
        UserEntity userByEmail = userRepository.findByEmail(user.getEmail());
        if (userByEmail != null) {
            message.setMessage("Email đã tồn tại.");
            return message;
        } else {
            user.setCreatedAt(LocalDate.now());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        UserEntity userEntity = userRepository.save(user);

        ProfileEntity profileEntity = new ProfileEntity(
                null,
                "Description for you",
                userEntity.getEmail(),
                Const.IMAGE_USER_DEFAULT,
                true,
                userEntity.getId()
        );
        profileRepository.save(profileEntity);

        message.setSuccess(true);
        message.setMessage("Tạo tài khoảnh thành công.");
        return message;
    }

    @Override
    public Integer checkAccount(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        UserEntity user = userRepository.findByEmail(email);
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            return user.getId();
        }
        return null;
    }

    @Override
    public String checkLogin(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            return "Tài khoản không hợp lệ, vui lòng kiểm tra lại.";
        }
        else {
            return "Mật khẩu chưa chính xác, vui lòng kiểm tra lại.";
        }
    }
}
