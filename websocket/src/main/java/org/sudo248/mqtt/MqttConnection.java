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

    private final Map<Long, List<String>> subscriberTopic;

    private final MqttListener listener;

    public MqttConnection(MqttListener listener) {
        this(new HashMap<>(), listener, new HashMap<>());
    }

    public MqttConnection(Map<String, Publisher> publishers, MqttListener listener, Map<Long, List<String>> subscriberTopic) {
        this.publishers = publishers;
        this.listener = listener;
        this.subscriberTopic = subscriberTopic;
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
        Long clientId = message.getClientId();
        resubscribe(clientId, ws);
        listener.onMqttConnect(message);
        ws.send(message.copy("connected"));
    }

    private void processPublish(MqttMessage message, WebSocket ws) {
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
        Publisher publisher = publishers.get(message.getTopic());
        if (publisher == null) {
            publisher = createPublisher(message.getTopic());
        }
        long clientId = message.getClientId();
        List<Subscriber> subscribers = publisher.getSubscribers();
        for (Subscriber sub : subscribers) {
            if (clientId == sub.getClientId()) {
                sub.setWebSocket(ws);
                listener.onMqttSubscribe(message);
                return;
            }
        }
        subscribers.add(new Subscriber(
                message.getClientId(),
                ws
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
                    subscriberTopic.get(clientId).remove(message.getTopic());
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
        Publisher publisher = publishers.get(message.getTopic());
        if (publisher != null) {
            long clientId = message.getClientId();
            List<Subscriber> subscribers = publisher.getSubscribers();
            for (Subscriber sub : subscribers) {
                if (clientId == sub.getClientId()) {
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

    private void resubscribe(Long clientId, WebSocket ws) {
        for (String topic : publishers.keySet()) {
            resubscribeTopic(clientId, ws, topic);
        }
    }

    private void resubscribeTopic(Long clientId, WebSocket ws, String topic) {
        Publisher publisher = publishers.get(topic);
        if (publisher == null) {
            subscriberTopic.get(clientId).remove(topic);
        } else {
            List<Subscriber> subscribers = publisher.getSubscribers();
            for (Subscriber sub : subscribers) {
                if (clientId.equals(sub.getClientId())) {
                    sub.setWebSocket(ws);
                    return;
                }
            }
        }
    }

    public void removePublisher(String topic) {
        publishers.remove(topic);
    }

    public void publish(String topic, MqttMessage message) {
        publishers.get(topic).publish(message);
    }
}
