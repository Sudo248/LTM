package org.sudo248.mqtt;

public interface Mqtt {
    void publish(String topic, Object message);
    void subscribe(String topic);

    void unsubscribe(String topic);
    void connectMqtt(Long clientId);
}
