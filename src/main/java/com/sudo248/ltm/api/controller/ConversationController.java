package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.conversation.Conversation;
import com.sudo248.ltm.api.model.conversation.ConversationType;
import com.sudo248.ltm.api.model.entities.ConversationEntity;
import com.sudo248.ltm.api.model.entities.ProfileEntity;
import com.sudo248.ltm.api.service.ConversationService;
import com.sudo248.ltm.api.service.MessageService;
import com.sudo248.ltm.api.service.ProfileService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@WsController(path = "/conversation")
public class ConversationController implements WebSocketController<Request<Conversation>, Response<Conversation>> {

    Logger logger = LoggerFactory.getLogger(ConversationController.class);

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ProfileService profileService;

    @Override
    public void onGet(Request<Conversation> request, Response<Conversation> response) {

        String conversationId = request.getParams().get("conversationId");
        ConversationEntity conversationEntity = conversationService.getConversationById(Integer.parseInt(conversationId));

        logger.info("conversationEntity: " + conversationEntity);

        Conversation conversation = new Conversation(
                conversationEntity.getId(),
                conversationEntity.getName(),
                conversationEntity.getAvtUrl(),
                conversationEntity.getType(),
                conversationEntity.getCreatedAt()
        );
        if (conversationEntity.getType() == ConversationType.P2P) {
            String[] id = conversationEntity.getName().split("-");
            Integer userId = Integer.parseInt(request.getParams().get("userId"));
            if (userId.equals(id[0])) {
                ProfileEntity profileEntity = profileService.getProfileByUserId(Integer.parseInt(id[1]));
                conversation.setName(profileEntity.getName());
                conversation.setAvtUrl(profileEntity.getImage());
            } else {
                ProfileEntity profileEntity = profileService.getProfileByUserId(Integer.parseInt(id[0]));
                conversation.setName(profileEntity.getName());
                conversation.setAvtUrl(profileEntity.getImage());
            }
        }

        conversation.setDescription(messageService.getNewMessage(conversationEntity.getId()));

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

