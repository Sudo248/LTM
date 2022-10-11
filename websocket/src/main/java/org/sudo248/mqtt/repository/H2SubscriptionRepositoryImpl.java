package org.sudo248.mqtt.repository;

import org.h2.mvstore.Cursor;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sudo248.mqtt.model.Subscription;

import java.util.HashSet;
import java.util.Set;

public class H2SubscriptionRepositoryImpl implements SubscriptionRepository {

    private final Logger log = LoggerFactory.getLogger(H2SubscriptionRepositoryImpl.class);

    private final String SUBSCRIPTIONS_MAP = "subscriptions";

    private MVMap<String, Subscription> subscriptions;

    public H2SubscriptionRepositoryImpl(MVStore mvStore) {
        this.subscriptions = mvStore.openMap(SUBSCRIPTIONS_MAP);
    }

    @Override
    public Set<Subscription> listAllSubscriptions() {
        log.debug("Retrieving existing subscriptions");
        Set<Subscription> results = new HashSet<>();
        Cursor<String, Subscription> mapCursor = subscriptions.cursor(null);
        while (mapCursor.hasNext()) {
            String subscriptionStr = mapCursor.next();
            results.add(mapCursor.getValue());
        }
        log.debug("Loaded {} subscriptions", results.size());
        return results;
    }

    @Override
    public void addNewSubscription(Subscription subscription) {
        subscriptions.put(subscription.getTopic() + "-" + subscription.getClientId(), subscription);
    }

    @Override
    public void removeSubscription(Subscription subscription) {
        subscriptions.remove(subscription.getTopic() + "-" + subscription.getClientId());
    }
}
