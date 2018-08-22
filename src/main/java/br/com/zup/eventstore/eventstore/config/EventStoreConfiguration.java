package br.com.zup.eventstore.eventstore.config;

import br.com.zup.eventstore.eventstore.listener.AccountVolatileListener;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;
import com.github.msemys.esjc.ResolvedEvent;
import com.github.msemys.esjc.Subscription;
import com.github.msemys.esjc.SubscriptionDropReason;
import com.github.msemys.esjc.VolatileSubscriptionListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean
    public EventStore eventStore() {
        return EventStoreBuilder.newBuilder()
                .singleNodeAddress(hostname, port)
                .userCredentials(login, password)
                .build();
    }

    @Autowired
    public void accountVolatileListener(EventStore eventStore, AccountVolatileListener accountVolatileListener) {
        eventStore.subscribeToStream("accounts", false, accountVolatileListener);
    }

}
