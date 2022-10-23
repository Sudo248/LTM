package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.ContactEntity;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;

@WsController(path = "/contact")
public class ContactController implements WebSocketController<Request<ContactEntity>, Response<String>>{
    @Override
    public void onGet(Request request, Response response) {

    }

    @Override
    public void onPost(Request request, Response response) {
        WebSocketController.super.onPost(request, response);
    }

    @Override
    public void onPut(Request request, Response response) {
        WebSocketController.super.onPut(request, response);
    }

    @Override
    public void onDelete(Request request, Response response) {
        WebSocketController.super.onDelete(request, response);
    }
}
