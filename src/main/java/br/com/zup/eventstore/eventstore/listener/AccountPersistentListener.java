package br.com.zup.eventstore.eventstore.listener;

import com.github.msemys.esjc.PersistentSubscription;
import com.github.msemys.esjc.PersistentSubscriptionListener;
import com.github.msemys.esjc.ResolvedEvent;
import com.github.msemys.esjc.SubscriptionDropReason;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import static br.com.zup.eventstore.eventstore.util.EventUtil.printResolvedEvent;

@Log4j2
@Component
public class AccountPersistentListener implements PersistentSubscriptionListener {

    @Override
    public void onEvent(PersistentSubscription subscription, ResolvedEvent event) {
        log.info("\n\n\tAccountPersistentListener: {}", event.originalEvent().eventType);
        printResolvedEvent(event);
    }

    @Override
    public void onClose(PersistentSubscription subscription, SubscriptionDropReason reason, Exception exception) {
        log.info("AccountPersistentListener Subscription closed {} \n", reason, exception);
    }

}
