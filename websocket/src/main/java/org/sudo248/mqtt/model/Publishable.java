package org.sudo248.mqtt.model;

public interface Publishable {
    void publish(MqttMessage message);
}
