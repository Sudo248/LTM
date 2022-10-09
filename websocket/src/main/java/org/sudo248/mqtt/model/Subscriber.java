package org.sudo248.mqtt.model;

import org.sudo248.WebSocket;

import java.util.Objects;

public class Subscriber {
    private final Long clientId;
    private WebSocket ws;
    private boolean isValid;

    public Subscriber(Long clientId) {
        this.clientId = clientId;
    }

    public Subscriber(Long clientId, WebSocket ws) {
        this.clientId = clientId;
        this.ws = ws;
    }

    public Subscriber(Long clientId, WebSocket ws, boolean isValid) {
        this.clientId = clientId;
        this.ws = ws;
        this.isValid = isValid;
    }

    public Subscriber(Long clientId, boolean isValid) {
        this.clientId = clientId;
        this.isValid = isValid;
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

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscriber that = (Subscriber) o;
        return clientId == that.clientId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, ws, isValid);
    }
}
