package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.ContactEntity;
import com.sudo248.ltm.api.model.entities.UserConversationEntity;
import com.sudo248.ltm.api.service.UserConversationService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@WsController(path = "/user_conversation")
public class UserConversationController implements WebSocketController<Request<Integer>, Response<UserConversationEntity>> {

    @Autowired
    private UserConversationService userConversationService;
    // lay cac phong dang chat cua user
    @Override
    public void onGet(Request<Integer> request, Response<UserConversationEntity> response) {
        String userId = request.getParams().get("userId");
        response.setPayload((UserConversationEntity) userConversationService.getAllByUserId(Integer.parseInt(userId)));
    }

    @Override
    public void onPost(Request<Integer> request, Response<UserConversationEntity> response) {
        WebSocketController.super.onPost(request, response);
    }

    @Override
    public void onPut(Request<Integer> request, Response<UserConversationEntity> response) {
        WebSocketController.super.onPut(request, response);
    }

    @Override
    public void onDelete(Request<Integer> request, Response<UserConversationEntity> response) {
        WebSocketController.super.onDelete(request, response);
    }
}
