package br.com.zup.eventstore.eventstore;

import br.com.zup.eventstore.eventstore.event.AccountCreatedEvent;
import br.com.zup.eventstore.eventstore.event.MoneyDepositedEvent;
import br.com.zup.eventstore.eventstore.event.MoneyWithdrawEvent;
import br.com.zup.eventstore.eventstore.util.ResolvedEventUtil;
import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.ExpectedVersion;
import com.github.msemys.esjc.ResolvedEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static br.com.zup.eventstore.eventstore.util.JsonUtil.objectToJson;
import static java.util.Arrays.asList;

@SpringBootApplication
@Log4j2
public class EventstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventstoreApplication.class, args);
    }

    @Bean
    @Order(1000)
    public CommandLineRunner publishAccountEvents(EventStore eventStore) {
        return args -> {


            final String streamName = "accounts";

            final String accountId = UUID.randomUUID().toString();

            final HashMap metaData = new HashMap<String, String>() {{
                put("user", "user@zup.me");
            }};

            final List<EventData> events = asList(EventData.newBuilder()
                                                          .type(AccountCreatedEvent.class.getName())
                                                          .data(objectToJson(new AccountCreatedEvent(accountId, "Account " + accountId, 50L)))
                                                          .metadata(objectToJson(metaData))
                                                          .build(),

                                                  EventData.newBuilder()
                                                          .type(MoneyDepositedEvent.class.getName())
                                                          .data(objectToJson(new MoneyDepositedEvent(accountId, 50L)))
                                                          .metadata(objectToJson(metaData))
                                                          .build(),

                                                  EventData.newBuilder()
                                                          .type(MoneyWithdrawEvent.class.getName())
                                                          .data(objectToJson(new MoneyWithdrawEvent(accountId, 30L)))
                                                          .metadata(objectToJson(metaData))
                                                          .build());

            log.info("\n\nPublishing events to accounts stream");
            eventStore.appendToStream(streamName, ExpectedVersion.ANY, events)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("\tUnexpected error publishing the events={}", eventsToString(events), error);
                        } else {
                            log.info("\tEvent published events={}, position={}", eventsToString(events), result.logPosition);
                        }
                    })
                    .join();


        };
    }

    @Bean
    @Order(1001)
    public CommandLineRunner readingAllAccountsEvents(EventStore eventStore) {
        return args -> {
            log.info("\n\n\t Accounts: readStreamEventsForward");
            eventStore.readStreamEventsForward("accounts", 0L, 1000, false)
                    .join()
                    .events
                    .stream()
                    .sorted(Comparator.comparing(o -> o.event.eventNumber))
                    .forEach(ResolvedEventUtil::printResolvedEvent);
        };
    }

    @Bean
    @Order(1002)
    public CommandLineRunner streamAccountsEvents(EventStore eventStore) {
        return args -> {
            log.info("\n\n\t Accounts: streamAccountsEvents filtered by AccountCreatedEvent");
            eventStore.streamEventsForward("accounts", 0L, 1000, false)
                    .filter(e -> e.event.eventType.equals(AccountCreatedEvent.class.getName()))
                    .sorted(Comparator.comparing(o -> o.event.eventNumber))
                    .forEach(ResolvedEventUtil::printResolvedEvent);
        };
    }

    private String eventsToString(List<EventData> events) {
        return events.stream()
                .map(e -> new String(e.data))
                .collect(Collectors.toList())
                .toString();
    }

}
