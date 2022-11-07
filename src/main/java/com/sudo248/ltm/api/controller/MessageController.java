package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.ContactEntity;
import com.sudo248.ltm.api.model.entities.MessageEntity;
import com.sudo248.ltm.api.service.MessageService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@WsController(path = "/message")
public class MessageController implements WebSocketController<Request<String>, Response<ArrayList<MessageEntity>>> {

    @Autowired
    private MessageService messageService;

    @Override
    public void onGet(Request<String> request, Response<ArrayList<MessageEntity>> response) {
        String conversationId = request.getParams().get("conversationId");
        response.setPayload((ArrayList<MessageEntity>) messageService.getAllMessage(Integer.parseInt(conversationId)));
        response.setCode(200);
        response.setMessage("success");
    }

    @Override
    public void onPost(Request<String> request, Response<ArrayList<MessageEntity>> response) {
        WebSocketController.super.onPost(request, response);

    }

    @Override
    public void onPut(Request<String> request, Response<ArrayList<MessageEntity>> response) {
        WebSocketController.super.onPut(request, response);
    }

    @Override
    public void onDelete(Request<String> request, Response<ArrayList<MessageEntity>> response) {
        WebSocketController.super.onDelete(request, response);
    }
    // get all message of one conversation
//    @Override
//    public void onGet(Request<MessageEntity> request, Response<MessageEntity> response) {
//        String conversationId = request.getParams().get("conversationId");
//
//        request.setPayload((MessageEntity) messageService.getAllMessage(Integer.parseInt(conversationId)));
//    }
}
