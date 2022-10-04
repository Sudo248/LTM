package com.sudo248.ltm.websocket;

import com.sudo248.ltm.api.constants.Const;
import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.RequestMethod;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.websocket.common.WsControllerProvider;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sudo248.WebSocket;
import org.sudo248.handshake.client.ClientHandshake;
import org.sudo248.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WebSocketServerImpl extends WebSocketServer {
    private final Logger log = LoggerFactory.getLogger(WebSocketServerImpl.class);

    public WebSocketServerImpl() {
        super(new InetSocketAddress(Const.WS_HOST, Const.WS_PORT));
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake handshake) {
        log.info("onOpen -> handshake: " + handshake);
    }

    @Override
    public void onClose(WebSocket ws, int code, String reason, boolean remote) {
        log.info("onClose -> code: " + code + "; reason: " + reason + "; remote: " + remote);
    }

    @Override
    public void onMessage(WebSocket ws, String message) {
        log.info("onMessage -> ws: " + ws + "; message: " + message);
    }

    @Override
    public void onMessage(WebSocket ws, Object object) {
        if (object instanceof Request<?>) {
            Request<?> request = (Request<?>) object;
            process(ws, request);
        }
    }

    @Override
    public void onError(WebSocket ws, Exception ex) {
        log.error("onError -> ex: " + ex);
    }

    @Override
    public void onStart() {
        log.info("onStart");
    }

    private void process(WebSocket ws, Request<?> request) {
        RequestMethod method = request.getMethod();
        String path = request.getPath();
        WebSocketController<Request<?>, Response<?>> controller =
                WsControllerProvider.getInstance().getController(path);

        Response<?> response = new Response<>();
        switch (method) {
            case GET:
                controller.onGet(request, response);
                break;
            case POST:
                controller.onPost(request, response);
                break;
            case PUT:
                controller.onPut(request, response);
                break;
            case DELETE:
                controller.onDelete(request, response);
                break;
            default:
        }
        ws.send(response);
    }
}
