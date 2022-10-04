package com.sudo248.ltm.websocket.controller;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;

/**
 * each WebSocketController has 4 method request GET, POST, PUT, DELETE
 * call method by the <RequestMethod> in <Request>
 * <Response> that server send to client
 *
 * @param <REQ>
 * @param <RES>
 */
public interface WebSocketController<REQ extends Request<?>, RES extends Response<?>> {
    void onGet(final REQ request, final RES response);

    default void onPost(final REQ request, final RES response) {

    }

    default void onPut(final REQ request, final RES response) {

    }

    default void onDelete(final REQ request, final RES response) {

    }
}
