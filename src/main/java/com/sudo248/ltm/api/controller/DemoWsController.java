package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entity.user.User;
import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import com.sudo248.ltm.websocket.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@WsController(path = "/user")
public class DemoWsController implements WebSocketController<Request<String>, Response<User>> {
    @Override
    public void onGet(Request<String> request, Response<User> response) {
        System.out.println(request);

        response.setCode(200);
        response.setMessage("success");
        response.setPayload(new User(
                123L,
                "Le Hong Duong",
                "24/08/2001"
        ));
    }

    @Override
    public void onPost(Request<String> request, Response<User> response) {

    }

    @Override
    public void onPut(Request<String> request, Response<User> response) {
    }

    @Override
    public void onDelete(Request<String> request, Response<User> response) {
    }

}
