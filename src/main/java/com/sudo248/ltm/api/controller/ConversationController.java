package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.conversation.Conversation;
import com.sudo248.ltm.api.model.entities.ConversationEntity;
import com.sudo248.ltm.api.service.ConversationService;
import com.sudo248.ltm.api.service.MessageService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;

@WsController(path = "/conversation")
public class ConversationController implements WebSocketController<Request<Conversation>, Response<Conversation>> {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MessageService messageService;

    @Override
    public void onGet(Request<Conversation> request, Response<Conversation> response) {
        String conversationId = request.getParams().get("conversationId");

        ConversationEntity conversationEntity = conversationService.getConversationById(Integer.parseInt(conversationId));

        Conversation conversation = new Conversation(Integer.parseInt(conversationId),
                conversationEntity.getName(),
                conversationEntity.getAvtUrl(),
                conversationEntity.getType(),
                conversationEntity.getCreatedAt());

        conversation.setDescription(messageService.getNewMessage(Integer.parseInt(conversationId)));

        response.setPayload(conversation);

        response.setCode(200);
        response.setMessage("success");

    }

    @Override
    public void onPost(Request<Conversation> request, Response<Conversation> response) {
        WebSocketController.super.onPost(request, response);
    }

    @Override
    public void onPut(Request<Conversation> request, Response<Conversation> response) {
        Conversation conversation = request.getPayload();
        String conversationId = request.getParams().get("conversationId");
        ConversationEntity conversationEntity = conversationService.getConversationById(Integer.parseInt(conversationId));

        conversationEntity.setName(conversation.getName());
        conversationEntity.setAvtUrl(conversation.getAvtUrl());

        response.setPayload(conversation);
        response.setCode(200);
        response.setMessage("success");
    }

    @Override
    public void onDelete(Request<Conversation> request, Response<Conversation> response) {
        String conversationId = request.getParams().get("conversationId");
        conversationService.delete(Integer.parseInt(conversationId));
        response.setPayload(null);
        response.setCode(200);
        response.setMessage("success");
    }
}

