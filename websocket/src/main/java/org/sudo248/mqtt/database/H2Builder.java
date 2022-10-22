package org.sudo248.mqtt.database;

import org.h2.mvstore.MVStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sudo248.mqtt.repository.H2SubscriptionRepositoryImpl;
import org.sudo248.mqtt.repository.SubscriptionRepository;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class H2Builder {

    private final Logger log = LoggerFactory.getLogger(H2Builder.class);

    private final String storePath;

    private final int autoSaveInterval;

    private final ScheduledExecutorService scheduler;

    private MVStore mvStore;

    public H2Builder(String storePath, ScheduledExecutorService scheduler) {
        this.storePath = storePath;
        this.autoSaveInterval = 30;
        this.scheduler = scheduler;
    }

    public H2Builder initStore() {
        log.info("Initializing H2 store");
        if (storePath == null || storePath.isEmpty()) {
            throw new IllegalArgumentException("H2 store path can't be null or empty");
        }
        mvStore = new MVStore.Builder()
                .fileName(storePath+"/Mqtt-database.db")
                .autoCommitDisabled()
                .open();

        log.trace("Scheduling H2 commit task");
        scheduler.scheduleWithFixedDelay(() -> {
            log.trace("Committing to H2");
            mvStore.commit();
        }, autoSaveInterval, autoSaveInterval, TimeUnit.SECONDS);
        return this;
    }

    public SubscriptionRepository subscriptionRepository() {
        return new H2SubscriptionRepositoryImpl(mvStore);
    }

    public void closeStore() {
        mvStore.close();
    }
}
