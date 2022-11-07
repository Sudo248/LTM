package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.conversation.Conversation;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;

@WsController(path="/friend")
public class FriendController implements WebSocketController<Request<String>, Response<Conversation>> {



    @Override
    public void onGet(Request<String> request, Response<Conversation> response) {

    }

    @Override
    public void onPost(Request<String> request, Response<Conversation> response) {
        int userId = Integer.parseInt(request.getParams().get("userId"));
        int friendId = Integer.parseInt(request.getParams().get("friendId"));

    }

    @Override
    public void onPut(Request<String> request, Response<Conversation> response) {



    }

    @Override
    public void onDelete(Request<String> request, Response<Conversation> response) {
        WebSocketController.super.onDelete(request, response);
    }
}
