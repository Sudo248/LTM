package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.ProfileEntity;
import com.sudo248.ltm.api.model.profile.Profile;
import com.sudo248.ltm.api.service.ProfileService;
import com.sudo248.ltm.api.service.UserService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@WsController(path = "/profile/conversation")
public class GetAllProfileOfConversationController implements WebSocketController<Request<String>, Response<ArrayList<Profile>>> {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserService userService;

    @Override
    public void onGet(Request<String> request, Response<ArrayList<Profile>> response) {
        String conversationId = request.getParams().get("conversationId");
        List<Integer> userId = userService.getAllUserIdOfOneConversation(Integer.parseInt(conversationId));
        ArrayList<Profile> arr = new ArrayList<>();
        List<ProfileEntity> profileEntityList = new ArrayList<>();
        for (Integer uid : userId) {
            profileEntityList.add(profileService.getProfileByUserId(uid));
        }

        for (ProfileEntity profileEntity : profileEntityList) {
            arr.add(profileEntity.toProfile(false));
        }

        response.setPayload(arr);
        response.setCode(200);
        response.setMessage("success");
    }

    @Override
    public void onPost(Request<String> request, Response<ArrayList<Profile>> response) {
        WebSocketController.super.onPost(request, response);
    }

    @Override
    public void onPut(Request<String> request, Response<ArrayList<Profile>> response) {
        WebSocketController.super.onPut(request, response);
    }

    @Override
    public void onDelete(Request<String> request, Response<ArrayList<Profile>> response) {
        WebSocketController.super.onDelete(request, response);
    }
}
