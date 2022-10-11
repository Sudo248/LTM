package org.sudo248.mqtt.model;

import java.io.Serializable;

public enum MqttMessageType implements Serializable {
    CONNECT(1),
    PUBLISH(2),
    SUBSCRIBE(3),
    UNSUBSCRIBE(4),
    DISCONNECT(5);

    private final int value;

    MqttMessageType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    MqttMessageType valueOf(int type) {
        switch (type) {
            case 1:
                return CONNECT;
            case 2:
                return PUBLISH;
            case 3:
                return SUBSCRIBE;
            case 4:
                return UNSUBSCRIBE;
            default:
                return DISCONNECT;
        }
    }
}
