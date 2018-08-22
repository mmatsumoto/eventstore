package br.com.zup.eventstore.eventstore.config;

import br.com.zup.eventstore.eventstore.listener.AccountVolatileListener;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;
import com.github.msemys.esjc.Subscription;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
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

    private List<CompletableFuture<Subscription>> subscriptions = new LinkedList<>();


    @Bean
    public EventStore eventStore() {
        return EventStoreBuilder.newBuilder()
                .singleNodeAddress(hostname, port)
                .userCredentials(login, password)
                .build();
    }

    @Autowired
    public void accountVolatileListener(EventStore eventStore, AccountVolatileListener accountVolatileListener) throws ExecutionException, InterruptedException {
        subscriptions.add(eventStore.subscribeToStream("accounts", false, accountVolatileListener));
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
