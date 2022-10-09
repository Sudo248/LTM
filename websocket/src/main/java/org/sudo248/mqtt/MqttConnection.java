package org.sudo248.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sudo248.WebSocket;
import org.sudo248.mqtt.model.MqttMessage;
import org.sudo248.mqtt.model.MqttMessageType;
import org.sudo248.mqtt.model.Publisher;
import org.sudo248.mqtt.model.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MqttConnection {
    private final Logger log = LoggerFactory.getLogger(MqttConnection.class);
    private final Map<String, Publisher> publishers;

    private final MqttListener listener;

    public MqttConnection(MqttListener listener) {
        this.publishers = new HashMap<>();
        this.listener = listener;
    }

    public MqttConnection(Map<String, Publisher> publishers, MqttListener listener) {
        this.publishers = publishers;
        this.listener = listener;
    }

    public boolean createPublisher(String topic, List<Subscriber> subscribers) {
        if (isValidTopic(topic)) {
            return false;
        } else {
            Publisher publisher = new Publisher(topic, subscribers);
            publishers.put(topic, publisher);
            return true;
        }
    }

    public Publisher createPublisher(String topic) {
        if (isValidTopic(topic)) {
            return publishers.get(topic);
        } else {
            Publisher publisher = new Publisher(topic, new ArrayList<>());
            publishers.put(topic, publisher);
            return publisher;
        }
    }

    public void handleMessage(MqttMessage message, WebSocket ws) {
        MqttMessageType messageType = message.getType();
        switch (messageType) {
            case CONNECT:
                processConnect(message, ws);
                break;
            case PUBLISH:
                processPublish(message, ws);
                break;
            case SUBSCRIBE:
                processSubscribe(message, ws);
                break;
            case UNSUBSCRIBE:
                processUnSubscribe(message, ws);
                break;
            case DISCONNECT:
                processDisconnect(message, ws);
                break;
            default:
                String error = "Unknown MessageType: " + messageType;
                log.error(error);
                listener.onMqttError(message, error);
                break;
        }
    }

    private boolean isValidTopic(String topic) {
        return publishers.get(topic) != null;
    }

    private void processConnect(MqttMessage message, WebSocket ws) {
        log.info("processConnect: message" + message);
        Publisher publisher = publishers.get(message.getTopic());
        if (publisher != null) {
            long clientId = message.getClientId();
            for (Subscriber sub : publisher.getSubscribers()) {
                if (clientId == sub.getClientId()) {
                    sub.setValid(true);
                    sub.setWebSocket(ws);
                    break;
                }
            }
            listener.onMqttConnect(message);
        } else {
            String error = "Invalid publisher topic: " + message.getTopic();
            log.error(error);
            listener.onMqttError(message, error);
        }
    }

    private void processPublish(MqttMessage message, WebSocket ws) {
        log.info("processPublish: message" + message);
        Publisher publisher = publishers.get(message.getTopic());
        if (publisher != null) {
            listener.onMqttPublish(message);
            publisher.publish(message);
        } else {
            String error = "Invalid publisher topic: " + message.getTopic();
            log.error(error);
            listener.onMqttError(message, error);
        }
    }

    private void processSubscribe(MqttMessage message, WebSocket ws) {
        log.info("processSubscribe: message" + message);
        Publisher publisher = publishers.get(message.getTopic());
        if (publisher == null) {
            publisher = createPublisher(message.getTopic());
        }
        long clientId = message.getClientId();
        List<Subscriber> subscribers = publisher.getSubscribers();
        for (Subscriber sub : subscribers) {
            if (clientId == sub.getClientId()) {
                sub.setValid(true);
                sub.setWebSocket(ws);
                listener.onMqttSubscribe(message);
                return;
            }
        }
        subscribers.add(new Subscriber(
                message.getClientId(),
                ws,
                true
        ));
        listener.onMqttSubscribe(message);
    }

    private void processUnSubscribe(MqttMessage message, WebSocket ws) {
        log.info("processUnSubscribe: message" + message);
        Publisher publisher = publishers.get(message.getTopic());
        if (publisher != null) {
            long clientId = message.getClientId();
            List<Subscriber> subscribers = publisher.getSubscribers();
            for (Subscriber sub : subscribers) {
                if (clientId == sub.getClientId()) {
                    publisher.removeSubscriber(sub);
                    listener.onMqttUnSubscribe(message);
                    return;
                }
            }
            String error = "Invalid subscriber clientId: " + message.getClientId();
            log.error(error);
            listener.onMqttError(message, error);
        } else {
            String error = "Invalid publisher topic: " + message.getTopic();
            log.error(error);
            listener.onMqttError(message, error);
        }
    }

    private void processDisconnect(MqttMessage message, WebSocket ws) {
        log.info("processDisconnect: message" + message);
        Publisher publisher = publishers.get(message.getTopic());
        if (publisher != null) {
            long clientId = message.getClientId();
            List<Subscriber> subscribers = publisher.getSubscribers();
            for (Subscriber sub : subscribers) {
                if (clientId == sub.getClientId()) {
                    sub.setValid(false);
                    sub.setWebSocket(null);
                    listener.onMqttDisconnect(message);
                    return;
                }
            }
        } else {
            String error = "Invalid publisher topic: " + message.getTopic();
            log.error(error);
            listener.onMqttError(message, error);
        }
    }

    public void publish(String topic, MqttMessage message) {
        publishers.get(topic).publish(message);
    }
}
