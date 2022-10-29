package com.sudo248.ltm.api.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entities.ContactEntity;
import com.sudo248.ltm.websocket.annotation.WsController;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.hibernate.mapping.Array;

import java.util.ArrayList;

@WsController(path = "/contact")
public class ContactController implements WebSocketController<Request<ContactEntity>, Response<ArrayList<ContactEntity>>>{
    @Override
    public void onGet(Request<ContactEntity> request, Response<ArrayList<ContactEntity>> response) {

    }

    @Override
    public void onPost(Request<ContactEntity> request, Response<ArrayList<ContactEntity>> response) {
        WebSocketController.super.onPost(request, response);
    }

    @Override
    public void onPut(Request<ContactEntity> request, Response<ArrayList<ContactEntity>> response) {
        WebSocketController.super.onPut(request, response);
    }

    @Override
    public void onDelete(Request<ContactEntity> request, Response<ArrayList<ContactEntity>> response) {
        WebSocketController.super.onDelete(request, response);
    }
    // get all contact


}
