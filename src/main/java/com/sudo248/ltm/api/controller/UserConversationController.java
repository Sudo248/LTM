package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.ContactEntity;
import com.sudo248.ltm.api.model.entities.UserConversationEntity;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;

@WsController
public class UserConversationController implements WebSocketController<Request<UserConversationEntity>, Response<String>> {
    @Override
    public void onGet(Request<UserConversationEntity> request, Response<String> response) {

    }

    @Override
    public void onPost(Request<UserConversationEntity> request, Response<String> response) {
        WebSocketController.super.onPost(request, response);
    }

    @Override
    public void onPut(Request<UserConversationEntity> request, Response<String> response) {
        WebSocketController.super.onPut(request, response);
    }

    @Override
    public void onDelete(Request<UserConversationEntity> request, Response<String> response) {
        WebSocketController.super.onDelete(request, response);
    }
}
