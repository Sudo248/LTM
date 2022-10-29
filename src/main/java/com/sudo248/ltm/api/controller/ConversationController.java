package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.ContactEntity;
import com.sudo248.ltm.api.model.entities.ConversationEntity;
import com.sudo248.ltm.api.service.ConversationService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;

@WsController(path = "/conversation")
public class ConversationController implements WebSocketController<Request<ConversationEntity>, Response<ConversationEntity>> {

    @Autowired
    private ConversationService conversationService;

    @Override
    public void onGet(Request<ConversationEntity> request, Response<ConversationEntity> response) {
        String conversationId = request.getParams().get("conversationId");
        response.setCode(200);
        response.setMessage("success");
        response.setPayload(conversationService.getConversationById(Integer.parseInt(conversationId)));
    }

    @Override
    public void onPost(Request<ConversationEntity> request, Response<ConversationEntity> response) {
        WebSocketController.super.onPost(request, response);
    }

    @Override
    public void onPut(Request<ConversationEntity> request, Response<ConversationEntity> response) {
        ConversationEntity conversation = request.getPayload();
        response.setPayload(conversationService.update(conversation));
        response.setCode(200);
        response.setMessage("success");
    }

    @Override
    public void onDelete(Request<ConversationEntity> request, Response<ConversationEntity> response) {
        String conversationId = request.getParams().get("conversationId");
        conversationService.delete(Integer.parseInt(conversationId));
        response.setPayload(null);
        response.setCode(200);
        response.setMessage("success");
    }
}
