package com.sudo248.ltm.websocket.common;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.websocket.controller.WebSocketController;

import java.util.HashMap;
import java.util.Map;

/**
 * store list of WebSocketController that has a path
 */
public class WsControllerProvider {
    private static WsControllerProvider INSTANCE = null;

    public static WsControllerProvider getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new WsControllerProvider();
        }
        return INSTANCE;
    }

    private WsControllerProvider(){}

    private final Map<String, WebSocketController<Request<?>, Response<?>>> wsController = new HashMap<>();

    public void putController(String key, WebSocketController<Request<?>, Response<?>> controller) {
        wsController.put(key, controller);
    }

    public WebSocketController<Request<?>, Response<?>> getController(String key) {
        return wsController.get(key);
    }
}
