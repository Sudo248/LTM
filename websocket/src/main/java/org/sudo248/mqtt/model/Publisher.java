package org.sudo248.mqtt.model;

import java.util.ArrayList;
import java.util.List;

public class Publisher implements Publishable{
    private final String topic;
    private final List<Subscriber> subscribers;

    public Publisher(String topic) {
        this.topic = topic;
        subscribers = new ArrayList<>();
    }

    public Publisher(String topic, List<Subscriber> subscribers) {
        this.topic = topic;
        this.subscribers = subscribers;
    }

    public String getTopic() {
        return topic;
    }

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    public void addSubscriber(Subscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    public void removeSubscriber(Subscriber subscriber) {
        this.subscribers.remove(subscriber);
    }

    public void connectSubscriber(Subscriber subscriber) {
        int size = subscribers.size();
        for (int i = 0; i < size; i++) {
            if (subscriber.equals(subscribers.get(i))) {

            }
        }
    }

    public boolean isValidSubscriber(Subscriber subscriber) {
        for (Subscriber sub : this.subscribers) {
            if (subscriber.equals(sub)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void publish(MqttMessage message) {
        for (Subscriber subscriber : this.subscribers) {
            if (subscriber.isOpen()) {
                subscriber.getWebSocket().send(message);
            }
        }
    }
}
