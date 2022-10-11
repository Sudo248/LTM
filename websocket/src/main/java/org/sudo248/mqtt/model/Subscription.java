package org.sudo248.mqtt.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class Subscription implements Serializable, Comparable<Subscription> {

    private static final long serialVersionUID = -7146699289397864999L;

    private final Long clientId;
    private final String topic;

    public Subscription(Long clientId, String topic) {
        this.clientId = clientId;
        this.topic = topic;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getTopic() {
        return topic;
    }

    protected Subscription copy() {
        return new Subscription(
                clientId,
                topic
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return clientId == that.clientId && Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, topic);
    }

    @Override
    public int compareTo(@NotNull Subscription subscription) {
        int idCompare = clientId.compareTo(subscription.clientId);
        if (idCompare != 0) {
            return idCompare;
        }
        return topic.compareTo(subscription.topic);
    }
}
