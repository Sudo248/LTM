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

    public void getSubscriptionFromDb() {
        Set<Subscription> subscriptions = subscriptionRepository.listAllSubscriptions();
        for (Subscription sub : subscriptions) {
            updatePublisher(sub);
            updateSubscriberTopic(sub);
        }
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

    private void updatePublisher(Subscription subscription) {
        Publisher publisher = publishers.get(subscription.getTopic());
        if (publisher == null) {
            List<Subscriber> subscribers = new ArrayList<>();
            subscribers.add(new Subscriber(
                    subscription.getClientId(),
                    null
            ));
            publisher = new Publisher(subscription.getTopic(), subscribers);
            publishers.put(subscription.getTopic(), publisher);
        } else {
            publisher.getSubscribers().add(
                    new Subscriber(
                            subscription.getClientId(),
                            null
                    )
            );
        }
    }
}
