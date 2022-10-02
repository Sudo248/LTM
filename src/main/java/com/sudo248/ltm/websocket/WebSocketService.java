package com.sudo248.ltm.websocket;

public interface WebSocketService<REQ extends Object, RES extends Object> {
    String path = "/";

    RES onGet(REQ request, RES response);

    RES onPost(REQ request, RES response);

    default RES onPut(REQ request, RES response) {
        return null;
    }

    default RES onDelete(REQ request, RES response) {
        return null;
    }
}
