package com.sudo248.ltm.frontend.websocket;

import com.sudo248.ltm.api.constants.Const;
import com.sudo248.ltm.api.model.Request;
import org.sudo248.client.WebSocketClient;
import org.sudo248.handshake.server.ServerHandshake;
import org.sudo248.mqtt.model.MqttMessage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Queue;

public class WebSocketClientImpl extends WebSocketClient {

    private Queue<Object> startRequest;

    public WebSocketClientImpl() throws URISyntaxException {
        super(new URI("ws://" + Const.WS_HOST + ":" + Const.WS_PORT));
        startRequest = new LinkedList<>();
    }

    @Override
    public void send(Object object) {
        if (!isOpen()) {
            startRequest.add(object);
        } else {
            super.send(object);
        }
    }

    @Override
    public void send(String text) {
        if (!isOpen()) {
            startRequest.add(text);
        } else {
            super.send(text);
        }
    }

    @Override
    public void send(byte[] data) {
        if (!isOpen()) {
            startRequest.add(data);
        } else {
            super.send(data);
        }
    }

    @Override
    public void publish(String topic, Object message) {
        if (!isOpen()) {
            startRequest.add(message);
        } else {
            super.publish(topic, message);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        while (!startRequest.isEmpty()) {
            Object request = startRequest.poll();
            if (request instanceof MqttMessage) {
                publish(((MqttMessage) request).getTopic(), request);
            } if (request instanceof byte[]) {
                super.send((byte[])request);
            } if (request instanceof String) {
                super.send((String)request);
            } else {
                super.send(request);
            }
        }
        startRequest = null;
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onMessage(Object object) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }

    @Override
    public void onMqttPublish(MqttMessage message) {

    }
}
