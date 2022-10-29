package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.ContactEntity;
import com.sudo248.ltm.api.model.entities.MessageEntity;
import com.sudo248.ltm.api.service.MessageService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;

@WsController(path = "/message")
public class MessageController implements WebSocketController<Request<MessageEntity>, Response<MessageEntity>> {

    @Autowired
    private MessageService messageService;
    // get all message of one conversation
    @Override
    public void onGet(Request<MessageEntity> request, Response<MessageEntity> response) {
        String conversationId = request.getParams().get("conversationId");

        request.setPayload((MessageEntity) messageService.getAllMessage(Integer.parseInt(conversationId)));
    }

    @Override
    public void onPost(Request<MessageEntity> request, Response<MessageEntity> response) {
        WebSocketController.super.onPost(request, response);
    }

    @Override
    public void onPut(Request<MessageEntity> request, Response<MessageEntity> response) {
        WebSocketController.super.onPut(request, response);
    }

    @Override
    public void onDelete(Request<MessageEntity> request, Response<MessageEntity> response) {
        WebSocketController.super.onDelete(request, response);
    }
}
