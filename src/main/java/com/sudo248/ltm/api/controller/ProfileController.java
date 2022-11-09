package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.ProfileEntity;
import com.sudo248.ltm.api.model.profile.Profile;
import com.sudo248.ltm.api.service.ContactService;
import com.sudo248.ltm.api.service.ProfileService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
@WsController(path = "/profile")
public class ProfileController implements WebSocketController<Request<Profile>, Response<Profile>> {

    private Logger logger = LoggerFactory.getLogger(ProfileController.class);
    @Autowired
    private ProfileService profileService;

    @Autowired
    private ContactService contactService;

    @Override
    public void onGet(Request<Profile> request, Response<Profile> response) {
        Integer userId = Integer.parseInt(request.getParams().get("userId"));
        logger.info("userId: " + userId);
        ProfileEntity profileEntity = profileService.getProfileByUserId(userId);
        logger.info("ProfileEntity: " + profileEntity);
        Profile profile = profileEntity.toProfile(true);
        response.setPayload(profile);
        response.setCode(200);
        response.setMessage("success");
    }

    @Override
    public void onPost(Request<Profile> request, Response<Profile> response) {
        String name = request.getParams().get("name");
        //response.setPayload(profileService.findProfileByName(name).);
    }

    @Override
    public void onPut(Request<Profile> request, Response<Profile> response) {
//        response.setPayload(profileService.update(request.getPayload()));
    }

    @Override
    public void onDelete(Request<Profile> request, Response<Profile> response) {
        WebSocketController.super.onDelete(request, response);
    }
}
