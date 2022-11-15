package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.Application;
import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.ContactType;
import com.sudo248.ltm.api.model.entities.ProfileEntity;
import com.sudo248.ltm.api.model.profile.Profile;
import com.sudo248.ltm.api.service.ContactService;
import com.sudo248.ltm.api.service.ProfileService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@WsController(path = "/profile/get")
public class GetProfileController implements WebSocketController<Request<Profile>, Response<ArrayList<Profile>>> {

    private Logger logger = LoggerFactory.getLogger(GetProfileController.class);

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ContactService contactService;

    @Override
    public void onGet(Request<Profile> request, Response<ArrayList<Profile>> response) {
        Integer userId = Integer.parseInt(request.getParams().get("userId"));
        List<ProfileEntity> listProfileEntity = profileService.getAllProfile();
        ArrayList<Profile> listProfile = new ArrayList<>();
        for (ProfileEntity profileEntity : listProfileEntity) {
            logger.info("userId: " + userId + " profile: " + profileEntity.getUserId());
            if (!Objects.equals(profileEntity.getUserId(), userId)) {
                ContactType contactType = contactService.getContactType(userId, profileEntity.getUserId());
                Boolean isActive = Application.serverSocket.isActive(profileEntity.getUserId());
                logger.info(profileEntity.getName() + " " + isActive);
                listProfile.add(
                        profileEntity.toProfile(contactType == ContactType.FRIEND, isActive)
                );
            }
        }
        response.setPayload(listProfile);
        response.setCode(200);
        response.setMessage("success");
    }
}
