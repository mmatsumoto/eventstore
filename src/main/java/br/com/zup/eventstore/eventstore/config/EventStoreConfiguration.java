package br.com.zup.eventstore.eventstore.config;

import br.com.zup.eventstore.eventstore.listener.AccountPersistentListener;
import br.com.zup.eventstore.eventstore.listener.AccountVolatileListener;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;
import com.github.msemys.esjc.PersistentSubscriptionSettings;
import com.github.msemys.esjc.system.SystemConsumerStrategy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log4j2
@Configuration
public class EventStoreConfiguration {


    @Value("${eventstore.hostname:127.0.0.1}")
    private String hostname;

    @Value("${eventstore.port:1113}")
    private Integer port;

    @Value("${eventstore.login:admin}")
    private String login;

    @Value("${eventstore.password:changeit}")
    private String password;

    @Value("${eventstore.subscriber.group.name:AccountsGroup1}")
    private String subscriberGroupName;

    private List<CompletableFuture<? extends AutoCloseable>> subscriptions = new LinkedList<>();

    @Bean
    public EventStore eventStore() {
        return EventStoreBuilder.newBuilder()
                .singleNodeAddress(hostname, port)
                .userCredentials(login, password)
                .build();
    }

        @Autowired
    public void accountVolatileListener(EventStore eventStore, AccountVolatileListener accountVolatileListener) throws ExecutionException, InterruptedException {
        subscriptions.add(
                eventStore.subscribeToStream("accounts", false, accountVolatileListener));
    }


    @Autowired
    public void createPersistentAccountListenerAndSubscribe(EventStore eventStore, AccountPersistentListener accountPersistentListener) {
        final String streamName = "accounts";

        eventStore.createPersistentSubscription(streamName,
                                                subscriberGroupName,
                                                PersistentSubscriptionSettings.newBuilder()
                                                        .resolveLinkTos(true)
                                                        .historyBufferSize(20)
                                                        .checkPointAfter(Duration.ofSeconds(2))
                                                        .liveBufferSize(500)
                                                        .minCheckPointCount(10)
                                                        .maxCheckPointCount(1000)
                                                        .maxRetryCount(3)
                                                        .maxSubscriberCount(5)
                                                        .messageTimeout(Duration.ofSeconds(30))
                                                        .readBatchSize(10)
                                                        .startFromBeginning()
                                                        .timingStatistics(true)
                                                        .namedConsumerStrategy(SystemConsumerStrategy.ROUND_ROBIN)
                                                        .build())
                .whenComplete((createResult, error) -> {
                    if (error == null || error.getMessage().contains(streamName + " already exists")) {

                        log.info("PersistentSubscription created or already exists! stream={}, group={}", streamName, subscriberGroupName);
                        subscribeAccountPersistentListener(eventStore, accountPersistentListener);

                    } else {
                        log.error("Error creating PersistentSubscription stream={}, group={}", streamName, subscriberGroupName, error);
                    }
                });
    }


    private void subscribeAccountPersistentListener(EventStore eventStore,
                                                    AccountPersistentListener accountPersistentListener) {
        subscriptions.add(
                eventStore.subscribeToPersistent("accounts", subscriberGroupName, accountPersistentListener)
                        .whenComplete((persistentSubscription, error) -> {
                            log.info("\n\tPersistentAccountListener complete result={}, error={} ", persistentSubscription, error);
                        }));
    }


    @PreDestroy
    public void closeListeners() {
        log.info("Closing subscriptions connections number={}", subscriptions.size());
        subscriptions.forEach(s -> {
            try {
                s.get().close();
            } catch (Exception e) {
                log.error("Error trying to close the subscription connection. ", e);
            }
        });
    }

}
