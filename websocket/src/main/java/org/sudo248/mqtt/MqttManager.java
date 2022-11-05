package org.sudo248.mqtt;

import org.sudo248.mqtt.model.Publisher;
import org.sudo248.mqtt.model.Subscriber;
import org.sudo248.mqtt.model.Subscription;
import org.sudo248.mqtt.repository.SubscriptionRepository;

import java.util.*;

public class MqttManager {

    private final SubscriptionRepository subscriptionRepository;
    private final Map<String, Publisher> publishers;
    private final Map<Long, List<String>> subscriberTopic;

    public MqttManager(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
        publishers = new HashMap<>();
        subscriberTopic = new HashMap<>();
    }

    public Map<String, Publisher> getPublishers() {
        return publishers;
    }

    public Map<Long, List<String>> getSubscriberTopic() {
        return subscriberTopic;
    }

    public Map<Long, Subscriber> getSubscriptionFromDb() {
        Set<Subscription> subscriptions = subscriptionRepository.listAllSubscriptions();
        Map<Long, Subscriber> subscribers = new HashMap<>();
        for (Subscription subscription : subscriptions) {
            Subscriber subscriber = new Subscriber(
                    subscription.getClientId(),
                    null
            );
            subscribers.put(subscription.getClientId(), subscriber);
            updatePublisher(subscription.getTopic(), subscriber);
            updateSubscriberTopic(subscription);
        }
        return subscribers;
    }

    public void addSubscription(Subscription sub) {
        subscriptionRepository.addNewSubscription(sub);
    }

    public void removeSubscription(Subscription sub) {
        subscriptionRepository.removeSubscription(sub);
    }

    private void updateSubscriberTopic(Subscription subscription) {
        List<String> topics = subscriberTopic.get(subscription.getClientId());
        if (topics == null) {
            topics = new ArrayList<>();
            topics.add(subscription.getTopic());
            subscriberTopic.put(subscription.getClientId(), topics);
        } else {
            topics.add(subscription.getTopic());
        }
    }

    private void updatePublisher(String topic, Subscriber subscriber) {
        Publisher publisher = publishers.get(topic);
        if (publisher == null) {
            List<Subscriber> subscribers = new ArrayList<>();
            subscribers.add(subscriber);
            publisher = new Publisher(topic, subscribers);
            publishers.put(topic, publisher);
        } else {
            publisher.getSubscribers().add(subscriber);
        }
    }
}
