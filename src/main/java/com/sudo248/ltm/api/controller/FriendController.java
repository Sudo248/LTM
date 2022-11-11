package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.conversation.Conversation;
import com.sudo248.ltm.api.model.entities.ContactEntity;
import com.sudo248.ltm.api.model.entities.ContactType;
import com.sudo248.ltm.api.model.entities.ConversationEntity;
import com.sudo248.ltm.api.service.ContactService;
import com.sudo248.ltm.api.service.ConversationService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@WsController(path = "/friend")
public class FriendController implements WebSocketController<Request<String>, Response<Conversation>> {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private ContactService contactService;

    @Override
    public void onGet(Request<String> request, Response<Conversation> response) {

    }

    @Override
    public void onPost(Request<String> request, Response<Conversation> response) {
        int userId = Integer.parseInt(request.getParams().get("userId"));
        int friendId = Integer.parseInt(request.getParams().get("friendId"));

        ContactEntity contactEntity = new ContactEntity(
                ContactType.FRIEND,
                userId,
                friendId
        );

        contactService.saveContact(contactEntity);

        ArrayList<Integer> listIds = new ArrayList<>();
        listIds.add(userId);
        listIds.add(friendId);
        ConversationEntity conversationEntity = conversationService.createGroup("P2P", listIds);
        Conversation conversation = conversationEntity.toConversation("Create new conversation");
        response.setPayload(conversation);
        response.setCode(200);
        response.setMessage("success");
    }

    @Override
    public void onPut(Request<String> request, Response<Conversation> response) {


    }

    @Override
    public void onDelete(Request<String> request, Response<Conversation> response) {
        WebSocketController.super.onDelete(request, response);
    }
}
