package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.conversation.Conversation;
import com.sudo248.ltm.api.model.entities.ConversationEntity;
import com.sudo248.ltm.api.model.entities.UserConversationEntity;
import com.sudo248.ltm.api.service.ConversationService;
import com.sudo248.ltm.api.service.MessageService;
import com.sudo248.ltm.api.service.ProfileService;
import com.sudo248.ltm.api.service.UserService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@WsController(path = "conversation/get")
public class GetConversationController implements WebSocketController<Request<Conversation>, Response<ArrayList<Conversation>>> {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private MessageService messageService;

    @Override
    public void onGet(Request<Conversation> request, Response<ArrayList<Conversation>> response) {
        String userId = request.getParams().get("userId");
        List<ConversationEntity> ce = conversationService.getAllByUserId(Integer.parseInt(userId));
        ArrayList<Conversation> arr = new ArrayList<>();

        for (ConversationEntity conversationEntity : ce) {

            Conversation conversation = new Conversation(conversationEntity.getId(),
                    conversationEntity.getName(),
                    conversationEntity.getAvtUrl(),
                    conversationEntity.getType(),
                    conversationEntity.getCreatedAt());

            conversation.setDescription(messageService.getNewMessage(conversationEntity.getId()));

            if (conversation.getType().equals("P2P")) {
                String[] id = "+".split(conversationEntity.getName());

                if (userId.equals(id[0])) {
                    conversation.setName(profileService.getProfileByUserId(Integer.parseInt(id[1])).getName());
                } else conversation.setName(profileService.getProfileByUserId(Integer.parseInt(id[0])).getName());
            }

            arr.add(conversation);
        }

        response.setPayload(arr);
        response.setCode(200);
        response.setMessage("success");
    }

    @Override
    public void onPost(Request<Conversation> request, Response<ArrayList<Conversation>> response) {
        String name = request.getParams().get("name");
        ArrayList<ConversationEntity> conversations = (ArrayList<ConversationEntity>) conversationService.findAllByName(name);

        ArrayList<Conversation> conversation = new ArrayList<>();

        for (ConversationEntity entity : conversations) {
            Conversation conv = new Conversation(
                    entity.getId(),
                    entity.getName(),
                    entity.getAvtUrl(),
                    entity.getType(),
                    entity.getCreatedAt());

            conv.setDescription(messageService.getNewMessage(entity.getId()));
            conversation.add(conv);
        }

        response.setPayload(conversation);
        response.setMessage("success");
        response.setCode(200);
    }

    @Override
    public void onPut(Request<Conversation> request, Response<ArrayList<Conversation>> response) {
        WebSocketController.super.onPut(request, response);
    }

    @Override
    public void onDelete(Request<Conversation> request, Response<ArrayList<Conversation>> response) {
        WebSocketController.super.onDelete(request, response);
    }
}
