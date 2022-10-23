package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.UserEntity;
import com.sudo248.ltm.api.security.payload.LoginRequest;
import com.sudo248.ltm.api.security.payload.Status;
import com.sudo248.ltm.api.service.LoginService;
import com.sudo248.ltm.api.service.UserService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.annotation.WsPost;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

@WsController (path = "/user/login")
public class UserLoginController implements WebSocketController<Request<LoginRequest>, Response<String>> {

    @Autowired
    LoginService loginService;

    @Override
    public void onPost(Request<LoginRequest> request, Response<String> response) {
        String message = "";
        try {
            if (loginService.checkAccount((LoginRequest) request.getParams())) {
                message = "Đăng nhập thành công.";
            }
        }
        catch (Exception e) {
            message = loginService.checkLogin(request.getParams().get("username"));
        }
        response.setPayload(message);
    }

    @WsPost(path = "/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        String message = "";
        try {
            if (loginService.checkAccount(loginRequest)) {
                message = "Đăng nhập thành công.";
            }
        }
        catch (Exception e) {
            message = loginService.checkLogin(loginRequest.getUsername());
        }
        return message;
    }

    @Override
    public void onGet(Request<LoginRequest> request, Response<String> response) {

    }



    @Override
    public void onPut(Request<LoginRequest> request, Response<String> response) {
        WebSocketController.super.onPut(request, response);
    }

    @Override
    public void onDelete(Request<LoginRequest> request, Response<String> response) {
        WebSocketController.super.onDelete(request, response);
    }
}
