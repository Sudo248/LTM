package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.auth.Account;
import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import com.sudo248.ltm.websocket.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@WsController(path = "/login")
public class DemoWsController implements WebSocketController<Request<Account>, Response<Account>> {
    @Override
    public void onGet(Request<Account> request, Response<Account> response) {
        Account account = request.getPayload();
        System.out.println(account.getEmail());
        System.out.println(account.getPassword());

        account.setId(123L);

        response.setCode(200);
        response.setMessage("success");
        response.setPayload(account);
    }
}
