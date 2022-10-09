package org.sudo248.mqtt.repository;

import org.sudo248.mqtt.model.Subscription;

import java.util.Set;

public interface SubscriptionRepository {
    Set<Subscription> listAllSubscriptions();
    void addNewSubscription(Subscription subscription);
    void removeSubscription(Subscription subscription);
}
