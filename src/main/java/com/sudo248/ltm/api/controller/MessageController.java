package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.constants.Const;
import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.MessageEntity;
import com.sudo248.ltm.api.model.message.Message;
import com.sudo248.ltm.api.service.ConversationService;
import com.sudo248.ltm.api.service.MessageService;
import com.sudo248.ltm.api.service.ProfileService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WsController(path = "/message")
public class MessageController implements WebSocketController<Request<Message>, Response<ArrayList<Message>>> {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ConversationService conversationService;

    @Override
    public void onGet(Request<Message> request, Response<ArrayList<Message>> response) {
        String conversationId = request.getParams().get("conversationId");
        List<MessageEntity> listMessageEntity =  messageService.getAllMessage(Integer.parseInt(conversationId));
        ArrayList<Message> messages = new ArrayList<>();
        Map<Integer, String> avatarUser = new HashMap<>();
        for (MessageEntity messageEntity : listMessageEntity) {
            if (messageEntity.getSenderId().equals(Const.SERVER_ID)) {
                messages.add(messageEntity.toMessage(Const.IMAGE_USER_DEFAULT));
            } else {
                String urlAvatar = avatarUser.get(messageEntity.getSenderId());
                if (urlAvatar == null) {
                    urlAvatar = profileService.getAvatarUser(messageEntity.getSenderId());
                    avatarUser.put(messageEntity.getSenderId(), urlAvatar);
                }
                messages.add(messageEntity.toMessage(urlAvatar));
            }
        }
        response.setPayload(messages);
        response.setCode(200);
        response.setMessage("success");
    }

    @Override
    public void onPost(Request<Message> request, Response<ArrayList<Message>> response) {

        Message message = request.getPayload();

        MessageEntity messageEntity = messageService.createMessage(MessageEntity.fromMessage(message));
        LoggerFactory.getLogger(MessageController.class).info(message.getConversationId() + " " + message.getSendAt());
//        conversationService.updateTimeConversation(message.getConversationId(), message.getSendAt());
        message.setId(messageEntity.getId());
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        response.setPayload(messages);
        response.setCode(201);
        response.setMessage("success");
    }

    @Override
    public void onPut(Request<Message> request, Response<ArrayList<Message>> response) {
        WebSocketController.super.onPut(request, response);
    }

    @Override
    public void onDelete(Request<Message> request, Response<ArrayList<Message>> response) {
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
