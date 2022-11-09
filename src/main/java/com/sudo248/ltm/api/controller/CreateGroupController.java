package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.conversation.Conversation;
import com.sudo248.ltm.api.model.entities.ConversationEntity;
import com.sudo248.ltm.api.service.ConversationService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@WsController(path = "/conversation/create_group")
public class CreateGroupController implements WebSocketController<Request<ArrayList<Integer>>, Response<Conversation>> {

    @Autowired
    private ConversationService conversationService;

    @Override
    public void onGet(Request<ArrayList<Integer>> request, Response<Conversation> response) {

    }

    @Override
    public void onPost(Request<ArrayList<Integer>> request, Response<Conversation> response) {
        ArrayList<Integer> id = request.getPayload();
        ConversationEntity conversationEntity = conversationService.createGroup(id);
        Conversation conversation = conversationEntity.toConversation("Create new Conversation");
        response.setPayload(conversation);
        response.setCode(201);
        response.setMessage("success");
    }
}
