package org.sudo248.mqtt.model;

import org.sudo248.WebSocket;

import java.util.Objects;

public class Subscriber {
    private final Long clientId;
    private WebSocket ws;

    public Subscriber(Long clientId) {
        this.clientId = clientId;
        this.ws = null;
    }

    public Subscriber(Long clientId, WebSocket ws) {
        this.clientId = clientId;
        this.ws = ws;
    }

    public Long getClientId() {
        return clientId;
    }

    public WebSocket getWebSocket() {
        return ws;
    }

    public void setWebSocket(WebSocket ws) {
        this.ws = ws;
    }

    public boolean isOpen() {
        return ws != null && ws.isOpen();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscriber that = (Subscriber) o;
        return Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, ws);
    }
}
