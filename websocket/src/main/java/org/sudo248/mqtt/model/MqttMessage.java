package org.sudo248.mqtt.model;

import java.io.Serializable;

public class MqttMessage implements Serializable {
    private static final long serialVersionUID = -3075617026078137668L;
    private final long clientId;
    private final String topic;
    private final MqttMessageType type;
    private final Object payload;

    public MqttMessage(long clientId, String topic, MqttMessageType type, Object payload) {
        this.clientId = clientId;
        this.topic = topic;
        this.type = type;
        this.payload = payload;
    }

    public long getClientId() {
        return clientId;
    }

    public String getTopic() {
        return topic;
    }

    public Object getPayload() {
        return payload;
    }

    public MqttMessageType getType() {
        return type;
    }

    public MqttMessage copy(Object payload) {
        return new MqttMessage(
                clientId,
                topic,
                type,
                payload
        );
    }

    @Override
    public String toString() {
        return "{" + "\n" +
                "clientId: " + clientId + "\n" +
                "topic: " + topic + "\n" +
                "type: " + type + "\n" +
                "payload: " + payload + "\n" +
                "}";
    }
}
