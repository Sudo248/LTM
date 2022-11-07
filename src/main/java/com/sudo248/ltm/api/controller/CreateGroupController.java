package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.ConversationEntity;
import com.sudo248.ltm.api.service.ConversationService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@WsController(path = "conversation/create_group")
public class CreateGroupController implements WebSocketController<Request<ArrayList<String>>, Response<ConversationEntity>>
{

    @Autowired
    private ConversationService conversationService;

    @Override
    public void onGet(Request<ArrayList<String>> request, Response<ConversationEntity> response) {

    }

    @Override
    public void onPost(Request<ArrayList<String>> request, Response<ConversationEntity> response) {
        ArrayList<String> id = request.getPayload();
        response.setPayload(conversationService.createGroup(id));
        response.setCode(201);
        response.setMessage("success");
    }

    @Override
    public void onPut(Request<ArrayList<String>> request, Response<ConversationEntity> response) {
        WebSocketController.super.onPut(request, response);
    }

    @Override
    public void onDelete(Request<ArrayList<String>> request, Response<ConversationEntity> response) {
        WebSocketController.super.onDelete(request, response);
    }
}
