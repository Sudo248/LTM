package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.ProfileEntity;
import com.sudo248.ltm.api.service.ProfileService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
@WsController(path = "/profile")
public class ProfileController implements WebSocketController<Request<ProfileEntity>, Response<ProfileEntity>> {

    @Autowired
    private ProfileService profileService;

    @Override
    public void onGet(Request<ProfileEntity> request, Response<ProfileEntity> response) {
        String userId = request.getParams().get("userid");
        response.setPayload(profileService.getProfileByUserId(Integer.parseInt(userId)));
    }

    @Override
    public void onPost(Request<ProfileEntity> request, Response<ProfileEntity> response) {
        String name = request.getParams().get("name");
        //response.setPayload(profileService.findProfileByName(name).);
    }

    @Override
    public void onPut(Request<ProfileEntity> request, Response<ProfileEntity> response) {
        response.setPayload(profileService.update(request.getPayload()));
    }

    @Override
    public void onDelete(Request<ProfileEntity> request, Response<ProfileEntity> response) {
        WebSocketController.super.onDelete(request, response);
    }
}
