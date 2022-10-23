package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.UserEntity;
import com.sudo248.ltm.api.security.payload.Status;
import com.sudo248.ltm.api.service.LoginService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.annotation.WsPost;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

@WsController(path = "/user/signup")
public class UserSignUpController implements WebSocketController<Request<UserEntity>, Response<Status>>{

    @Autowired
    private LoginService loginService;
    @Override
    public void onGet(Request<UserEntity> request, Response<Status> response) {
        UserEntity user = (UserEntity) request.getParams();
        Status message = loginService.signUp(user);
        response.setPayload(message);
    }

    @WsPost(path = "/signup")
    public Status signUp(@RequestBody UserEntity user) {
        return loginService.signUp(user);
    }
}
