package com.sudo248.ltm.websocket;

import com.sudo248.ltm.constants.Const;
import com.sudo248.ltm.model.Method;
import com.sudo248.ltm.model.Request;
import com.sudo248.ltm.model.entity.user.User;
import com.sudo248.ltm.model.entity.user.UserRequest;
import com.sudo248.ltm.model.entity.user.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.sudo248.WebSocket;
import org.sudo248.handshake.client.ClientHandshake;
import org.sudo248.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WebSocketServerImpl extends WebSocketServer {


    public WebSocketServerImpl() {
        super(new InetSocketAddress(Const.WS_HOST, Const.WS_PORT));
    }

    public WebSocketServerImpl(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake handshake) {

    }

    @Override
    public void onClose(WebSocket ws, int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(WebSocket ws, String message) {
//        System.out.println("Receive: " + message);
//        UserRequest request = UserRequest.fromJson(message);
//        UserResponse response = new UserResponse();
//        if (request.getMethod().equals(Method.GET)) {
//            response.setCode(200);
//            response.setUser(request.getUser());
//            response.setError(request.getPath());
//        }
//        ws.send(response.toJson());
        System.out.println(message);
    }

    @Override
    public void onMessage(WebSocket ws, Object object) {
        Request<User> request = (Request<User>) object;
        System.out.println(request);
        System.out.println(request.getPayload());
    }

    @Override
    public void onError(WebSocket ws, Exception ex) {

    }

    @Override
    public void onStart() {

    }

}
