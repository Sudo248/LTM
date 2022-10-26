package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.auth.Account;
import com.sudo248.ltm.api.model.entities.UserEntity;
import com.sudo248.ltm.api.security.payload.Status;
import com.sudo248.ltm.api.service.LoginService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.annotation.WsPost;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;

@WsController(path = "/user/signup")
public class UserSignUpController implements WebSocketController<Request<Account>, Response<Account>>{

    @Autowired
    private LoginService loginService;
    @Override
    public void onGet(Request<Account> request, Response<Account> response) {

    }

    @Override
    public void onPost(Request<Account> request, Response<Account> response) {
        Account account = request.getPayload();
        UserEntity user = new UserEntity(
                null,
                account.getEmail(),
                account.getPassword(),
                null,
                true
        );
        Status message = loginService.signUp(user);
        if (message.getSuccess()) {
            account.setId(user.getId().longValue());
            account.setCreatedAt(user.getCreatedAt());
            response.setCode(201);
            response.setMessage(message.getMessage());
            response.setPayload(account);
        } else {
            response.setCode(401);
            response.setMessage(message.getMessage());
        }
    }
}
