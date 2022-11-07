package com.sudo248.ltm.api.controller;
import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.service.ProfileService;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;

@WsController(path = "profile/avatar")
public class GetAvatarUserController implements WebSocketController<Request<String>, Response<String>> {

    @Autowired
    ProfileService profileService;
    // get avatar user
    @Override
    public void onGet(Request<String> request, Response<String> response) {
        String userId = request.getParams().get("userId");
        response.setPayload(profileService.getProfileByUserId(Integer.parseInt(userId)).getImage());
        response.setCode(200);
        response.setMessage("success");
    }

    @Override
    public void onPost(Request<String> request, Response<String> response) {
        WebSocketController.super.onPost(request, response);
    }

    @Override
    public void onPut(Request<String> request, Response<String> response) {
        WebSocketController.super.onPut(request, response);
    }

    @Override
    public void onDelete(Request<String> request, Response<String> response) {
        WebSocketController.super.onDelete(request, response);
    }
}
